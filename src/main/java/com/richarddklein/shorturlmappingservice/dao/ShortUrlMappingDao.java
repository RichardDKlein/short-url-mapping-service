/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dao;

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
     * Create an empty Short URL Mapping table in the repository.
     */
    void initializeShortUrlMappingRepository();
}
