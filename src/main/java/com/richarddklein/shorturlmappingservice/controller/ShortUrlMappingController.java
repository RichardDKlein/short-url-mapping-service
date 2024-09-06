/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import com.richarddklein.shorturlmappingservice.dto.*;
import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public interface ShortUrlMappingController {
    @PostMapping("/initialize-repository")
    ResponseEntity<Status>
    initializeShortUrlMappingRepository(ServerHttpRequest request);

    @PostMapping("/create-mapping")
    Mono<ResponseEntity<Status>>
    createMapping(@RequestBody ShortUrlMapping shortUrlMapping);

    @GetMapping("/get-mappings")
    Mono<ResponseEntity<StatusAndShortUrlMappingArray>>
    getMappings(@RequestBody ShortUrlMappingFilter shortUrlMappingFilter);

    @GetMapping("/{shortUrl}")
    Mono<ResponseEntity<?>>
    redirectShortUrlToLongUrl(@PathVariable String shortUrl);

    @PatchMapping("/change-long-url")
    Mono<ResponseEntity<Status>>
    changeLongUrl(@RequestBody ShortUrlAndLongUrl shortUrlAndLongUrl);

    @DeleteMapping("/delete-mappings")
    Mono<ResponseEntity<Status>>
    deleteMappings(@RequestBody ShortUrlMappingFilter shortUrlMappingFilter);
}
