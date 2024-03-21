/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.response;

import java.util.List;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;

/**
 * Class defining an HTTP Response containing a status code/message
 * as well as an array of Short URL Mapping entities.
 */
public class StatusAndShortUrlMappingArrayResponse {
    private StatusResponse status;
    private List<ShortUrlMapping> shortUrlMappings;

    /**
     * General constructor.
     *
     * @param status The status code/message to be embedded in the
     *               HTTP Response.
     * @param shortUrlMappings The array of Short URL Mapping entities
     *                         to be embedded in the HTTP Response.
     */
    public StatusAndShortUrlMappingArrayResponse(
            StatusResponse status,
            List<ShortUrlMapping> shortUrlMappings) {

        this.status = status;
        this.shortUrlMappings = shortUrlMappings;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public List<ShortUrlMapping> getShortUrlMappings() {
        return shortUrlMappings;
    }

    public void setShortUrlMappings(List<ShortUrlMapping> shortUrlMappings) {
        this.shortUrlMappings = shortUrlMappings;
    }

    @Override
    public String toString() {
        return "StatusAndShortUrlMappingArrayResponse{" +
                "status=" + status +
                ", shortUrlMappings=" + shortUrlMappings +
                '}';
    }
}
