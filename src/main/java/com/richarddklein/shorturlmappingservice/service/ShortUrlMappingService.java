/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.entity.ShortUrlMapping;
import reactor.core.publisher.Mono;

public interface ShortUrlMappingService {
    ShortUrlMappingStatus
    initializeShortUrlMappingRepository();

    Mono<ShortUrlMappingStatus>
    createMapping(ShortUrlMapping shortUrlMapping);

    Mono<StatusAndShortUrlMappingArray>
    getMappings(ShortUrlMappingFilter shortUrlMappingFilter);

    Mono<Status>
    changeLongUrl(ShortUrlAndLongUrl shortUrlAndLongUrl);

    Mono<Status>
    deleteMappings(ShortUrlMappingFilter shortUrlMappingFilter);
}
