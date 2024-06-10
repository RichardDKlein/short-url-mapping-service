/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service.shorturlmappingservice;

/**
 * The Short URL Reservation Status.
 *
 * An enumerated type describing the various possible statuses
 * that can be returned in response to a client request.
 */
public enum ShortUrlMappingStatus {
    SUCCESS,
    BAD_LONG_URL_SYNTAX,
    NO_LONG_URL_SPECIFIED,
    NO_SHORT_URL_IS_AVAILABLE,
    NO_SUCH_LONG_URL,
    NO_SUCH_MAPPING,
    NO_SUCH_SHORT_URL,
    NOT_ON_LOCAL_MACHINE,
    SHORT_URL_ALREADY_TAKEN,
    SHORT_URL_NOT_IN_USE,
    SHORT_URL_NOT_VALID,
    UNKNOWN_SHORT_URL_MAPPING_ERROR,
    UNKNOWN_SHORT_URL_RESERVATION_ERROR,
}
