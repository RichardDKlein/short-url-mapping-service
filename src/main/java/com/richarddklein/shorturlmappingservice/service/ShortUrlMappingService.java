/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.richarddklein.shorturlmappingservice.dto.ShortUrlMappingStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

public interface ShortUrlMappingService {
    ShortUrlMappingStatus
    initializeShortUrlMappingRepository(ServerHttpRequest request);
}
