/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.exception;

/**
 * The "No Short URLs Available" exception.
 *
 * Thrown when a client requests any available short URLs,
 * but none are available.
 */
public class NoShortUrlsAvailableException extends Exception {
    public NoShortUrlsAvailableException() {
        super("No Short URLs are available");
    }
}
