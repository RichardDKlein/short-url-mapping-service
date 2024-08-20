/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import com.richarddklein.shorturlmappingservice.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
public interface ShortUrlMappingController {
    @PostMapping("/initialize-repository")
    ResponseEntity<Status>
    initializeShortUrlMappingRepository(ServerHttpRequest request);
}
