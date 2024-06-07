/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.config;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreReader;
import com.richarddklein.shorturlmappingservice.client.shorturlreservationservice.ShortUrlReservationClient;
import com.richarddklein.shorturlmappingservice.client.shorturlreservationservice.ShortUrlReservationClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Client @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Client package.</p>
 */
@Configuration
public class ClientConfig {
    @Autowired
    ParameterStoreReader parameterStoreReader;

    @Bean
    public ShortUrlReservationClient
    shortUrlReservationClient() {
        return new ShortUrlReservationClientImpl(
                parameterStoreReader);
    }
}
