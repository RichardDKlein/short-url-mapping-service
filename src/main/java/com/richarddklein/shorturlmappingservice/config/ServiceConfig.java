/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.config;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreReader;
import com.richarddklein.shorturlmappingservice.service.shorturlreservationclient.ShortUrlReservationClient;
import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import com.richarddklein.shorturlmappingservice.service.shorturlreservationclient.ShortUrlReservationClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.richarddklein.shorturlmappingservice.service.ShortUrlMappingService;
import com.richarddklein.shorturlmappingservice.service.ShortUrlMappingServiceImpl;

/**
 * The Service @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Service package.</p>
 */
@Configuration
public class ServiceConfig {
    @Autowired
    ShortUrlMappingDao shortUrlMappingDao;

    @Autowired
    ParameterStoreReader parameterStoreReader;

    @Bean
    public ShortUrlMappingService
    shortUrlMappingService() {
        return new ShortUrlMappingServiceImpl(
                shortUrlMappingDao,
                shortUrlReservationClient());
    }

    @Bean
    public ShortUrlReservationClient
    shortUrlReservationClient() {
        return new ShortUrlReservationClientImpl(
                parameterStoreReader);
    }
}
