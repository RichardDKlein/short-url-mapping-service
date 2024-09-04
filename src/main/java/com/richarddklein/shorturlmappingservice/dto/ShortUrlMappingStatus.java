/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dto;

public enum ShortUrlMappingStatus {
    SUCCESS,
    BAD_LONG_URL_SYNTAX,
    MISSING_LONG_URL,
    MISSING_SHORT_URL,
    MISSING_USERNAME,
    NOT_ON_LOCAL_MACHINE,
    SHORT_URL_ALREADY_TAKEN,
    SHORT_URL_NOT_FOUND,
    UNKNOWN_ERROR,
}
