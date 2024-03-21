/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import java.util.List;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import com.richarddklein.shorturlmappingservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;

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
    public void initializeShortUrlReservationRepository() {
        shortUrlMappingDao.initializeShortUrlReservationRepository();
    }

    @Override
    public List<ShortUrlMapping> getAllShortUrlReservations() {
        return shortUrlMappingDao.getAllShortUrlReservations();
    }

    @Override
    public ShortUrlMapping getSpecificShortUrlReservation(String shortUrl) {
        return shortUrlMappingDao.getSpecificShortUrlReservation(shortUrl);
    }

    @Override
    public ShortUrlMapping reserveAnyShortUrl() throws NoShortUrlsAvailableException {
        return shortUrlMappingDao.reserveAnyShortUrl();
    }

    @Override
    public ShortUrlMappingStatus reserveSpecificShortUrl(String shortUrl) {
        return shortUrlMappingDao.reserveSpecificShortUrl(shortUrl);
    }

    @Override
    public void reserveAllShortUrls() {
        shortUrlMappingDao.reserveAllShortUrls();
    }

    @Override
    public ShortUrlMappingStatus cancelSpecificShortUrlReservation(String shortUrl) {
        return shortUrlMappingDao.cancelSpecificShortUrlReservation(shortUrl);
    }

    @Override
    public void cancelAllShortUrlReservations() {
        shortUrlMappingDao.cancelAllShortUrlReservations();
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
