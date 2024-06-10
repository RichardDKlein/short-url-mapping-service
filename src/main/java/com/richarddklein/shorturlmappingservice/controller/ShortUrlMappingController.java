/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import com.richarddklein.shorturlmappingservice.controller.response.StatusAndShortUrlMappingArrayResponse;
import com.richarddklein.shorturlmappingservice.controller.response.StatusAndShortUrlMappingResponse;
import com.richarddklein.shorturlmappingservice.controller.response.StatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;

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
     * @param request The HTTP Request.
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the database initialization operation.
     */
    @PostMapping("/dbinit")
    ResponseEntity<StatusResponse>
    initializeShortUrlMappingRepository(ServerHttpRequest request);

    /**
     * Create a short URL mapping.
     *
     * Create a new Short URL Mapping item in the Short URL Mapping
     * repository.
     *
     * @param request The HTTP Request sent by the client.
     * @param shortUrlMapping The new Short URL Mapping item to be
     *                        created. If the `shortUrl` property is
     *                        not specified, then the caller is willing
     *                        to accept any available short URL.
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the Short URL Mapping creation operation, and
     * the newly created Short URL Mapping item if the operation was
     * successful.
     */
    @PostMapping("")
    ResponseEntity<StatusAndShortUrlMappingResponse>
    createShortUrlMapping(ServerHttpRequest request,
                          @RequestBody ShortUrlMapping shortUrlMapping);

    /**
     * Get specific short URL mapping(s).
     *
     * Retrieve the specified Short URL Mapping item(s) from the Short URL
     * Mapping repository.
     *
     * @param shortUrlMapping Data structure specifying the Short URL
     *                        Mapping item(s) to be retrieved. Inside the
     *                        data structure, the caller may specify the
     *                        `shortUrl` or `longUrl` (or both) to be used
     *                        as the query parameter(s).
     * @return An HTTP Response Entity containing the status (success or
     * failure) of the Short URL Mapping retrieval operation, and the
     * retrieved Short URL Mapping item(s) if the operation was successful.
     */
    @GetMapping("/specific")
    ResponseEntity<StatusAndShortUrlMappingArrayResponse>
    getSpecificShortUrlMapping(@RequestBody ShortUrlMapping shortUrlMapping);

    /**
     * Get all short URL mappings.
     *
     * Retrieve all Short URL Mapping items from the Short URL Mapping
     * repository.
     *
     * @return An HTTP Response Entity containing the status (success or
     * failure) of the Short URL Mapping retrieval operation, and the
     * retrieved Short URL Mapping items if the operation was successful.
     */
    @GetMapping("/all")
    ResponseEntity<StatusAndShortUrlMappingArrayResponse>
    getAllShortUrlMappings();

    /**
     * Redirect a short URL to the corresponding long URL.
     *
     * Retrieve the specified Short URL Mapping item from the Short URL Mapping
     * repository, and perform a 302 (temporary) redirect to the corresponding
     * long URL. (Temporary because the mapping may change in the future.)
     *
     * @param shortUrl The short URL that is to be redirected.
     * @return On success, an HTTP Response Entity with an HTTP status code of
     * 302 (temporary redirect) and a Location header pointing to the corresponding
     * long URL. On failure, a Response Entity describing the reason for the failure.
     */
    @GetMapping("/{shortUrl}")
    ResponseEntity<?>
    redirectShortUrlToLongUrl(@PathVariable String shortUrl);

    /**
     * Update a long URL.
     *
     * @param shortUrl The short URL whose long URL is to be updated.
     * @param shortUrlMapping A data structure containing the new long URL.
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the update operation.
     */
    @PatchMapping("/{shortUrl}")
    ResponseEntity<StatusResponse>
    updateLongUrl(@PathVariable String shortUrl,
                  @RequestBody ShortUrlMapping shortUrlMapping);

    /**
     * Delete a Short URL Mapping item from the repository.
     *
     * @param request The HTTP Request sent by the client.
     * @param shortUrl The short URL property of the Short URL Mapping item to be
     *                 deleted from the repository.
     * @return An HTTP Response Entity containing the status (success or failure)
     * of the Short URL Mapping deletion operation, and the deleted Short URL
     * Mapping item if the operation was successful.
     */
    @DeleteMapping("/{shortUrl}")
    ResponseEntity<StatusAndShortUrlMappingResponse>
    deleteShortUrlMapping(ServerHttpRequest request, @PathVariable String shortUrl);

    /**
     * Delete all Short URL Mapping items from the repository.
     *
     * @param request The HTTP Request sent by the client.
     * @return An HTTP Response Entity containing the status (successor failure) of
     * the delete operation.
     */
    @DeleteMapping("/all")
    ResponseEntity<StatusResponse>
    deleteAllShortUrlMappings(ServerHttpRequest request);
}
