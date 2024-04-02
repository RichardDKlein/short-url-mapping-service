/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.response;

/**
 * The Short URL Reservation Status.
 *
 * An enumerated type describing the various possible statuses
 * that can be returned in response to a client request.
 */
public enum ShortUrlMappingStatus {
    SUCCESS,
    SHORT_URL_NOT_VALID,
    SHORT_URL_ALREADY_TAKEN,
    NO_SHORT_URL_IS_AVAILABLE,
    NO_LONG_URL_SPECIFIED,
    NOT_ON_LOCAL_MACHINE,
    UNKNOWN_SHORT_URL_RESERVATION_ERROR
}
