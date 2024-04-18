/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedResponse;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;

/**
 * The production implementation of the Short URL Mapping DAO interface.
 *
 * <p>This implementation uses a DynamoDB table, the Short URL Mapping table, to
 * store each Short URL Mapping item.</p>
 *
 * <p>For this table, eventual consistency is good enough. Therefore, the table can
 * (and should) be replicated across multiple, geographically dispersed, instances.
 *
 * <p>To increase throughput, DynamoDB will automatically shard (horizontally scale)
 * each instance of the table into multiple, disjoint partitions as the access frequency
 * increases, thereby ensuring acceptable throughput regardless of the user load.</p>
 *
 * <p>Each Short URL Mapping item in the table consists of just three attributes:
 * `shortUrl`, `longUrl`, and `version`.</p>
 *
 * <p>The `shortUrl` attribute of each Short URL Mapping item is the short URL itself.
 * This is a relatively short string that is unique: No two Short URL Mapping items
 * will contain the same `shortUrl` attribute. The `shortUrl` values are obtained
 * from, and managed by, the Short URL Reservation service. This service ensures that
 * the short URLs are unique.</p>
 *
 * <p>The `shortUrl` attribute is the Partition Key for each Short URL Mapping item.
 * Because it has a uniform hash distribution, it can be used to quickly locate the
 * database partition containing the corresponding Short URL Mapping item.</p>
 *
 * <p>The `longUrl` attribute of each Short URL Mapping item is the URL that the short
 * URL maps to. When the user attempts to visit the `shortUrl` web page, his browser
 * will be redirected to the `longUrl` web page. This allows the user to specify the
 * short URL in links where space is a premium, such as in `X` tweets.</p>
 *
 * <p>One of the use cases of the Short URL Mapping service is to retrieve the short
 * URL that maps to a given long URL, when the user has forgotten the former. To
 * handle this use case, we create a Global Secondary Index (GSI) with the `longUrl`
 * attribute as the Partition Key. This allows us to do a quick lookup of any Short
 * URL Mapping item given its long URL.</p>
 *
 * <p>The `version` attribute of each Short URL Mapping item is a long integer indicating
 * the version # of the Short URL Mapping entity. This attribute is for the exclusive use
 * of DynamoDB; the developer should not read or write it. DynamoDB uses the `version`
 * attribute for what it calls "optimistic locking".</p>
 *
 * <p>In the optimistic locking scheme, the code proceeds with a read-update-write
 * transaction under the assumption that most of the time the item will not be updated
 * by another user between the `read` and `write` operations. In the (hopefully rare)
 * situations where this is not the case, the `write` operation will fail, allowing
 * the code to retry with a new read-update-write transaction.</p>
 *
 * <p>DynamoDB uses the `version` attribute to detect when another user has updated
 * the same item concurrently. Every time the item is written to the database, DynamoDB
 * first checks whether the `version` attribute in the item is the same as the `version`
 * attribute in the database. If so, DynamoDB lets the `write` proceed, and updates the
 * `version` attribute in the database. If not, DynamoDB announces that the `write` has
 * failed.</p>
 */
@Repository
public class ShortUrlMappingDaoImpl implements ShortUrlMappingDao {
    private static final String LONG_URL_INDEX_NAME = "longUrl-index";
    private final ParameterStoreReader parameterStoreReader;
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<ShortUrlMapping> shortUrlMappingTable;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param parameterStoreReader Dependency injection of a class instance that
     *                             is to play the role of reading parameters from
     *                             the Parameter Store component of the AWS Simple
     *                             System Manager (SSM).
     * @param dynamoDbClient Dependency injection of a class instance that is to
     *                       play the role of a DynamoDB Client.
     * @param shortUrlMappingTable Dependency injection of a class instance that
     *                             is to model the Short URL Mapping table in
     *                             DynamoDB.
     */
    public ShortUrlMappingDaoImpl(
            ParameterStoreReader parameterStoreReader,
            DynamoDbClient dynamoDbClient,
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            DynamoDbTable<ShortUrlMapping> shortUrlMappingTable) {

        this.parameterStoreReader = parameterStoreReader;
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.shortUrlMappingTable = shortUrlMappingTable;
    }

    @Override
    public void initializeShortUrlMappingRepository() {
        if (doesTableExist()) {
            deleteShortUrlMappingTable();
        }
        createShortUrlMappingTable();
    }

    @Override
    public ShortUrlMappingStatus createShortUrlMapping(ShortUrlMapping shortUrlMapping) {
        PutItemEnhancedResponse<ShortUrlMapping> response =
                shortUrlMappingTable.putItemWithResponse(req -> req
                        .item(shortUrlMapping)
                        .conditionExpression(Expression.builder()
                                .expression("attribute_not_exists(shortUrl)")
                                .build())
                        .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL));
        return (response.consumedCapacity().capacityUnits() > 0) ?
                ShortUrlMappingStatus.SUCCESS :
                ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN; // should never happen
    }

    @Override
    public Object[] getSpecificShortUrlMappings(ShortUrlMapping shortUrlMapping) {
        Object[] result = new Object[2];

        String shortUrl = shortUrlMapping.getShortUrl();
        String longUrl = shortUrlMapping.getLongUrl();

        boolean isShortUrlSpecified = shortUrl != null && !shortUrl.isEmpty();
        boolean isLongUrlSpecified = longUrl != null && !longUrl.isEmpty();

        boolean isOnlyShortUrlSpecified = isShortUrlSpecified && !isLongUrlSpecified;
        boolean isOnlyLongUrlSpecified = isLongUrlSpecified && !isShortUrlSpecified;
        boolean areBothShortAndLongUrlsSpecified = isShortUrlSpecified && isLongUrlSpecified;

        List<ShortUrlMapping> matchingMappings = null;
        ShortUrlMappingStatus shortUrlMappingStatus = ShortUrlMappingStatus.SUCCESS;

        if (isOnlyShortUrlSpecified) {
            matchingMappings = findMatchingShortUrls();
            if (matchingMappings.isEmpty()) {
                shortUrlMappingStatus = ShortUrlMappingStatus.NO_SUCH_SHORT_URL;
            }
        } else if (isOnlyLongUrlSpecified) {
            matchingMappings = findMatchingLongUrls();
            if (matchingMappings.isEmpty()) {
                shortUrlMappingStatus = ShortUrlMappingStatus.NO_SUCH_LONG_URL;
            }
        } else if (areBothShortAndLongUrlsSpecified) {
            Set<ShortUrlMapping> matchingShortUrls = new HashSet<>(findMatchingShortUrls());
            Set<ShortUrlMapping> matchingLongUrls = new HashSet<>(findMatchingLongUrls());
            matchingShortUrls.retainAll(matchingLongUrls);
            matchingMappings = new ArrayList<>(matchingShortUrls);
            if (matchingMappings.isEmpty()) {
                shortUrlMappingStatus = ShortUrlMappingStatus.NO_SUCH_MAPPING;
            }
        }
        result[0] = shortUrlMappingStatus;
        result[1] = matchingMappings;
        return result;
// =====================================================================================
        if (shortUrl != null && !shortUrl.isEmpty()) {
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(shortUrl).build());
            SdkIterable<ShortUrlMapping> results = shortUrlMappingTable.query(
                    req -> req.queryConditional(queryConditional)).items();
            List<ShortUrlMapping> matchingShortUrls = new ArrayList<>();
            results.forEach(matchingShortUrls::add);
        }

        if (longUrl != null && !longUrl.isEmpty()) {
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(longUrl).build());
            DynamoDbIndex<ShortUrlMapping> gsiIndexOnLongUrl =
                    shortUrlMappingTable.index(LONG_URL_INDEX_NAME);
            SdkIterable<Page<ShortUrlMapping>> results = gsiIndexOnLongUrl.query(
                    req -> req.queryConditional(queryConditional));
            List<ShortUrlMapping> matchingLongUrls = new ArrayList<>();
            results.forEach(page -> page.items().forEach(matchingLongUrls::add));
        }
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Determine whether the Short URL Mapping table currently exists in
     * DynamoDB.
     *
     * @return `true` if the table currently exists, or `false` otherwise.
     */
    private boolean doesTableExist() {
        try {
            shortUrlMappingTable.describeTable();
        } catch (ResourceNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Delete the Short URL Mapping table from DynamoDB.
     */
    private void deleteShortUrlMappingTable() {
        System.out.print("Deleting the Short URL Mapping table ...");
        shortUrlMappingTable.deleteTable();
        DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build();
        waiter.waitUntilTableNotExists(builder -> builder
                .tableName(parameterStoreReader.getShortUrlMappingTableName())
                .build());
        waiter.close();
        System.out.println(" done!");
    }

    /**
     * Create the Short URL Mapping table in DynamoDB.
     */
    private void createShortUrlMappingTable() {
        System.out.print("Creating the Short URL Mapping table ...");
        CreateTableEnhancedRequest createTableRequest = CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(gsiBuilder -> gsiBuilder
                        .indexName("longUrl-index")
                        .projection(projectionBuilder -> projectionBuilder
                                .projectionType(ProjectionType.KEYS_ONLY))
                ).build();
        shortUrlMappingTable.createTable(createTableRequest);
        DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build();
        waiter.waitUntilTableExists(builder -> builder
                .tableName(parameterStoreReader.getShortUrlMappingTableName()).build());
        waiter.close();
        System.out.println(" done!");
    }
}
