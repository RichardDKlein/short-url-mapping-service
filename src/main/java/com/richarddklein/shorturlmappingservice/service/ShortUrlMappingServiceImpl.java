/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richarddklein.shorturlmappingservice.dao.ParameterStoreReader;
import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.response.shorturlreservationservice.ApiResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import org.springframework.web.client.RestTemplate;

/**
 * The production implementation of the Short URL Mapping Service interface.
 */
@Service
public class ShortUrlMappingServiceImpl implements ShortUrlMappingService {
    private final ShortUrlMappingDao shortUrlMappingDao;
    private final ParameterStoreReader parameterStoreReader;
    private final RestTemplate restTemplate;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param shortUrlMappingDao Dependency injection of a class instance that
     *                           is to play the role of the Short URL Mapping
     *                           Data Access Object (DAO).
     */
    public ShortUrlMappingServiceImpl(ShortUrlMappingDao shortUrlMappingDao,
                                      ParameterStoreReader parameterStoreReader,
                                      RestTemplate restTemplate) {
        this.shortUrlMappingDao = shortUrlMappingDao;
        this.parameterStoreReader = parameterStoreReader;
        this.restTemplate = restTemplate;
    }

    @Override
    public void initializeShortUrlMappingRepository() {
        shortUrlMappingDao.initializeShortUrlMappingRepository();
    }

    @Override
    public ShortUrlMappingStatus createShortUrlMapping(
            boolean isRunningLocally, ShortUrlMapping shortUrlMapping) {

        String shortUrl = shortUrlMapping.getShortUrl();
        String longUrl = shortUrlMapping.getLongUrl();

        if (longUrl == null) {
            return ShortUrlMappingStatus.NO_LONG_URL_SPECIFIED;
        }

        String shortUrlReservationServiceBaseUrl = isRunningLocally ?
                parameterStoreReader.getShortUrlReservationServiceBaseUrlLocal() :
                parameterStoreReader.getShortUrlReservationServiceBaseUrlAws();

        if (shortUrl == null) {
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
                return ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR;
            }
            if (reservationStatus.equals("NO_SHORT_URL_IS_AVAILABLE")) {
                return ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE;
            }
            if (!reservationStatus.equals("SUCCESS")) {
                return ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR;
            }
            ShortUrlMapping newMapping = new ShortUrlMapping(reservationShortUrl, longUrl);
/*
import org.springframework.http.ResponseEntity;

public class JsonParser {

    public static void main(String[] args) throws Exception {
        // Assume responseEntity is the ResponseEntity<String> returned by the producing microservice
        ResponseEntity<String> responseEntity = ...;

        // Extract JSON string from ResponseEntity
        String jsonResponse = responseEntity.getBody();

        // Now you have the JSON string, proceed with JSON parsing
        // Use Jackson's ObjectMapper to parse the JSON response into instances of your Java classes
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);

        // Access the desired fields from the parsed ApiResponse object
        System.out.println("Status: " + apiResponse.getStatus().getStatus());
        System.out.println("Message: " + apiResponse.getStatus().getMessage());
        System.out.println("Short URL: " + apiResponse.getShortUrlReservation().getShortUrl());
    }
}
 */
        }

        return null;
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
