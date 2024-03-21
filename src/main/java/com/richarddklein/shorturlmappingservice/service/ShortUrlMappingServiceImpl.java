/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import org.springframework.stereotype.Service;

import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;

/**
 * The production implementation of the Short URL Mapping Service interface.
 */
@Service
public class ShortUrlMappingServiceImpl implements ShortUrlMappingService {
    private final ShortUrlMappingDao shortUrlMappingDao;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param shortUrlMappingDao Dependency injection of a class instance that
     *                           is to play the role of the Short URL Mapping
     *                           Data Access Object (DAO).
     */
    public ShortUrlMappingServiceImpl(ShortUrlMappingDao shortUrlMappingDao) {
        this.shortUrlMappingDao = shortUrlMappingDao;
    }

    @Override
    public void initializeShortUrlMappingRepository() {
        shortUrlMappingDao.initializeShortUrlMappingRepository();
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
