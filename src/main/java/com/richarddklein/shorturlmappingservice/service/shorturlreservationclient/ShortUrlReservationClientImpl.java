/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service.shorturlreservationclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreReader;
import com.richarddklein.shorturlmappingservice.service.shorturlreservationclient.dto.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * The production implementation of the Short URL Reservation Client interface.
 */
public class ShortUrlReservationClientImpl implements ShortUrlReservationClient {
    private final ParameterStoreReader parameterStoreReader;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlReservationClientImpl(ParameterStoreReader parameterStoreReader) {
        this.parameterStoreReader = parameterStoreReader;
    }

    @Override
    public ShortUrlReservationResult reserveAnyShortUrl(boolean isRunningLocally) {
        String shortUrlReservationServiceBaseUrl =
                getShortUrlReservationServiceBaseUrl(isRunningLocally);

        WebClient webClient = WebClient.builder()
                .baseUrl(shortUrlReservationServiceBaseUrl)
                .build();

        Mono<String> responseMono = webClient.patch()
                .uri("/reserve/any")
                .retrieve()
                .bodyToMono(String.class);

        try {
            String responseBody = responseMono.block();
            ObjectMapper objectMapper = new ObjectMapper();
            ReserveAnyShortUrlApiResponse reserveAnyShortUrlApiResponse =
                    objectMapper.readValue(responseBody, ReserveAnyShortUrlApiResponse.class);

            ShortUrlReservationStatus reservationStatus = ShortUrlReservationStatus.valueOf(
                    reserveAnyShortUrlApiResponse.getStatus().getStatus());
            String reservationShortUrl =
                    reserveAnyShortUrlApiResponse.getShortUrlReservation().getShortUrl();

            return new ShortUrlReservationResult(reservationStatus, reservationShortUrl);

        } catch (Exception e) {
            System.out.printf("====> e.getMessage = %s\n", e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("404 Not Found")) {
                return new ShortUrlReservationResult(
                        ShortUrlReservationStatus.NO_SHORT_URL_IS_AVAILABLE, null);
            }
            return new ShortUrlReservationResult(ShortUrlReservationStatus.UNKNOWN, null);
        }
    }

    @Override
    public ShortUrlReservationStatus reserveSpecificShortUrl(boolean isRunningLocally,
                                                             String shortUrl) {
        String shortUrlReservationServiceBaseUrl =
                getShortUrlReservationServiceBaseUrl(isRunningLocally);

        WebClient webClient = WebClient.builder()
                .baseUrl(shortUrlReservationServiceBaseUrl)
                .build();

        Mono<String> responseMono = webClient.patch()
                .uri(String.format("/reserve/specific/%s", shortUrl))
                .retrieve()
                .bodyToMono(String.class);

        try {
            String responseBody = responseMono.block();
            ObjectMapper objectMapper = new ObjectMapper();
            ReserveSpecificShortUrlApiResponse reserveSpecificShortUrlApiResponse =
                    objectMapper.readValue(responseBody, ReserveSpecificShortUrlApiResponse.class);

            return ShortUrlReservationStatus.valueOf(
                    reserveSpecificShortUrlApiResponse.getStatus().getStatus());

        } catch (Exception e) {
            System.out.printf("====> e.getMessage = %s\n", e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("404 Not Found")) {
                return ShortUrlReservationStatus.SHORT_URL_NOT_FOUND;
            }
            if (e.getMessage().contains("409 Conflict")) {
                return ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_AVAILABLE;
            }
            return ShortUrlReservationStatus.UNKNOWN;
        }
    }

    @Override
    public ShortUrlReservationStatus cancelSpecificShortUrl(boolean isRunningLocally,
                                                            String shortUrl) {
        String shortUrlReservationServiceBaseUrl =
                getShortUrlReservationServiceBaseUrl(isRunningLocally);

        WebClient webClient = WebClient.builder()
                .baseUrl(shortUrlReservationServiceBaseUrl)
                .build();

        Mono<String> responseMono = webClient.patch()
                .uri(String.format("/cancel/specific/%s", shortUrl))
                .retrieve()
                .bodyToMono(String.class);

        try {
            String responseBody = responseMono.block();
            ObjectMapper objectMapper = new ObjectMapper();

            Status cancelSpecificShortUrlApiResponse =
                    objectMapper.readValue(responseBody, Status.class);
            String reservationStatus =
                    cancelSpecificShortUrlApiResponse.getStatus();

            return ShortUrlReservationStatus.valueOf(reservationStatus);

        } catch (Exception e) {
            System.out.printf("====> e.getMessage = %s\n", e.getMessage());
            return ShortUrlReservationStatus.UNKNOWN;
        }
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private String getShortUrlReservationServiceBaseUrl(boolean isRunningLocally) {
        return isRunningLocally ?
                parameterStoreReader.getShortUrlReservationServiceBaseUrlLocal() :
                parameterStoreReader.getShortUrlReservationServiceBaseUrlAws();
    }
}
