/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.richarddklein.shorturlmappingservice.dto.ShortUrlMappingFilter;
import com.richarddklein.shorturlmappingservice.dto.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.dto.StatusAndShortUrlMappingArray;
import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public interface ShortUrlMappingService {
    ShortUrlMappingStatus
    initializeShortUrlMappingRepository(ServerHttpRequest request);

    Mono<ShortUrlMappingStatus>
    createMapping(ShortUrlMapping shortUrlMapping);

    Mono<StatusAndShortUrlMappingArray>
    getMappings(ShortUrlMappingFilter shortUrlMappingFilter);
}
