/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dao;

import java.util.*;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import com.richarddklein.shorturlmappingservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;

/**
 * The production implementation of the Short URL Reservation DAO interface.
 *
 * <p>This implementation uses a DynamoDB table, the Short URL Reservation table, to
 * store each Short URL Reservation item.</p>
 *
 * <p>This table must be strongly consistent, since we cannot allow the possibility
 * that two different users might accidentally reserve the same short URL. Therefore,
 * the table cannot be replicated across multiple, geographically dispersed, instances;
 * there can be only one instance of the table.</p>
 *
 * <p>However, DynamoDB will automatically shard (horizontally scale) the table into
 * multiple, disjoint partitions as the access frequency increases, thereby ensuring
 * acceptable throughput regardless of the user load.</p>
 *
 * <p>Each Short URL Reservation item in the table consists of just three attributes:
 * `shortUrl`, `isAvailable`, and `version`.</p>
 *
 * <p>The `shortUrl` attribute of each Short URL Reservation item is the short URL
 * itself. This is a relatively short string that is unique: No two Short URL Reservation
 * items will contain the same `shortUrl` field. The `shortUrl` is an integer that has
 * been encoded using true base-64 encoding.</p>
 *
 * <p>Each character of the `shortUrl` string is a digit that can take on one of 64
 * possible values. Furthermore, the digits are weighted according to their position in
 * the `shortUrl` string: The rightmost digit is multiplied by 1, the next digit to the
 * left of it is multiplied by 64, the next digit to the left of that digit is multiplied
 * by (64 * 64), and so on.</p>
 *
 * <p>With this encoding scheme, a 5-character `shortUrl` can take on over 1 billion
 * unique values, a 6-character `shortUrl` can take on almost 69 billion unique values,
 * and a 7-character `shortUrl` can take on almost 4.4 trillion unique values.</p>
 *
 * <p>The 64 characters that compose the allowable values of each base-64 digit are '0'
 * thru '9', 'a' thru 'z', 'A' thru 'Z', and the characters '_' and '-'. All these
 * characters are legal URL characters that have no special meaning.</p>
 *
 * <p>Note that this base-64 encoding scheme is totally different from the Base64 encoding
 * that is used in HTML to encode binary data such as images.</p>
 *
 * <p>The `shortUrl` attribute is the Partition Key for each Short URL Reservation item.
 * Because it has a uniform hash distribution, it can be used to quickly locate the
 * database partition containing the corresponding Short URL Reservation item.</p>
 *
 * <p>The `isAvailable` field of each Short URL Reservation item indicates whether the
 * associated `shortUrl` is available. Our first inclination might be to make this a
 * Boolean attribute: A value of `true` would mean that the associated `shortUrl` is
 * available, while a value of `false` would mean that someone has already reserved
 * this `shortUrl`.</p>
 *
 * <p>However, this would lead to very poor performance of one of our most important
 * use cases: Finding an available short URL. If the `isAvailable` attribute were a
 * Boolean, then in order to find an available short URL, DynamoDB would have to scan
 * the entire table until it found an item whose `isAvailable` attribute had a value
 * of `true`. The inefficiency of this operation would be compounded by the fact that
 * DynamoDB might have to search multiple partitions until it found an available short
 * URL.</p>
 *
 * <p>To solve this problem, we use the concept of a DynamoDB "sparse index". We say
 * that the `isAvailable` attribute is present in a Short URL Reservation item if and
 * only if the corresponding `shortUrl` is available. If the `shortUrl` is not available,
 * then the `isAvailable` attribute is completely absent from the item. We then create
 * a Global Secondary Index (GSI), with the `isAvailable` attribute as the Partition
 * Key. Finding an available short URL is then simply a matter of looking up the first
 * item in the `isAvailable` GSI.</p>
 *
 * <p>The only remaining problem is this: What should we use as the value of the
 * `isAvailable` attribute? We cannot use a Boolean, because a Partition Key cannot be a
 * Boolean. To get around this restriction, we could use the Strings "T" and "F" instead
 * of the Boolean values `true` and `false`, but this would introduce another problem.
 * With only two possible values for `isAvailable`, DynamoDB's hashing of `isAvailable`
 * to locate the appropriate partition would basically be worthless. DynamoDB might have
 * to examine many partitions before finding an available short URL.</p>
 *
 * <p>To solve this remaining problem, we need to let `isAvailable` take on many possible
 * values, so that each value will hash efficiently to the appropriate partition. An easy
 * way to accomplish this is to set `isAvailable` to the same value as `shortUrl`. That is,
 * we say that when a short URL is available, then the corresponding `isAvailable` attribute
 * exists, and has a value equal to `shortUrl`. If a short URL is not available, then the
 * corresponding `isAvailable` attribute is completely absent.</p>
 *
 * <p>The `version` field of each Short URL Reservation item is a long integer indicating
 * the version # of the Short URL Reservation entity. This field is for the exclusive use
 * of DynamoDB; the developer should not read or write it. DynamoDB uses the `version`
 * field for what it calls "optimistic locking".</p>
 *
 * <p>In the optimistic locking scheme, the code proceeds with a read-update-write transaction
 * under the assumption that most of the time the item will not be updated by another user
 * between the `read` and `write` operations. In the (hopefully rare) situations where this
 * is not the case, the `write` operation will fail, allowing the code to retry with a new
 * read-update-write transaction.</p>
 *
 * <p>DynamoDB uses the `version` field to detect when another user has updated the same
 * item concurrently. Every time the item is written to the database, DynamoDB first checks
 * whether the `version` field in the item is the same as the `version` field in the database.
 * If so, DynamoDB lets the `write` proceed, and updates the `version` field in the database.
 * If not, DynamoDB announces that the `write` has failed.</p>
 *
 * <p>The Short URL Reservation table is fully populated with short URLs, and each short URL
 * is initialized as being available, before the service goes into production.</p>
 */
@Repository
public class ShortUrlMappingDaoImpl implements ShortUrlMappingDao {
    private static final String DIGITS =
            "0123456789" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "_-";
    private static final int BASE = DIGITS.length();

    private static final int MAX_BATCH_SIZE = 25;
    private static final int SCAN_LIMIT = 128;

    private final ParameterStoreReader parameterStoreReader;
    private final DynamoDbClient dynamoDbClient;
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
            DynamoDbTable<ShortUrlMapping> shortUrlMappingTable) {
        this.parameterStoreReader = parameterStoreReader;
        this.dynamoDbClient = dynamoDbClient;
        this.shortUrlMappingTable = shortUrlMappingTable;
    }

    @Override
    public void initializeShortUrlReservationRepository() {
        if (doesTableExist()) {
            deleteShortUrlMappingTable();
        }
        createShortUrlMappingTable();
        populateShortUrlMappingTable();
    }

    @Override
    public List<ShortUrlMapping> getAllShortUrlReservations() {
        List<ShortUrlMapping> result = new ArrayList<>();
        shortUrlMappingTable.scan(req -> req.consistentRead(true))
                .items().forEach(result::add);
        return result;
    }

    @Override
    public ShortUrlMapping getSpecificShortUrlReservation(String shortUrl) {
        return shortUrlMappingTable.getItem(req -> req
                .key(key -> key.partitionValue(shortUrl))
                .consistentRead(true));
    }

    @Override
    public ShortUrlMapping reserveAnyShortUrl() throws NoShortUrlsAvailableException {
        ShortUrlMapping updatedShortUrlMapping;
        do {
            ShortUrlMapping availableShortUrlMapping = findAvailableShortUrlMapping();
            availableShortUrlMapping.setIsAvailable(null);
            updatedShortUrlMapping = updateShortUrlMapping(availableShortUrlMapping);
        } while (updatedShortUrlMapping == null);
        return updatedShortUrlMapping;
    }

    @Override
    public ShortUrlMappingStatus reserveSpecificShortUrl(String shortUrl) {
        ShortUrlMapping updatedShortUrlMapping;
        do {
            ShortUrlMapping shortUrlMapping = getSpecificShortUrlReservation(shortUrl);
            if (shortUrlMapping == null) {
                return ShortUrlMappingStatus.SHORT_URL_NOT_FOUND;
            }
            if (!shortUrlMapping.isReallyAvailable()) {
                return ShortUrlMappingStatus.SHORT_URL_FOUND_BUT_NOT_AVAILABLE;
            }
            shortUrlMapping.setIsAvailable(null);
            updatedShortUrlMapping = updateShortUrlMapping(shortUrlMapping);
        } while (updatedShortUrlMapping == null);
        return ShortUrlMappingStatus.SUCCESS;
    }

    @Override
    public void reserveAllShortUrls() {
        SdkIterable<Page<ShortUrlMapping>> pagedResult =
                shortUrlMappingTable.scan(req -> req
                        .limit(SCAN_LIMIT)
                        .filterExpression(Expression.builder()
                                .expression("attribute_exists(isAvailable)")
                                .build())
                );

        for (Page<ShortUrlMapping> page : pagedResult) {
            for (ShortUrlMapping shortUrlMapping : page.items()) {
                shortUrlMapping.setIsAvailable(null);
                // Don't have to check for update failure, since we're in
                // system maintenance mode.
                updateShortUrlMapping(shortUrlMapping);
            }
        }
    }

    @Override
    public void cancelAllShortUrlReservations() {
        SdkIterable<Page<ShortUrlMapping>> pagedResult =
                shortUrlMappingTable.scan(req -> req
                        .limit(SCAN_LIMIT)
                        .filterExpression(Expression.builder()
                                .expression("attribute_not_exists(isAvailable)")
                                .build()));
        for (Page<ShortUrlMapping> page : pagedResult) {
            for (ShortUrlMapping shortUrlMapping : page.items()) {
                shortUrlMapping.setIsAvailable(shortUrlMapping.getShortUrl());
                // Don't have to check for update failure, since we're in
                // system maintenance mode.
                updateShortUrlMapping(shortUrlMapping);
            }
        }
    }

    @Override
    public ShortUrlMappingStatus cancelSpecificShortUrlReservation(String shortUrl) {
        ShortUrlMapping updatedShortUrlMapping;
        do {
            ShortUrlMapping shortUrlMapping = getSpecificShortUrlReservation(shortUrl);
            if (shortUrlMapping == null) {
                return ShortUrlMappingStatus.SHORT_URL_NOT_FOUND;
            }
            if (shortUrlMapping.isReallyAvailable()) {
                return ShortUrlMappingStatus.SHORT_URL_FOUND_BUT_NOT_RESERVED;
            }
            shortUrlMapping.setIsAvailable(shortUrl);
            updatedShortUrlMapping = updateShortUrlMapping(shortUrlMapping);
        } while (updatedShortUrlMapping == null);
        return ShortUrlMappingStatus.SUCCESS;
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
        System.out.print("Creating the Short URL Reservation table ...");
        CreateTableEnhancedRequest createTableRequest = CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(gsiBuilder -> gsiBuilder
                        .indexName("isAvailable-index")
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

    /**
     * Populate the Short URL Mapping table in DynamoDB.
     *
     * Create a Short URL Mapping item for each short URL in the range
     * specified in the Parameter Store, and mark all the items as being
     * available.
     */
    private void populateShortUrlMappingTable() {
        System.out.print("Populating the Short URL Mapping table ...");
        List<ShortUrlMapping> shortUrlMappings = new ArrayList<>();
        long minShortUrlBase10 = parameterStoreReader.getMinShortUrlBase10();
        long maxShortUrlBase10 = parameterStoreReader.getMaxShortUrlBase10();
        for (long i = minShortUrlBase10; i <= maxShortUrlBase10; i++) {
            String shortUrl = longToShortUrl(i);
            ShortUrlMapping shortUrlMapping = new ShortUrlMapping(shortUrl, shortUrl);
            shortUrlMapping.setVersion(1L);
            shortUrlMappings.add(shortUrlMapping);
        }
        batchInsertShortUrlMappings(shortUrlMappings);
        System.out.println(" done!");
    }

    /**
     * Convert a long integer to its base-64 representation.
     *
     * @param n The long integer of interest.
     * @return A string that is the base-64 representation of `n`.
     */
    private String longToShortUrl(long n) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(DIGITS.charAt((int) (n % BASE)));
            n /= BASE;
        } while (n > 0);
        return sb.reverse().toString();
    }

    /**
     * Batch insert some Short URL Mapping items.
     *
     * Into the Short URL Mapping table in DynamoDB, perform a
     * batch insert of a list of Short URL Mapping items. Insert
     * the items in batches rather than one at a time in order to
     * improve efficiency.
     *
     * @param shortUrlMappings The list of Short URL Mapping
     *                         items to be batch inserted.
     */
    private void batchInsertShortUrlMappings(List<ShortUrlMapping> shortUrlMappings) {
        long numItems = shortUrlMappings.size();
        for (int i = 0; i < numItems; i += MAX_BATCH_SIZE) {
            List<WriteRequest> writeRequests = new ArrayList<>();
            for (int j = i; j < Math.min(i + MAX_BATCH_SIZE, numItems); j++) {
                ShortUrlMapping shortUrlMapping = shortUrlMappings.get(j);
                WriteRequest writeRequest = WriteRequest.builder()
                        .putRequest(put -> put.item(shortUrlMapping.toAttributeValueMap()))
                        .build();
                writeRequests.add(writeRequest);
            }
            dynamoDbClient.batchWriteItem(req -> req.requestItems(Collections.singletonMap(
                    parameterStoreReader.getShortUrlMappingTableName(), writeRequests)));
        }
    }

    /**
     * Find an available Short URL Mapping item.
     *
     * In the Short URL Mapping table in DynamoDB, find an available
     * Short URL Mapping item. Use the General Secondary Index (GSI)
     * on the `isAvailable` attribute to avoid a time-consuming scan
     * operation.
     *
     * @return An available Short URl Mapping item.
     * @throws NoShortUrlsAvailableException If no short URLs are available,
     * i.e. if they are all reserved.
     */
    private ShortUrlMapping findAvailableShortUrlMapping() throws NoShortUrlsAvailableException {
        while (true) {
            // Get the first item from the `isAvailable-index` GSI.
            SdkIterable<Page<ShortUrlMapping>> pagedResult =
                    shortUrlMappingTable.index("isAvailable-index").scan(req -> req.limit(1));
            try {
                ShortUrlMapping gsiItem = pagedResult.iterator().next().items().get(0);
                ShortUrlMapping availableShortUrlMapping =
                        getSpecificShortUrlReservation(gsiItem.getShortUrl());
                // Since reads from any GSI, such as `isAvailable-index`, are not
                // strongly consistent, we should perform a manual consistency check,
                // to verify that the ShortUrlMapping we obtained from the
                // `isAvailable-index` really is available.
                if (!availableShortUrlMapping.isReallyAvailable()) {
                    continue;
                }
                return availableShortUrlMapping;
            } catch (NullPointerException e) {
                throw new NoShortUrlsAvailableException();
            }
        }
    }

    /**
     * Update a Short URL Mapping item.
     *
     * In the Short URL Mapping table in DynamoDB, update a specified
     * Short URL Mapping item.
     *
     * @param shortUrlMapping The Short URL Mapping item that is
     *                        to be used to update DynamoDB.
     * @return The updated Short URL Mapping item, or `null` if the
     * update failed. (The update can fail if someone else is updating
     * the same item concurrently.)
     */
    private ShortUrlMapping updateShortUrlMapping(ShortUrlMapping shortUrlMapping) {
        try {
            return shortUrlMappingTable.updateItem(req -> req.item(shortUrlMapping));
        } catch (ConditionalCheckFailedException e) {
            // Version check failed. Someone updated the ShortUrlMapping
            // item in the database after we read the item, so the item we
            // just tried to update contains stale data.
            System.out.println(e.getMessage());
            return null;
        }
    }
}
