/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;

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

    /**
     * Create a Short URL Mapping item.
     *
     * Create (add) a Short URL Mapping item in the Short URL Mapping
     * repository.
     *
     * @param isRunningLocally 'true' if the Short URL Mapping service is
     *                         running on your local machine, 'false'
     *                         otherwise.
     * @param shortUrlMapping The Short URL Mapping item to be created.
     * @return The success/failure status of the item creation operation.
     */
    ShortUrlMappingStatus createShortUrlMapping(
            boolean isRunningLocally, ShortUrlMapping shortUrlMapping);
}
