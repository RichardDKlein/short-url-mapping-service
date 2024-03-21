/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.entity;

import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * The Entity corresponding to an item in the Short URL Mappings
 * table in AWS DynamoDB.
 */
@DynamoDbBean
public class ShortUrlMapping {
    /**
     * The `shortUrl` attribute. See the `ShortUrlMappingDaoImpl`
     * Javadoc for a detailed description of this attribute.
     */
    private String shortUrl;

    /**
     * The `isAvailable` attribute. See the `ShortUrlMappingDaoImpl`
     * Javadoc for a detailed description of this attribute.
     */
    private String isAvailable;

    /**
     * The `version` attribute. See the `ShortUrlMappingDaoImpl`
     * Javadoc for a detailed description of this attribute.
     */
    private Long version;

    /**
     * Default constructor.
     *
     * This is not used by our code, but Spring requires it.
     */
    public ShortUrlMapping() {
    }

    /**
     * General constructor #1.
     *
     * Construct a Short URL Mapping entity from parameters specifying
     * the value of the `shortUrl` and `isAvailable` attributes.
     *
     * @param shortUrl The value of the `shortUrl` attribute.
     * @param isAvailable The value of the `isAvailable` attribute.
     */
    public ShortUrlMapping(String shortUrl, String isAvailable) {
        this.shortUrl = shortUrl;
        this.isAvailable = isAvailable;
    }

    /**
     * General constructor #2.
     *
     * Construct a Short URL Mapping entity from a Map containing
     * entries that specify the values of the `shortUrl` and `isAvailable`
     * attributes.
     *
     * @param item The Map containing the entries that specify the values
     *             of the `shortUrl` and `isAvailable` attributes.
     */
    public ShortUrlMapping(Map<String, AttributeValue> item) {
        shortUrl = item.get("shortUrl").s();
        isAvailable = item.get("isAvailable").s();
    }

    @DynamoDbPartitionKey
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @DynamoDbAttribute("isAvailable")
    @DynamoDbSecondaryPartitionKey(indexNames = "isAvailable-index")
    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    /**
     * Is this Short URL Mapping entity really available?
     *
     * This is a "transient" getter, i.e. a getter-like method that does not
     * correspond to an actual attribute in a Short URL Mapping item.
     *
     * Verify that this Short URL Mapping entity is really available, i.e.
     * that the `isAvailable` attribute exists and has the same value as the
     * `shortUrl` attribute.
     *
     * @return 'true' if this Short URL Mapping entity is really available,
     * 'false' otherwise.
     */
    @Transient
    public boolean isReallyAvailable() {
        return (isAvailable != null) && (isAvailable.equals(shortUrl));
    }

    @DynamoDbVersionAttribute
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Convert to an attribute-value map.
     *
     * Convert this Short URL Mapping entity to an attribute-value Map
     * containing entries that specify the values of the `shortUrl`, `isAvailable`,
     * and `version` attributes.
     *
     * @return the attribute-value Map corresponding to this Short URL Mapping
     * entity.
     */
    public Map<String, AttributeValue> toAttributeValueMap() {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put("shortUrl", AttributeValue.builder().s(shortUrl).build());
        attributeValues.put("isAvailable", AttributeValue.builder().s(isAvailable).build());
        attributeValues.put("version", AttributeValue.builder().n(version.toString()).build());

        return attributeValues;
    }

    @Override
    public String toString() {
        return "ShortUrlMapping{" +
                "shortUrl='" + shortUrl + '\'' +
                ", isAvailable='" + isAvailable + '\'' +
                ", version=" + version +
                '}';
    }
}
