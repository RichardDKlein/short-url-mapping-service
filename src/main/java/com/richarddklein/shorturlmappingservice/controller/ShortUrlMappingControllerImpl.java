/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import com.richarddklein.shorturlmappingservice.response.StatusResponse;
import com.richarddklein.shorturlmappingservice.service.ShortUrlMappingService;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;

/**
 * The production implementation of the Short URL Mapping Controller
 * interface.
 */
@RestController
@RequestMapping({"/shorturl/mappings", "/"})
public class ShortUrlMappingControllerImpl implements ShortUrlMappingController {
    private final ShortUrlMappingService shortUrlMappingService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param shortUrlMappingService Dependency injection of a class instance
     *                               that is to play the role of the Short URL
     *                               Mapping service layer.
     */
    public ShortUrlMappingControllerImpl(
            ShortUrlMappingService shortUrlMappingService) {

        this.shortUrlMappingService = shortUrlMappingService;
    }

    @Override
    public ResponseEntity<StatusResponse>
    initializeShortUrlMappingRepository(HttpServletRequest request) {
        if (isRunningLocally(request.getRemoteAddr())) {
            shortUrlMappingService.initializeShortUrlMappingRepository();
            StatusResponse response = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    "Initialization of Short URL Mapping table "
                            + "completed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            StatusResponse response = new StatusResponse(
                    ShortUrlMappingStatus.NOT_ON_LOCAL_MACHINE,
                    "Initialization of the Short URL Mapping table "
                            + "can be done only when the service is running "
                            + "on your local machine");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<StatusResponse>
    createShortUrlMapping(@RequestBody ShortUrlMapping shortUrlMapping) {
        ShortUrlMappingStatus shortUrlMappingStatus =
                shortUrlMappingService.createShortUrlMapping(shortUrlMapping);

        String shortUrl = shortUrlMapping.getShortUrl();
        HttpStatus httpStatus;
        StatusResponse response;

        if (shortUrlMappingStatus == ShortUrlMappingStatus.SHORT_URL_NOT_VALID) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_NOT_VALID,
                    String.format("'%s' is not a valid short URL", shortUrl)
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN) {
            httpStatus = HttpStatus.CONFLICT;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN,
                    String.format("Short URL '%s' is already taken", shortUrl)
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE) {
            httpStatus = HttpStatus.NOT_FOUND;
            response = new StatusResponse(
                    ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE,
                    "No short URLs are available"
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.NO_LONG_URL_SPECIFIED) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new StatusResponse(
                    ShortUrlMappingStatus.NO_LONG_URL_SPECIFIED,
                    "You must specify a long URL"
            );
        } else {
            httpStatus = HttpStatus.OK;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    "Short URL Mapping item successfully created"
            );
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Is the service running locally?
     *
     * Determine whether the Short URL Mapping Service is running
     * on your local machine, or in the AWS cloud.
     *
     * @param remoteAddr The IP address of the machine that sent the
     *                   HTTP request.
     * @return 'true' if the service is running locally, 'false' otherwise.
     */
    private boolean isRunningLocally(String remoteAddr) {
        return remoteAddr.equals("127.0.0.1");
    }
}
