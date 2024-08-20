/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dto;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;

@SuppressWarnings("unused")
public class StatusAndShortUrlMapping {
    private Status status;
    private ShortUrlMapping shortUrlMapping;

    public StatusAndShortUrlMapping() {
    }

    public StatusAndShortUrlMapping(Status status, ShortUrlMapping shortUrlMapping) {
        this.status = status;
        this.shortUrlMapping = shortUrlMapping;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ShortUrlMapping getShortUrlMapping() {
        return shortUrlMapping;
    }

    public void setShortUrlMapping(ShortUrlMapping shortUrlMapping) {
        this.shortUrlMapping = shortUrlMapping;
    }
}
