/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.response.shorturlreservationservice;

/**
 * Class defining the status returned by the Short URL Reservation service
 * in response to a request to reserve a short URL.
 */
public class Status {
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
