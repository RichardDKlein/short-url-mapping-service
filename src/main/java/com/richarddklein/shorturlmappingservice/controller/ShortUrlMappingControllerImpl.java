/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

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
                    "Initialization of Short URL Reservation table "
                            + "completed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            StatusResponse response = new StatusResponse(
                    ShortUrlMappingStatus.NOT_ON_LOCAL_MACHINE,
                    "Initialization of the Short URL Reservation "
                            + "table can be done only when the service is "
                            + "running on your local machine");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
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
