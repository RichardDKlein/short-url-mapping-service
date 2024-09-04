/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dto;

@SuppressWarnings("unused")
public class ShortUrlAndLongUrl {
    private String shortUrl;
    private String longUrl;

    public ShortUrlAndLongUrl() {
    }

    public ShortUrlAndLongUrl(String shortUrl, String longUrl) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
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
