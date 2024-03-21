/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.config;

import com.richarddklein.shorturlmappingservice.controller.ShortUrlMappingController;
import com.richarddklein.shorturlmappingservice.controller.ShortUrlMappingControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Controller @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Controller package.</p>
 */
@Configuration
public class ControllerConfig {
    @Autowired
    ServiceConfig serviceConfig;

    @Bean
    public ShortUrlMappingController
    shortUrlMappingController() {
        return new ShortUrlMappingControllerImpl(serviceConfig.shortUrlMappingService());
    }
}
