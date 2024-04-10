/**
 * The Short URL Reservation Client
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richarddklein.shorturlmappingservice.dao.ParameterStoreReader;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ReserveAnyShortUrlApiResponse;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ReserveSpecificShortUrlApiResponse;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ShortUrlReservationResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
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
        ResponseEntity<String> responseEntity = null;
        responseEntity = restTemplate.exchange(
                url, HttpMethod.PUT, null, String.class);
        String responseBody = responseEntity.getBody();
        System.out.printf("====> responseBody = %s\n", responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        String reservationStatus = null;
        String reservationShortUrl = null;
        try {
            ReserveAnyShortUrlApiResponse reserveAnyShortUrlApiResponse =
                    objectMapper.readValue(responseBody, ReserveAnyShortUrlApiResponse.class);
            reservationStatus = reserveAnyShortUrlApiResponse.getStatus().getStatus();
            reservationShortUrl = reserveAnyShortUrlApiResponse.getShortUrlReservation().getShortUrl();
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

    @Override
    public ShortUrlReservationResult reserveSpecificShortUrl(boolean isRunningLocally,
                                                             String shortUrl) {
        String shortUrlReservationServiceBaseUrl =
                getShortUrlReservationServiceBaseUrl(isRunningLocally);
        String url = String.format("%s/reserve/specific/%s",
                shortUrlReservationServiceBaseUrl, shortUrl);
        System.out.printf("====> url = %s\n", url);
        ResponseEntity<String> responseEntity = null;
        String responseBody;
        try {
            responseEntity = restTemplate.exchange(
                    url, HttpMethod.PUT, null, String.class);
            responseBody = responseEntity.getBody();
        } catch (Exception e) {
            System.out.println("====> HERE I AM IN MY EXCEPTION HANDLER");
            System.out.println("====> " + e.getMessage());
            responseBody = e.getMessage().substring(e.getMessage().indexOf("{"));
            responseBody = responseBody.substring(0, responseBody.length() - 1);
        }
        System.out.printf("====> responseBody = %s\n", responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        String reservationStatus = null;
        try {
            ReserveSpecificShortUrlApiResponse reserveSpecificShortUrlApiResponse =
                    objectMapper.readValue(responseBody, ReserveSpecificShortUrlApiResponse.class);
            reservationStatus = reserveSpecificShortUrlApiResponse.getStatus().getStatus();
            System.out.printf("====> reservationStatus = %s\n", reservationStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reservationStatus == null) {
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR, null);
        }
        if (reservationStatus.equals("SHORT_URL_NOT_FOUND")) {
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.SHORT_URL_NOT_VALID, null);
        }
        if (reservationStatus.equals("SHORT_URL_FOUND_BUT_NOT_AVAILABLE")) {
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN, null);
        }
        if (!reservationStatus.equals("SUCCESS")) {
            return new ShortUrlReservationResult(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR, null);
        }
        return new ShortUrlReservationResult(ShortUrlMappingStatus.SUCCESS, shortUrl);
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
