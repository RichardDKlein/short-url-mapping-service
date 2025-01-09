/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.ShortUrlAndLongUrl;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.ShortUrlMappingFilter;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.Status;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.StatusAndShortUrlMappingArray;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.entity.ShortUrlMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public interface ShortUrlMappingController {
    @PostMapping("/initialize-repository")
    ResponseEntity<Status>
    initializeShortUrlMappingRepository();

    @PostMapping("/create-mapping")
    Mono<ResponseEntity<Status>>
    createMapping(@RequestBody ShortUrlMapping shortUrlMapping);

    @GetMapping("/get-mappings")
    Mono<ResponseEntity<StatusAndShortUrlMappingArray>>
    getMappings(@RequestBody ShortUrlMappingFilter shortUrlMappingFilter);

    @PatchMapping("/change-long-url")
    Mono<ResponseEntity<Status>>
    changeLongUrl(@RequestBody ShortUrlAndLongUrl shortUrlAndLongUrl);

    @DeleteMapping("/delete-mappings")
    Mono<ResponseEntity<Status>>
    deleteMappings(@RequestBody ShortUrlMappingFilter shortUrlMappingFilter);
}
