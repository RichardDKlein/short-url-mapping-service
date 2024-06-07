/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service.shorturlreservationclient.dto;

/**
 * Class defining the result of requesting the Short URL Reservation service
 * to reserve a short URL.
 */
public class ShortUrlReservationResult {
    public ShortUrlReservationStatus status;
    public String shortUrl;

    public ShortUrlReservationResult(ShortUrlReservationStatus status, String shortUrl) {
        this.status = status;
        this.shortUrl = shortUrl;
    }
}
