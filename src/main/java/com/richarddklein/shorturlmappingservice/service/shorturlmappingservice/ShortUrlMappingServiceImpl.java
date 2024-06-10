/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service.shorturlmappingservice;

import java.util.List;

import com.richarddklein.shorturlmappingservice.service.shorturlreservationclient.ShortUrlReservationClient;
import com.richarddklein.shorturlmappingservice.service.shorturlreservationclient.dto.ShortUrlReservationResult;
import com.richarddklein.shorturlmappingservice.service.shorturlreservationclient.dto.ShortUrlReservationStatus;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;

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
            if (shortUrlReservationResult.status != ShortUrlReservationStatus.SUCCESS) {
                return convertReservationStatusToMappingStatus(
                        shortUrlReservationResult.status);
            }
            shortUrlMapping.setShortUrl(shortUrlReservationResult.shortUrl);
        } else {
            ShortUrlReservationStatus shortUrlReservationStatus =
                    shortUrlReservationClient.reserveSpecificShortUrl(
                            isRunningLocally, shortUrl);
            if (shortUrlReservationStatus != ShortUrlReservationStatus.SUCCESS) {
                return convertReservationStatusToMappingStatus(shortUrlReservationStatus);
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
    public Object[] deleteShortUrlMapping(boolean isRunningLocally, String shortUrl) {
        ShortUrlMapping shortUrlMapping = new ShortUrlMapping();
        shortUrlMapping.setShortUrl(shortUrl);

        Object[] doesMappingExist =
                shortUrlMappingDao.getSpecificShortUrlMappings(shortUrlMapping);

        if ((ShortUrlMappingStatus)doesMappingExist[0] != ShortUrlMappingStatus.SUCCESS) {
            doesMappingExist[1] = null;
            return doesMappingExist;
        }

        ShortUrlReservationStatus shortUrlReservationStatus =
                shortUrlReservationClient.cancelSpecificShortUrl(isRunningLocally, shortUrl);

        if (shortUrlReservationStatus != ShortUrlReservationStatus.SUCCESS) {
            return new Object[] {convertReservationStatusToMappingStatus(
                    shortUrlReservationStatus), null};
        }
        return shortUrlMappingDao.deleteShortUrlMapping(shortUrl);
    }

    @Override
    public ShortUrlMappingStatus deleteAllShortUrlMappings(boolean isRunningLocally) {
        List<ShortUrlMapping> allItems = getAllShortUrlMappings();
        for (ShortUrlMapping item : allItems) {
            Object[] result = deleteShortUrlMapping(isRunningLocally, item.getShortUrl());
            ShortUrlMappingStatus shortUrlMappingStatus = (ShortUrlMappingStatus)result[0];
            if (shortUrlMappingStatus != ShortUrlMappingStatus.SUCCESS) {
                return ShortUrlMappingStatus.UNKNOWN_SHORT_URL_MAPPING_ERROR;
            }
        }
        return ShortUrlMappingStatus.SUCCESS;
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    ShortUrlMappingStatus convertReservationStatusToMappingStatus(
            ShortUrlReservationStatus shortUrlReservationStatus) {

        return switch (shortUrlReservationStatus) {
            case ShortUrlReservationStatus.SUCCESS ->
                    ShortUrlMappingStatus.SUCCESS;
            case ShortUrlReservationStatus.SHORT_URL_NOT_FOUND ->
                    ShortUrlMappingStatus.SHORT_URL_NOT_VALID;
            case ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_AVAILABLE ->
                    ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN;
            case ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_RESERVED ->
                    ShortUrlMappingStatus.SHORT_URL_NOT_IN_USE;
            case ShortUrlReservationStatus.NO_SHORT_URL_IS_AVAILABLE ->
                    ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE;
            case ShortUrlReservationStatus.NOT_ON_LOCAL_MACHINE ->
                    ShortUrlMappingStatus.NOT_ON_LOCAL_MACHINE;
            default ->
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR;
        };
    }
}
