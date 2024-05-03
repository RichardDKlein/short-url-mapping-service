/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.response.shorturlreservationservice;

/**
 * Class defining the HTTP Response sent by the Short URL Reservation service
 * in response to a request to reserve any available short URL.
 */
public class ReserveAnyShortUrlApiResponse {
    private Status status;
    private ShortUrlReservation shortUrlReservation;

    public Status getStatus() {
        return status;
    }

    public ShortUrlReservation getShortUrlReservation() {
        return shortUrlReservation;
    }
}
