/**
 * The Short URL Reservation Client
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ShortUrlReservationResult;

/**
 * The Short URL Reservation Client interface.
 *
 * <p>Specifies the methods that must be implemented by any class that plays
 * the role of a client of the Short URL Reservation Service.</p>
 */
public interface ShortUrlReservationClient {
    /**
     * Reserve any short URL.
     *
     * Request the Short URL Reservation Service to reserve any available
     * short URL.
     *
     * @param isRunningLocally 'true' if the Short URL Reservation Service
     *                         is running on your local machine, 'false'
     *                         otherwise.
     * @return The success/failure status of the reservation operation.
     */
    ShortUrlReservationResult reserveAnyShortUrl(boolean isRunningLocally);

    /**
     * Reserve specific short URL.
     *
     * Request the Short URL Reservation Service to reserve a specific
     * short URL.
     *
     * @param isRunningLocally 'true' if the Short URL Reservation Service
     *                         is running on your local machine, 'false'
     *                         otherwise.
     * @param shortUrl The specific short URL to be reserved.
     * @return The success/failure status of the reservation operation.
     */
    ShortUrlReservationResult reserveSpecificShortUrl(boolean isRunningLocally,
                                                      String shortUrl);
}
