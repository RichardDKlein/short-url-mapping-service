/**
 * The Short URL Reservation Client
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richarddklein.shorturlmappingservice.dao.ParameterStoreReader;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ApiResponse;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ShortUrlReservationResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
        String url = String.format("%s/reserve/any", shortUrlReservationServiceBaseUrl);
        System.out.printf("====> url = %s\n", url);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, HttpMethod.PUT, null, String.class);
        String responseBody = responseEntity.getBody();
        System.out.printf("====> responseBody = %s\n", responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        String reservationStatus = null;
        String reservationShortUrl = null;
        try {
            ApiResponse apiResponse =
                    objectMapper.readValue(responseBody, ApiResponse.class);
            reservationStatus = apiResponse.getStatus().getStatus();
            reservationShortUrl = apiResponse.getShortUrlReservation().getShortUrl();
            System.out.printf("====> reservationStatus = %s\n", reservationStatus);
            System.out.printf("====> reservationShortUrl = %s\n", reservationShortUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reservationStatus == null) {
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR, null);
        }
        if (reservationStatus.equals("NO_SHORT_URL_IS_AVAILABLE")) {
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE, null);
        }
        if (!reservationStatus.equals("SUCCESS")) {
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR, null);
        }
        return new ShortUrlReservationResult(ShortUrlMappingStatus.SUCCESS, reservationShortUrl);
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
