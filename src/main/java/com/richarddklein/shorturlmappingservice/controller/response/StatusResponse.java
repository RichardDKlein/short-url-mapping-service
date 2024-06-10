/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller.response;

import com.richarddklein.shorturlmappingservice.service.shorturlmappingservice.ShortUrlMappingStatus;

/**
 * Class defining an HTTP Response containing a status
 * code/message only.
 */
public class StatusResponse {
    private ShortUrlMappingStatus status;
    private String message;

    /**
     * General constructor.
     *
     * @param status The status code to be embedded in the HTTP Response.
     * @param message The status message to be embedded in the HTTP Response.
     */
    public StatusResponse(ShortUrlMappingStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ShortUrlMappingStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlMappingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
