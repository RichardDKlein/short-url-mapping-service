/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus.*;

@Service
public class ShortUrlMappingServiceImpl implements ShortUrlMappingService {
    private final ShortUrlMappingDao shortUrlMappingDao;
    private final HostUtils hostUtils;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlMappingServiceImpl(
            ShortUrlMappingDao shortUrlMappingDao,
            HostUtils hostUtils) {

        this.shortUrlMappingDao = shortUrlMappingDao;
        this.hostUtils = hostUtils;
    }

    // Initialization of the Short URL Mapping repository is performed rarely,
    // and then only by the Admin from a local machine. Therefore, we do not
    // need to use reactive (asynchronous) programming techniques here. Simple
    // synchronous logic will work just fine.
    @Override
    public ShortUrlStatus
    initializeShortUrlMappingRepository() {
        if (!hostUtils.isRunningLocally()) {
            return NOT_ON_LOCAL_MACHINE;
        }

        shortUrlMappingDao.initializeShortUrlMappingRepository();
        return SUCCESS;
    }

    @Override
    public Mono<ShortUrlStatus>
    createMapping(ShortUrlMapping shortUrlMapping) {
        String username = shortUrlMapping.getUsername();
        String shortUrl = shortUrlMapping.getShortUrl();
        String longUrl = shortUrlMapping.getLongUrl();

        if (username == null || username.isBlank()) {
            return Mono.just(MISSING_USERNAME);
        }
        if (shortUrl == null || shortUrl.isBlank()) {
            return Mono.just(MISSING_SHORT_URL);
        }
        if (longUrl == null || longUrl.isBlank()) {
            return Mono.just(MISSING_LONG_URL);
        }
        return shortUrlMappingDao.createMapping(shortUrlMapping);
    }

    @Override
    public Mono<StatusAndShortUrlMappingArray>
    getMappings(ShortUrlMappingFilter shortUrlMappingFilter) {
        String username = shortUrlMappingFilter.getUsername();
        String shortUrl = shortUrlMappingFilter.getShortUrl();
        String longUrl = shortUrlMappingFilter.getLongUrl();

        if (username == null || username.isBlank()) {
            return Mono.just(new StatusAndShortUrlMappingArray(
                    new Status(MISSING_USERNAME),
                    null));
        }
        if (shortUrl == null || shortUrl.isBlank()) {
            return Mono.just(new StatusAndShortUrlMappingArray(
                    new Status(MISSING_SHORT_URL),
                    null));
        }
        if (longUrl == null || longUrl.isBlank()) {
            return Mono.just(new StatusAndShortUrlMappingArray(
                    new Status(MISSING_LONG_URL),
                    null));
        }
        return shortUrlMappingDao.getMappings(shortUrlMappingFilter);
    }

    @Override
    public Mono<Status>
    changeLongUrl(ShortUrlAndLongUrl shortUrlAndLongUrl) {
        String shortUrl = shortUrlAndLongUrl.getShortUrl();
        String longUrl = shortUrlAndLongUrl.getLongUrl();

        if (shortUrl == null || shortUrl.isBlank()) {
            return Mono.just(new Status(MISSING_SHORT_URL));
        }
        if (longUrl == null || longUrl.isBlank()) {
            return Mono.just(new Status(MISSING_LONG_URL));
        }
        return shortUrlMappingDao.changeLongUrl(shortUrlAndLongUrl).map(Status::new);
    }

    @Override
    public Mono<Status>
    deleteMappings(ShortUrlMappingFilter shortUrlMappingFilter) {
        String username = shortUrlMappingFilter.getUsername();
        String shortUrl = shortUrlMappingFilter.getShortUrl();
        String longUrl = shortUrlMappingFilter.getLongUrl();

        if (username == null || username.isBlank()) {
            return Mono.just(new Status(MISSING_USERNAME));
        }
        if (shortUrl == null || shortUrl.isBlank()) {
            return Mono.just(new Status(MISSING_SHORT_URL));
        }
        if (longUrl == null || longUrl.isBlank()) {
            return Mono.just(new Status(MISSING_LONG_URL));
        }
        return shortUrlMappingDao.deleteMappings(shortUrlMappingFilter);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
