/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.client.shorturlreservationservice.dto;

/**
 * Class defining the Short URL Reservation object sent by the Short URL Reservation
 * service in response to a request to reserve a short URL.
 */
public class ShortUrlReservation {
    private String shortUrl;
    private String isAvailable;
    private int version;

    public String getShortUrl() {
        return shortUrl;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public int getVersion() {
        return version;
    }
}
