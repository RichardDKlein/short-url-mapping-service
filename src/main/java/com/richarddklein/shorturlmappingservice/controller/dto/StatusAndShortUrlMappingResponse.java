/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller.dto;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;

/**
 * Class defining an HTTP Response containing a status
 * code/message as well as a Short URL Mapping entity.
 */
public class StatusAndShortUrlMappingResponse {
    private StatusResponse status;
    private ShortUrlMapping shortUrlMapping;

    /**
     * General constructor.
     *
     * @param status The status code/message to be embedded
     *               in the HTTP Response.
     * @param shortUrlMapping The Short URL Mapping entity
     *                        to be embedded in the HTTP
     *                        Response.
     */
    public StatusAndShortUrlMappingResponse(
            StatusResponse status,
            ShortUrlMapping shortUrlMapping) {

        this.status = status;
        this.shortUrlMapping = shortUrlMapping;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public ShortUrlMapping getShortUrlMapping() {
        return shortUrlMapping;
    }

    public void setShortUrlMapping(ShortUrlMapping shortUrlMapping) {
        this.shortUrlMapping = shortUrlMapping;
    }

    @Override
    public String toString() {
        return "StatusAndShortUrlMappingResponse{" +
                "status=" + status +
                ", shortUrlMapping=" + shortUrlMapping +
                '}';
    }
}
