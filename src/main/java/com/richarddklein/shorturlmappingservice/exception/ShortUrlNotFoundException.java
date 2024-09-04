/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.exception;

public class ShortUrlNotFoundException extends Exception {
    public ShortUrlNotFoundException() {
        super("No such short URL");
    }
}
