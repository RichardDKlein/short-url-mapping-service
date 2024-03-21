/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

/**
 * The Short URL Mapping Service interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides service-layer functionality to the Short URL Mapping Service.</p>
 */
public interface ShortUrlMappingService {
    /**
     * Initialize the Short URL Mapping repository.
     *
     * <p></p>This is a synchronous method. It will return only when the
     * initialization has completed successfully, or has failed.</p>
     */
    void initializeShortUrlMappingRepository();
}
