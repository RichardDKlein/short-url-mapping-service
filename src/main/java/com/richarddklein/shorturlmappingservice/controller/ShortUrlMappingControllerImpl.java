/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.richarddklein.shorturlmappingservice.dto.*;
import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlmappingservice.service.ShortUrlMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlmappingservice.dto.ShortUrlMappingStatus.*;

@RestController
@RequestMapping({"/short-url/mappings", "/"})
public class ShortUrlMappingControllerImpl implements ShortUrlMappingController {
    private final ShortUrlMappingService shortUrlMappingService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlMappingControllerImpl(
            ShortUrlMappingService shortUrlMappingService) {

        this.shortUrlMappingService = shortUrlMappingService;
    }

    @Override
    public ResponseEntity<Status>
    initializeShortUrlMappingRepository(ServerHttpRequest request) {
        ShortUrlMappingStatus shortUrlMappingStatus = shortUrlMappingService
                .initializeShortUrlMappingRepository(request);

        HttpStatus httpStatus;
        String message;

        switch (shortUrlMappingStatus) {
            case SUCCESS:
                httpStatus = HttpStatus.OK;
                message = "Initialization of Short URL Mapping table "
                        + "completed successfully";
                break;

            case NOT_ON_LOCAL_MACHINE:
                httpStatus = HttpStatus.FORBIDDEN;
                message = "Initialization of the Short URL Mapping "
                        + "table can be done only when the service is "
                        + "running on your local machine";
                break;

            default:
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
        }

        return new ResponseEntity<>(
                new Status(shortUrlMappingStatus, message),
                httpStatus);
    }

    @Override
    public Mono<ResponseEntity<Status>>
    createMapping(ShortUrlMapping shortUrlMapping) {
        return shortUrlMappingService.createMapping(shortUrlMapping)
        .map(shortUrlUserStatus -> {

            HttpStatus httpStatus;
            String message;

            switch (shortUrlUserStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = "Mapping successfully created";
                    break;

                case MISSING_USERNAME:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty username must be specified";
                    break;

                case MISSING_SHORT_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty short URL must be specified";
                    break;

                case MISSING_LONG_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty long URL must be specified";
                    break;

                case SHORT_URL_ALREADY_TAKEN:
                    httpStatus = HttpStatus.CONFLICT;
                    message = String.format(
                            "Short URL '%s' is already taken",
                            shortUrlMapping.getShortUrl());
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            }

            return new ResponseEntity<>(
                    new Status(shortUrlUserStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlMappingArray>>
    getMappings(ShortUrlMappingFilter shortUrlMappingFilter) {
        return shortUrlMappingService.getMappings(shortUrlMappingFilter)
        .map(statusAndShortUrlMappingArray -> {

            ShortUrlMappingStatus shortUrlMappingStatus =
                    statusAndShortUrlMappingArray.getStatus().getStatus();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlMappingStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = "Mappings successfully retrieved";
                    break;

                case MISSING_USERNAME:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty username must be specified";
                    break;

                case MISSING_SHORT_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty short URL must be specified";
                    break;

                case MISSING_LONG_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty long URL must be specified";
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            }
            statusAndShortUrlMappingArray.getStatus().setMessage(message);

            return new ResponseEntity<>(
                    statusAndShortUrlMappingArray,
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<?>>
    redirectShortUrlToLongUrl(@PathVariable String shortUrl) {
        ShortUrlMappingFilter shortUrlMappingFilter =
                new ShortUrlMappingFilter("*", shortUrl, "*");

        return shortUrlMappingService.getMappings(shortUrlMappingFilter)
        .map(statusAndShortUrlMappings -> {

            List<ShortUrlMapping> shortUrlMappings =
                    statusAndShortUrlMappings.getShortUrlMappings();

            if (shortUrlMappings.isEmpty()) {
                return new ResponseEntity<>(
                        new Status(SHORT_URL_NOT_FOUND, String.format(
                                "Short URL '%s' was not found", shortUrl)),
                        HttpStatus.NOT_FOUND);
            }
            String longUrl = shortUrlMappings.getFirst().getLongUrl();
            try {
                URI longUri = new URI(longUrl);
                return ResponseEntity
                        .status(HttpStatus.TEMPORARY_REDIRECT)
                        .location(longUri).build();
            } catch (URISyntaxException e) {
                return new ResponseEntity<>(
                        new Status(BAD_LONG_URL_SYNTAX, String.format(
                                "Long URL '%s' has invalid syntax", longUrl)),
                        HttpStatus.BAD_REQUEST);
            }
        });
    }

    @Override
    public Mono<ResponseEntity<Status>>
    changeLongUrl(ShortUrlAndLongUrl shortUrlAndLongUrl) {
        return shortUrlMappingService.changeLongUrl(shortUrlAndLongUrl)
        .map(status -> {

            ShortUrlMappingStatus shortUrlMappingStatus = status.getStatus();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlMappingStatus) {
                case MISSING_SHORT_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty short URL must be specified";
                    break;

                case MISSING_LONG_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty long URL must be specified";
                    break;

                case SHORT_URL_NOT_FOUND:
                    httpStatus = HttpStatus.NOT_FOUND;
                    message = String.format("Short URL '%s' was not found",
                            shortUrlAndLongUrl.getShortUrl());
                    break;

                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = "Long URL successfully changed";
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            }
            return new ResponseEntity<>(
                    new Status(shortUrlMappingStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<Status>>
    deleteMappings(ShortUrlMappingFilter shortUrlMappingFilter) {
        return shortUrlMappingService.deleteMappings(shortUrlMappingFilter)
        .map(status -> {

            ShortUrlMappingStatus shortUrlMappingStatus = status.getStatus();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlMappingStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = "Mappings successfully deleted";
                    break;

                case MISSING_USERNAME:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty username must be specified";
                    break;

                case MISSING_SHORT_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty short URL must be specified";
                    break;

                case MISSING_LONG_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty long URL must be specified";
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            }
            status.setMessage(message);

            return new ResponseEntity<>(status, httpStatus);
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
