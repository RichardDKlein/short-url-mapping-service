/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dao;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;

/**
 * The Short URL Mapping DAO (Data Access Object) interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides access to the data repository of the Short URL Mapping
 * Service.</p>
 */

public interface ShortUrlMappingDao {
    /**
     * Initialize the Short URL Reservation repository.
     *
     * Delete any existing Short URL Mapping table from the repository,
     * and create a new, empty one.
     */
    void initializeShortUrlMappingRepository();

    /**
     * Create a new Short URL Mapping item in the repository.
     *
     * @param shortUrlMapping The new Short URL Mapping item to be created.
     *                        If the `shortUrl` property is not specified,
     *                        then the caller is willing to accept any
     *                        available short URL.
     * @return A status code indicating the success/failure status of the
     * item creation operation.
     */
    ShortUrlMappingStatus createShortUrlMapping(ShortUrlMapping shortUrlMapping);

    /**
     * Get specific Short URL Mapping items from the repository.
     *
     * @param shortUrlMapping A data structure specifying the Short URL
     *                        Mapping item(s) to be retrieved. Inside the
     *                        data structure, the caller may specify the
     *                        `shortUrl` or `longUrl` (or both) to be used
     *                        as the query parameter(s).
     * @return An array consisting of two Objects. Objects[0] is a status
     * code (the enumerated type ShortUrlMappingStatus) indicating the
     * success/failure status of the retrieval operation. Objects[1] is a
     * List (possibly empty) of all the `ShortURLMapping` items matching
     * the query parameters specified in the `shortUrlMapping` parameter.
     */
    Object[] getSpecificShortUrlMappings(ShortUrlMapping shortUrlMapping);

    /**
     * Delete a Short URL Mapping item from the repository.
     *
     * @param shortUrl The short URL property of the Short URL Mapping
     *                 item to be deleted.
     * @return An array consisting of two Objects. Objects[0] is a status
     * code (the enumerated type ShortUrlMappingStatus) indicating the
     * success/failure status of the deletion operation. Objects[1] is the
     * Short URL Mapping item that was deleted, if the deletion operation
     * was successful. If the deletion was not successful, then Objects[1]
     * will be null.
     */
    Object[] deleteShortUrlMapping(String shortUrl);
}
