/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dto;

public enum ShortUrlMappingStatus {
    SUCCESS,
    NO_LONG_URL_SPECIFIED,
    NO_SHORT_URL_IS_AVAILABLE,
    NO_SUCH_LONG_URL,
    NO_SUCH_MAPPING,
    NO_SUCH_SHORT_URL,
    NOT_ON_LOCAL_MACHINE,
    SHORT_URL_ALREADY_TAKEN,
    SHORT_URL_NOT_IN_USE,
    SHORT_URL_NOT_VALID,
    UNKNOWN_ERROR,
}
