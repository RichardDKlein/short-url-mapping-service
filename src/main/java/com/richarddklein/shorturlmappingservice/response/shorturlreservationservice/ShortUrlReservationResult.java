/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.response.shorturlreservationservice;

import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;

/**
 * Class defining the result of requesting the Short URL Reservation service
 * to reserve a short URL.
 */
public class ShortUrlReservationResult {
    public ShortUrlMappingStatus status;
    public String shortUrl;

    public ShortUrlReservationResult(ShortUrlMappingStatus status, String shortUrl) {
        this.status = status;
        this.shortUrl = shortUrl;
    }
}
