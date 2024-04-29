/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ShortUrlReservationResult;


/**
 * The production implementation of the Short URL Mapping Service interface.
 */
@Service
public class ShortUrlMappingServiceImpl implements ShortUrlMappingService {
    private final ShortUrlMappingDao shortUrlMappingDao;
    private final ShortUrlReservationClient shortUrlReservationClient;

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
    public ShortUrlMappingServiceImpl(ShortUrlMappingDao shortUrlMappingDao,
                                      ShortUrlReservationClient shortUrlReservationClient) {
        this.shortUrlMappingDao = shortUrlMappingDao;
        this.shortUrlReservationClient = shortUrlReservationClient;
    }

    @Override
    public void initializeShortUrlMappingRepository() {
        shortUrlMappingDao.initializeShortUrlMappingRepository();
    }

    @Override
    public ShortUrlMappingStatus createShortUrlMapping(
            boolean isRunningLocally, ShortUrlMapping shortUrlMapping) {

        String shortUrl = shortUrlMapping.getShortUrl();
        String longUrl = shortUrlMapping.getLongUrl();

        if (longUrl == null) {
            return ShortUrlMappingStatus.NO_LONG_URL_SPECIFIED;
        }

        if (shortUrl == null) {
            ShortUrlReservationResult shortUrlReservationResult =
                    shortUrlReservationClient.reserveAnyShortUrl(isRunningLocally);
            if (shortUrlReservationResult.status != ShortUrlMappingStatus.SUCCESS) {
                return shortUrlReservationResult.status;
            }
            shortUrlMapping.setShortUrl(shortUrlReservationResult.shortUrl);
        } else {
            ShortUrlMappingStatus status = shortUrlReservationClient
                    .reserveSpecificShortUrl(isRunningLocally, shortUrl);
            if (status != ShortUrlMappingStatus.SUCCESS) {
                return status;
            }
        }
        return shortUrlMappingDao.createShortUrlMapping(shortUrlMapping);
    }

    @Override
    public Object[] getSpecificShortUrlMappings(ShortUrlMapping shortUrlMapping) {
        return shortUrlMappingDao.getSpecificShortUrlMappings(shortUrlMapping);
    }

    @Override
    public List<ShortUrlMapping> getAllShortUrlMappings() {
        return shortUrlMappingDao.getAllShortUrlMappings();
    }

    @Override
    public ShortUrlMappingStatus updateLongUrl(String shortUrl, String newLongUrl) {
        return shortUrlMappingDao.updateLongUrl(shortUrl, newLongUrl);
    }

    @Override
    public Object[] deleteShortUrlMapping(String shortUrl) {
        return shortUrlMappingDao.deleteShortUrlMapping(shortUrl);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
