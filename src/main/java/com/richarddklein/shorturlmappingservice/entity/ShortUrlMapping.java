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
 * The Entity corresponding to an item in the Short URL Mapping
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
     * The `longUrl` attribute. See the `ShortUrlMappingDaoImpl`
     * Javadoc for a detailed description of this attribute.
     */
    private String longUrl;

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
     * the value of the `shortUrl` and `longUrl` attributes.
     *
     * @param shortUrl The value of the `shortUrl` attribute.
     * @param longUrl The value of the `longUrl` attribute.
     */
    public ShortUrlMapping(String shortUrl, String longUrl) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    /**
     * General constructor #2.
     *
     * Construct a Short URL Mapping entity from a Map containing
     * entries that specify the values of the `shortUrl` and `longUrl`
     * attributes.
     *
     * @param item The Map containing the entries that specify the values
     *             of the `shortUrl` and `longUrl` attributes.
     */
    public ShortUrlMapping(Map<String, AttributeValue> item) {
        shortUrl = item.get("shortUrl").s();
        longUrl = item.get("longUrl").s();
    }

    @DynamoDbPartitionKey
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @DynamoDbAttribute("longUrl")
    @DynamoDbSecondaryPartitionKey(indexNames = "longUrl-index")
    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
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
     * containing entries that specify the values of the `shortUrl`,
     * `longUrl`, and `version` attributes.
     *
     * @return the attribute-value Map corresponding to this Short URL
     * Mapping entity.
     */
    public Map<String, AttributeValue> toAttributeValueMap() {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put("shortUrl", AttributeValue.builder().s(shortUrl).build());
        attributeValues.put("longUrl", AttributeValue.builder().s(longUrl).build());
        attributeValues.put("version", AttributeValue.builder().n(version.toString()).build());

        return attributeValues;
    }

    @Override
    public String toString() {
        return "ShortUrlMapping{" +
                "shortUrl='" + shortUrl + '\'' +
                ", longUrl='" + longUrl + '\'' +
                ", version=" + version +
                '}';
    }
}
