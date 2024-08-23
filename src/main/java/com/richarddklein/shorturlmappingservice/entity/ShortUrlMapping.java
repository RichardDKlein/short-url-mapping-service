/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

/**
 * The Entity corresponding to an item in the Short URL Mapping
 * table in AWS DynamoDB.
 */
@DynamoDbBean
public class ShortUrlMapping {
    /**
     * The Short URL Mapping item attributes. See the
     * `ShortUrlMappingDaoImpl` Javadoc for a detailed
     * description of these attributes.
     */
    private String username;
    private String shortUrl;
    private String longUrl;

    @JsonIgnore
    private Long version;

    /**
     * Default constructor.
     *
     * <p>This is not used by our code, but Spring requires it.</p>
     */
    public ShortUrlMapping() {
    }

    public ShortUrlMapping(String username, String shortUrl, String longUrl) {
        this.username = username;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    @DynamoDbAttribute("username")
    @DynamoDbSecondaryPartitionKey(indexNames = "username-index")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return "ShortUrlMapping{" +
                "username='" + username + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", longUrl='" + longUrl + '\'' +
                ", version=" + version +
                '}';
    }
}
