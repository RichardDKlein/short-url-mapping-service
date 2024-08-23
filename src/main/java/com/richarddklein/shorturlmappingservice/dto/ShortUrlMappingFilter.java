/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dto;

@SuppressWarnings("unused")
public class ShortUrlMappingFilter {
    private String username;
    private String shortUrl;
    private String longUrl;

    public ShortUrlMappingFilter() {
    }

    public ShortUrlMappingFilter(String username, String shortUrl, String longUrl) {
        this.username = username;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
