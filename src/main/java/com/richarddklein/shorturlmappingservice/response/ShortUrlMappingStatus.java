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
    SHORT_URL_NOT_FOUND,
    SHORT_URL_FOUND_BUT_NOT_AVAILABLE,
    SHORT_URL_FOUND_BUT_NOT_RESERVED,
    NO_SHORT_URL_IS_AVAILABLE,
    NOT_ON_LOCAL_MACHINE,
}
