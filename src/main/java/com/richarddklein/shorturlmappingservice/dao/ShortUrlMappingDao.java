/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dao;

import com.richarddklein.shorturlcommonlibrary.status.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.dto.*;
import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import reactor.core.publisher.Mono;

public interface ShortUrlMappingDao {
    void initializeShortUrlMappingRepository();

    Mono<ShortUrlMappingStatus>
    createMapping(ShortUrlMapping shortUrlUser);

    Mono<StatusAndShortUrlMappingArray>
    getMappings(ShortUrlMappingFilter shortUrlMappingFilter);

    Mono<ShortUrlMappingStatus>
    changeLongUrl(ShortUrlAndLongUrl shortUrlAndLongUrl);

    Mono<Status>
    deleteMappings(ShortUrlMappingFilter shortUrlMappingFilter);
}
