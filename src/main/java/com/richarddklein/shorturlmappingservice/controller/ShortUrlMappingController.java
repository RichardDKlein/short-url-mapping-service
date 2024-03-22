/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import com.richarddklein.shorturlmappingservice.response.StatusResponse;

/**
 * The Short URL Mapping Controller interface.
 *
 * <p>Specifies the REST API endpoints for the Short URL Mapping
 * Service.</p>
 */
public interface ShortUrlMappingController {
    /**
     * Initialize the Short URL Mapping repository.
     *
     * <p>This is a synchronous operation. It will return a response
     * to the client only when the database initialization has
     * completed successfully, or has failed.</p>
     *
     * <p>Because database initialization is a long-running operation
     * that exceeds the AWS API Gateway maximum response timeout of
     * 30 seconds, this REST endpoint is available only when the Short
     * URL Mapping Service is running on localhost, not on AWS.</p>
     *
     * @param request The HTTP Servlet Request object.
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the database initialization operation.
     */
    @PostMapping("/dbinit")
    ResponseEntity<StatusResponse>
    initializeShortUrlMappingRepository(HttpServletRequest request);

    /**
     * Create a short URL mapping.
     *
     * Create a new Short URL Mapping item in the Short URL Mapping
     * table.
     *
     * @param shortUrlMapping The new Short URL Mapping item to be
     *                        created.
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the Short URL Mapping creation operation.
     */
    @PostMapping("/")
    ResponseEntity<StatusResponse>
    createShortUrlMapping(@RequestBody ShortUrlMapping shortUrlMapping);
}
