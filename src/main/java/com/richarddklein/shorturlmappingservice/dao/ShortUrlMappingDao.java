/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dao;

import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import reactor.core.publisher.Mono;

public interface ShortUrlMappingDao {
    void initializeShortUrlMappingRepository();

    Mono<ShortUrlStatus>
    createMapping(ShortUrlMapping shortUrlUser);

    Mono<StatusAndShortUrlMappingArray>
    getMappings(ShortUrlMappingFilter shortUrlMappingFilter);

    Mono<ShortUrlStatus>
    changeLongUrl(ShortUrlAndLongUrl shortUrlAndLongUrl);

    Mono<Status>
    deleteMappings(ShortUrlMappingFilter shortUrlMappingFilter);
}
