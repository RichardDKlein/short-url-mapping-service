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

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    url, HttpMethod.PUT, null, String.class);
        } catch (Exception e) {
            if (e.getMessage().contains("NO_SHORT_URL_IS_AVAILABLE")) {
                return new ShortUrlReservationResult(
                        ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE, null);
            }
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR, null);
        }

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        String reservationStatus = null;
        String reservationShortUrl = null;

        try {
            ReserveAnyShortUrlApiResponse reserveAnyShortUrlApiResponse =
                    objectMapper.readValue(responseBody, ReserveAnyShortUrlApiResponse.class);
            reservationStatus = reserveAnyShortUrlApiResponse.getStatus().getStatus();
            reservationShortUrl = reserveAnyShortUrlApiResponse.getShortUrlReservation().getShortUrl();
        } catch (Exception e) {  // should never happen
            e.printStackTrace();
        }

        if (!reservationStatus.equals("SUCCESS")) {
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR, null);
        }

        return new ShortUrlReservationResult(ShortUrlMappingStatus.SUCCESS, reservationShortUrl);
    }

    @Override
    public ShortUrlMappingStatus reserveSpecificShortUrl(boolean isRunningLocally,
                                                         String shortUrl) {
        String shortUrlReservationServiceBaseUrl =
                getShortUrlReservationServiceBaseUrl(isRunningLocally);
        String url = String.format("%s/reserve/specific/%s", shortUrlReservationServiceBaseUrl, shortUrl);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    url, HttpMethod.PUT, null, String.class);
        } catch (Exception e) {
            if (e.getMessage().contains("SHORT_URL_NOT_FOUND")) {
                return ShortUrlMappingStatus.SHORT_URL_NOT_VALID;
            }
            if (e.getMessage().contains("SHORT_URL_FOUND_BUT_NOT_AVAILABLE")) {
                return ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN;
            }
            return ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR;
        }

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        String reservationStatus = null;

        try {
            Status status = objectMapper.readValue(responseBody, Status.class);
            reservationStatus = status.getStatus();
        } catch (Exception e) {  // should never happen
            e.printStackTrace();
        }

        if (!reservationStatus.equals("SUCCESS")) {
            return ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR;
        }

        return ShortUrlMappingStatus.SUCCESS;
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
