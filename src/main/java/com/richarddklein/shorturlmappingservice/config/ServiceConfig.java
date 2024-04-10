/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.config;

import java.io.IOException;

import com.richarddklein.shorturlmappingservice.service.ShortUrlReservationClient;
import com.richarddklein.shorturlmappingservice.service.ShortUrlReservationClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

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
    public RestTemplate
    restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setErrorHandler(new ResponseErrorHandler() {
//            @Override
//            public boolean hasError(ClientHttpResponse response) throws IOException {
//                return !response.getStatusCode().is2xxSuccessful();
//            }
//
//            @Override
//            public void handleError(ClientHttpResponse response) throws IOException {
//                System.out.println("====> HERE I AM IN MY CUSTOM RESPONSE ERROR HANDLER");
//            }
//        });
        return restTemplate;
    }

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
        return new ShortUrlReservationClientImpl(
                daoConfig.parameterStoreReader(),
                restTemplate());
    }
}
