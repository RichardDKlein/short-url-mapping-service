/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import java.util.List;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.response.StatusAndShortUrlMappingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import com.richarddklein.shorturlmappingservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlmappingservice.response.StatusAndShortUrlMappingArrayResponse;
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
     *                                   that is to play the role of the Short URL
     *                                   Reservation service layer.
     */
    public ShortUrlMappingControllerImpl(
            ShortUrlMappingService shortUrlMappingService) {

        this.shortUrlMappingService = shortUrlMappingService;
    }

    @Override
    public ResponseEntity<StatusResponse>
    initializeShortUrlReservationTable(HttpServletRequest request) {
        if (isRunningLocally(request.getRemoteAddr())) {
            shortUrlMappingService.initializeShortUrlReservationRepository();
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

    @Override
    public ResponseEntity<StatusAndShortUrlMappingArrayResponse>
    getAllShortUrlReservations() {
        List<ShortUrlMapping> shortUrlMappings =
                shortUrlMappingService.getAllShortUrlReservations();
        StatusResponse status = new StatusResponse(
                ShortUrlMappingStatus.SUCCESS,
                "Short URL Reservation table successfully retrieved");
        StatusAndShortUrlMappingArrayResponse response =
                new StatusAndShortUrlMappingArrayResponse(status, shortUrlMappings);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StatusAndShortUrlMappingResponse>
    getSpecificShortUrlReservation(@PathVariable String shortUrl) {
        ShortUrlMapping shortUrlMapping =
                shortUrlMappingService.getSpecificShortUrlReservation(shortUrl);

        HttpStatus httpStatus;
        StatusResponse status;

        if (shortUrlMapping == null) {
            httpStatus = HttpStatus.NOT_FOUND;
            status = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_NOT_FOUND,
                    String.format("Short URL '%s' not found", shortUrl)
            );
            shortUrlMapping = new ShortUrlMapping(
                    shortUrl, "<not found>");
        } else {
            httpStatus = HttpStatus.OK;
            status = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    String.format("Short URL '%s' successfully retrieved", shortUrl)
            );
        }
        StatusAndShortUrlMappingResponse response =
                new StatusAndShortUrlMappingResponse(status, shortUrlMapping);

        return new ResponseEntity<>(response, httpStatus);
    }

    @Override
    public ResponseEntity<StatusAndShortUrlMappingResponse>
    reserveAnyShortUrl() {
        HttpStatus httpStatus;
        StatusResponse status;

        ShortUrlMapping shortUrlMapping;
        try {
            shortUrlMapping = shortUrlMappingService.reserveAnyShortUrl();

            httpStatus = HttpStatus.OK;
            status = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    String.format(
                            "Short URL '%s' successfully reserved",
                            shortUrlMapping.getShortUrl())
            );
        } catch (NoShortUrlsAvailableException e) {
            httpStatus = HttpStatus.NOT_FOUND;
            status = new StatusResponse(
                    ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE,
                    String.format("No short URLs are available")
            );
            shortUrlMapping = new ShortUrlMapping(
                    "<not found>",
                    "<not found>");
        }

        StatusAndShortUrlMappingResponse response =
                new StatusAndShortUrlMappingResponse(
                        status, shortUrlMapping);

        return new ResponseEntity<>(response, httpStatus);
    }

    @Override
    public ResponseEntity<StatusResponse>
    reserveSpecificShortUrl(@PathVariable String shortUrl) {
        ShortUrlMappingStatus shortUrlMappingStatus =
                shortUrlMappingService.reserveSpecificShortUrl(shortUrl);

        HttpStatus httpStatus;
        StatusResponse response;

        if (shortUrlMappingStatus == ShortUrlMappingStatus.SHORT_URL_NOT_FOUND) {
            httpStatus = HttpStatus.NOT_FOUND;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_NOT_FOUND,
                    String.format("Short URL '%s' not found", shortUrl)
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.SHORT_URL_FOUND_BUT_NOT_AVAILABLE) {

            httpStatus = HttpStatus.CONFLICT;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_FOUND_BUT_NOT_AVAILABLE,
                    String.format("Short URL '%s' was found, but is not available",
                            shortUrl)
            );
        } else {
            httpStatus = HttpStatus.OK;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    String.format("Short URL '%s' successfully reserved", shortUrl)
            );
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @Override
    public ResponseEntity<StatusResponse>
    reserveAllShortUrls() {
        shortUrlMappingService.reserveAllShortUrls();

        StatusResponse response = new StatusResponse(
                ShortUrlMappingStatus.SUCCESS,
                "All short URL reservations successfully reserved");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StatusResponse>
    cancelSpecificShortUrlReservation(@PathVariable String shortUrl) {
        ShortUrlMappingStatus shortUrlMappingStatus =
                shortUrlMappingService.cancelSpecificShortUrlReservation(shortUrl);

        HttpStatus httpStatus;
        StatusResponse response;

        if (shortUrlMappingStatus == ShortUrlMappingStatus.SHORT_URL_NOT_FOUND) {

            httpStatus = HttpStatus.NOT_FOUND;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_NOT_FOUND,
                    String.format("Short URL '%s' not found", shortUrl)
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.SHORT_URL_FOUND_BUT_NOT_RESERVED) {

            httpStatus = HttpStatus.CONFLICT;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_FOUND_BUT_NOT_RESERVED,
                    String.format("Short URL '%s' was found, but is not reserved", shortUrl)
            );
        } else {
            httpStatus = HttpStatus.OK;
            response = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    String.format("Short URL '%s' reservation successfully canceled", shortUrl)
            );
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    @Override
    public ResponseEntity<StatusResponse>
    cancelAllShortUrlReservations() {
        shortUrlMappingService.cancelAllShortUrlReservations();

        StatusResponse response = new StatusResponse(
                ShortUrlMappingStatus.SUCCESS,
                "All short URL reservations successfully canceled");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Is the service running locally?
     *
     * Determines whether the Short URL Mapping Service is running
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
