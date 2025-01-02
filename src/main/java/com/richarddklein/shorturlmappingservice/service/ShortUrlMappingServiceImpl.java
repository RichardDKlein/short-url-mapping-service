/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.richarddklein.shorturlcommonlibrary.security.util.HostUtils;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
    public ShortUrlMappingStatus
    initializeShortUrlMappingRepository(ServerHttpRequest request) {
        if (!hostUtils.isRunningLocally(request)) {
            return ShortUrlMappingStatus.NOT_ON_LOCAL_MACHINE;
        }

        shortUrlMappingDao.initializeShortUrlMappingRepository();
        return ShortUrlMappingStatus.SUCCESS;
    }

    @Override
    public Mono<ShortUrlMappingStatus>
    createMapping(ShortUrlMapping shortUrlMapping) {
        String username = shortUrlMapping.getUsername();
        String shortUrl = shortUrlMapping.getShortUrl();
        String longUrl = shortUrlMapping.getLongUrl();

        if (username == null || username.isBlank()) {
            return Mono.just(ShortUrlMappingStatus.MISSING_USERNAME);
        }
        if (shortUrl == null || shortUrl.isBlank()) {
            return Mono.just(ShortUrlMappingStatus.MISSING_SHORT_URL);
        }
        if (longUrl == null || longUrl.isBlank()) {
            return Mono.just(ShortUrlMappingStatus.MISSING_LONG_URL);
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
                    new Status(ShortUrlMappingStatus.MISSING_USERNAME),
                    null));
        }
        if (shortUrl == null || shortUrl.isBlank()) {
            return Mono.just(new StatusAndShortUrlMappingArray(
                    new Status(ShortUrlMappingStatus.MISSING_SHORT_URL),
                    null));
        }
        if (longUrl == null || longUrl.isBlank()) {
            return Mono.just(new StatusAndShortUrlMappingArray(
                    new Status(ShortUrlMappingStatus.MISSING_LONG_URL),
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
            return Mono.just(new Status(ShortUrlMappingStatus.MISSING_SHORT_URL));
        }
        if (longUrl == null || longUrl.isBlank()) {
            return Mono.just(new Status(ShortUrlMappingStatus.MISSING_LONG_URL));
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
            return Mono.just(new Status(ShortUrlMappingStatus.MISSING_USERNAME));
        }
        if (shortUrl == null || shortUrl.isBlank()) {
            return Mono.just(new Status(ShortUrlMappingStatus.MISSING_SHORT_URL));
        }
        if (longUrl == null || longUrl.isBlank()) {
            return Mono.just(new Status(ShortUrlMappingStatus.MISSING_LONG_URL));
        }
        return shortUrlMappingDao.deleteMappings(shortUrlMappingFilter);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
