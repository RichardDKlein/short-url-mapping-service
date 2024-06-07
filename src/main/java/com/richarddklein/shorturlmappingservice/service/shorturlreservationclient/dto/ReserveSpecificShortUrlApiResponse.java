/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service.shorturlreservationclient.dto;

/**
 * Class defining the HTTP Response sent by the Short URL Reservation service
 * in response to a request to reserve a specific short URL.
 */
public class ReserveSpecificShortUrlApiResponse {
    private Status status;

    public Status getStatus() {
        return status;
    }
}
