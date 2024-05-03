/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;
import com.richarddklein.shorturlmappingservice.response.StatusAndShortUrlMappingArrayResponse;
import com.richarddklein.shorturlmappingservice.response.StatusAndShortUrlMappingResponse;
import com.richarddklein.shorturlmappingservice.response.StatusResponse;
import com.richarddklein.shorturlmappingservice.service.ShortUrlMappingService;

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
    initializeShortUrlMappingRepository(ServerHttpRequest request) {
        if (isRunningLocally(request.getRemoteAddress().getHostString())) {
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
    public ResponseEntity<StatusAndShortUrlMappingResponse>
    createShortUrlMapping(ServerHttpRequest request,
                          ShortUrlMapping shortUrlMapping) {

        ShortUrlMappingStatus shortUrlMappingStatus =
                shortUrlMappingService.createShortUrlMapping(
                        isRunningLocally(request.getRemoteAddress().getHostString()),
                        shortUrlMapping);

        String shortUrl = shortUrlMapping.getShortUrl();
        HttpStatus httpStatus;
        StatusResponse statusResponse;

        if (shortUrlMappingStatus == ShortUrlMappingStatus.SHORT_URL_NOT_VALID) {
            httpStatus = HttpStatus.BAD_REQUEST;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_NOT_VALID,
                    String.format("'%s' is not a valid short URL", shortUrl)
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN) {
            httpStatus = HttpStatus.CONFLICT;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_ALREADY_TAKEN,
                    String.format("Short URL '%s' is already taken", shortUrl)
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE) {
            httpStatus = HttpStatus.NOT_FOUND;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_SHORT_URL_IS_AVAILABLE,
                    "No short URLs are available"
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.NO_LONG_URL_SPECIFIED) {
            httpStatus = HttpStatus.BAD_REQUEST;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_LONG_URL_SPECIFIED,
                    "You must specify a long URL"
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_RESERVATION_ERROR,
                    "Unknown error while attempting to reserve a short URL"
            );
        } else {
            httpStatus = HttpStatus.OK;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    "Short URL Mapping item successfully created"
            );
        }

        StatusAndShortUrlMappingResponse statusAndShortUrlMappingResponse =
                new StatusAndShortUrlMappingResponse(statusResponse, shortUrlMapping);

        return new ResponseEntity<>(statusAndShortUrlMappingResponse, httpStatus);
    }

    @Override
    public ResponseEntity<StatusAndShortUrlMappingArrayResponse>
    getSpecificShortUrlMapping(ShortUrlMapping shortUrlMapping) {
        Object[] statusAndShortUrlMappings =
                shortUrlMappingService.getSpecificShortUrlMappings(shortUrlMapping);

        String shortUrl = shortUrlMapping.getShortUrl();
        String longUrl = shortUrlMapping.getLongUrl();
        HttpStatus httpStatus;
        StatusResponse statusResponse;
        ShortUrlMappingStatus shortUrlMappingStatus =
                (ShortUrlMappingStatus)statusAndShortUrlMappings[0];
        List<ShortUrlMapping> shortUrlMappings =
                (List<ShortUrlMapping>)statusAndShortUrlMappings[1];

        if (shortUrlMappingStatus == ShortUrlMappingStatus.NO_SUCH_SHORT_URL) {
            httpStatus = HttpStatus.NOT_FOUND;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_SUCH_SHORT_URL,
                    String.format("Short URL '%s' was not found", shortUrl)
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.NO_SUCH_LONG_URL) {
            httpStatus = HttpStatus.NOT_FOUND;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_SUCH_LONG_URL,
                    String.format("Long URL '%s' was not found", longUrl)
            );
        } else if (shortUrlMappingStatus ==
                ShortUrlMappingStatus.NO_SUCH_MAPPING) {
            httpStatus = HttpStatus.NOT_FOUND;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_SUCH_MAPPING,
                    String.format("Mapping '%s' => '%s' was not found", shortUrl, longUrl)
            );
        } else {
            httpStatus = HttpStatus.OK;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    "Short URL Mapping item(s) successfully retrieved"
            );
        }

        StatusAndShortUrlMappingArrayResponse statusAndShortUrlMappingArrayResponse =
                new StatusAndShortUrlMappingArrayResponse(statusResponse, shortUrlMappings);

        return new ResponseEntity<>(statusAndShortUrlMappingArrayResponse, httpStatus);
    }

    @Override
    public ResponseEntity<StatusAndShortUrlMappingArrayResponse> getAllShortUrlMappings() {
        List<ShortUrlMapping> shortUrlMappings =
                shortUrlMappingService.getAllShortUrlMappings();
        StatusResponse status = new StatusResponse(
                ShortUrlMappingStatus.SUCCESS,
                "All short URL mappings successfully retrieved");
        StatusAndShortUrlMappingArrayResponse response =
                new StatusAndShortUrlMappingArrayResponse(status, shortUrlMappings);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> redirectShortUrlToLongUrl(String shortUrl) {
        ShortUrlMapping shortUrlMapping = new ShortUrlMapping(shortUrl, "");

        Object[] statusAndShortUrlMappings =
                shortUrlMappingService.getSpecificShortUrlMappings(shortUrlMapping);

        HttpStatus httpStatus;
        StatusResponse statusResponse;
        ShortUrlMappingStatus shortUrlMappingStatus =
                (ShortUrlMappingStatus)statusAndShortUrlMappings[0];
        List<ShortUrlMapping> shortUrlMappings =
                (List<ShortUrlMapping>)statusAndShortUrlMappings[1];

        if (shortUrlMappingStatus == ShortUrlMappingStatus.NO_SUCH_SHORT_URL) {
            httpStatus = HttpStatus.NOT_FOUND;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_SUCH_SHORT_URL,
                    String.format("Short URL '%s' was not found", shortUrl)
            );
            return new ResponseEntity<StatusResponse>(statusResponse, httpStatus);
        }
        String longUrl = shortUrlMappings.get(0).getLongUrl();
        try {
            URI longUri = new URI(longUrl);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(longUri).build();
        } catch (URISyntaxException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.BAD_LONG_URL_SYNTAX,
                    String.format("Long URL '%s' has invalid syntax", longUrl)
            );
            return new ResponseEntity<StatusResponse>(statusResponse, httpStatus);
        }
    }

    @Override
    public ResponseEntity<StatusResponse>
    updateLongUrl(String shortUrl, ShortUrlMapping shortUrlMapping) {
        String newLongUrl = shortUrlMapping.getLongUrl();
        ShortUrlMappingStatus shortUrlMappingStatus =
                shortUrlMappingService.updateLongUrl(shortUrl, newLongUrl);

        HttpStatus httpStatus;
        StatusResponse statusResponse;

        if (shortUrlMappingStatus == ShortUrlMappingStatus.NO_SUCH_SHORT_URL) {
            httpStatus = HttpStatus.NOT_FOUND;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_SUCH_SHORT_URL,
                    String.format("Short URL '%s' was not found", shortUrl)
            );
        } else if (shortUrlMappingStatus == ShortUrlMappingStatus.NO_LONG_URL_SPECIFIED) {
            httpStatus = HttpStatus.BAD_REQUEST;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_LONG_URL_SPECIFIED,
                    "No new long URL was specified"
            );
        } else {
            httpStatus = HttpStatus.OK;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    "Long URL successfully updated"
            );
        }
        return new ResponseEntity<>(statusResponse, httpStatus);
    }

    @Override
    public ResponseEntity<StatusAndShortUrlMappingResponse>
    deleteShortUrlMapping(ServerHttpRequest request, String shortUrl) {
        Object[] statusAndShortUrlMapping =
                shortUrlMappingService.deleteShortUrlMapping(
                        isRunningLocally(request.getRemoteAddress().getHostString()),
                        shortUrl);

        HttpStatus httpStatus;
        StatusResponse statusResponse;
        ShortUrlMappingStatus shortUrlMappingStatus =
                (ShortUrlMappingStatus)statusAndShortUrlMapping[0];
        ShortUrlMapping shortUrlMapping =
                (ShortUrlMapping)statusAndShortUrlMapping[1];

        if (shortUrlMappingStatus == ShortUrlMappingStatus.NO_SUCH_SHORT_URL) {
            httpStatus = HttpStatus.NOT_FOUND;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.NO_SUCH_SHORT_URL,
                    String.format("Short URL '%s' was not found", shortUrl)
            );
        } else if (shortUrlMappingStatus == ShortUrlMappingStatus.SHORT_URL_NOT_IN_USE) {
            httpStatus = HttpStatus.CONFLICT;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.SHORT_URL_NOT_IN_USE,
                    String.format("Short URL '%s' is not currently in use", shortUrl)
            );
        } else if (shortUrlMappingStatus != ShortUrlMappingStatus.SUCCESS) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_MAPPING_ERROR,
                    "Unknown error while deleting Short URL Mapping item"
            );
        } else {
            httpStatus = HttpStatus.OK;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    "Short URL Mapping item successfully deleted"
            );
        }

        StatusAndShortUrlMappingResponse statusAndShortUrlMappingResponse =
                new StatusAndShortUrlMappingResponse(statusResponse, shortUrlMapping);

        return new ResponseEntity<>(statusAndShortUrlMappingResponse, httpStatus);
    }

    @Override
    public ResponseEntity<StatusResponse> deleteAllShortUrlMappings(
            ServerHttpRequest request) {

        ShortUrlMappingStatus shortUrlMappingStatus =
                shortUrlMappingService.deleteAllShortUrlMappings(
                        isRunningLocally(request.getRemoteAddress().getHostString()));

        HttpStatus httpStatus;
        StatusResponse statusResponse;

        if (shortUrlMappingStatus != ShortUrlMappingStatus.SUCCESS) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.UNKNOWN_SHORT_URL_MAPPING_ERROR,
                    "There was a problem deleting the Short URL Mapping items"
            );
        } else {
            httpStatus = HttpStatus.OK;
            statusResponse = new StatusResponse(
                    ShortUrlMappingStatus.SUCCESS,
                    "All Short URL Mapping items successfully deleted"
            );
        }
        return new ResponseEntity<>(statusResponse, httpStatus);
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
     * @param hostString The host that sent the HTTP request.
     * @return 'true' if the service is running locally, 'false' otherwise.
     */
    private boolean isRunningLocally(String hostString) {
        return hostString.contains("localhost");
    }
}
