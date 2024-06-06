/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.config;

import com.richarddklein.shorturlwebclientlibrary.shorturlreservationservice.client.ShortUrlReservationClient;
import com.richarddklein.shorturlwebclientlibrary.shorturlreservationservice.client.ShortUrlReservationClientImpl;
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
    DaoConfig daoConfig;

    @Bean
    public ShortUrlMappingService
    shortUrlMappingService() {
        return new ShortUrlMappingServiceImpl(
                daoConfig.shortUrlMappingDao(),
                shortUrlReservationClient());
    }

    @Bean
    public ShortUrlReservationClient
    shortUrlReservationClient() {
        return new ShortUrlReservationClientImpl();
    }
}
