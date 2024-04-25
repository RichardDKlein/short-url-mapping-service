/**
 * The Short URL Reservation Client
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richarddklein.shorturlmappingservice.dao.ParameterStoreReader;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ReserveAnyShortUrlApiResponse;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ShortUrlReservationResult;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.Status;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * The production implementation of the Short URL Reservation Client interface.
 */
public class ShortUrlReservationClientImpl implements ShortUrlReservationClient {
    private final ParameterStoreReader parameterStoreReader;
    private final RestTemplate restTemplate;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     */
    public ShortUrlReservationClientImpl(ParameterStoreReader parameterStoreReader,
                                         RestTemplate restTemplate) {
        this.parameterStoreReader = parameterStoreReader;
        this.restTemplate = restTemplate;
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
            System.out.printf("====> responseBody = %s\n", responseBody);
            ObjectMapper objectMapper = new ObjectMapper();
            ReserveAnyShortUrlApiResponse reserveAnyShortUrlApiResponse =
                    objectMapper.readValue(responseBody, ReserveAnyShortUrlApiResponse.class);

            String reservationStatus =
                    reserveAnyShortUrlApiResponse.getStatus().getStatus();
            String reservationShortUrl =
                    reserveAnyShortUrlApiResponse.getShortUrlReservation().getShortUrl();

            if (!reservationStatus.equals("SUCCESS")) {
                return new ShortUrlReservationResult(ShortUrlMappingStatus
                        .UNKNOWN_SHORT_URL_RESERVATION_ERROR, null);
            }
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.SUCCESS, reservationShortUrl);

        } catch (Exception e) {
            System.out.printf("====> e.getMessage = %s\n", e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("404 Not Found")) {
                return new ShortUrlReservationResult(
                        ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE, null);
            }
            return new ShortUrlReservationResult(ShortUrlMappingStatus
                    .UNKNOWN_SHORT_URL_RESERVATION_ERROR, null);
        }
    }

    @Override
    public ShortUrlMappingStatus reserveSpecificShortUrl(boolean isRunningLocally,
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
            System.out.printf("====> responseBody = %s\n", responseBody);
            ObjectMapper objectMapper = new ObjectMapper();
            ReserveAnyShortUrlApiResponse reserveAnyShortUrlApiResponse =
                    objectMapper.readValue(responseBody, ReserveAnyShortUrlApiResponse.class);

            String reservationStatus =
                    reserveAnyShortUrlApiResponse.getStatus().getStatus();

            if (!reservationStatus.equals("SUCCESS")) {
                return ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR;
            }
            return ShortUrlMappingStatus.SUCCESS;

        } catch (Exception e) {
            System.out.printf("====> e.getMessage = %s\n", e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("404 Not Found")) {
                return ShortUrlMappingStatus.SHORT_URL_NOT_VALID;
            }
            if (e.getMessage().contains("409 Conflict")) {
                return ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN;
            }
            return ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR;
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
