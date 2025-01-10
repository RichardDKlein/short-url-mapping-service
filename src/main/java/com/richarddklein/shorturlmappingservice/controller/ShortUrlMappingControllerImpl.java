/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.controller;

import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturlmappingservice.entity.ShortUrlMapping;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import com.richarddklein.shorturlmappingservice.service.ShortUrlMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
    initializeShortUrlMappingRepository() {
        ShortUrlStatus shortUrlMappingStatus = shortUrlMappingService
                .initializeShortUrlMappingRepository();

        HttpStatus httpStatus;
        String message;

        switch (shortUrlMappingStatus) {
            case SUCCESS -> {
                httpStatus = HttpStatus.OK;
                message = "Initialization of Short URL Mapping table "
                        + "completed successfully";
            }
            case NOT_ON_LOCAL_MACHINE -> {
                httpStatus = HttpStatus.FORBIDDEN;
                message = "Initialization of the Short URL Mapping "
                        + "table can be done only when the service is "
                        + "running on your local machine";
            }
            default -> {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }
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
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = "Mapping successfully created";
                    }
                    case MISSING_USERNAME -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty username must be specified";
                    }
                    case MISSING_SHORT_URL -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty short URL must be specified";
                    }
                    case MISSING_LONG_URL -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty long URL must be specified";
                    }
                    case SHORT_URL_ALREADY_TAKEN -> {
                        httpStatus = HttpStatus.CONFLICT;
                        message = String.format(
                                "Short URL '%s' is already taken",
                                shortUrlMapping.getShortUrl());
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
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
                ShortUrlStatus shortUrlMappingStatus =
                        statusAndShortUrlMappingArray.getStatus().getStatus();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlMappingStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = "Mappings successfully retrieved";
                    }
                    case MISSING_USERNAME -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty username must be specified";
                    }
                    case MISSING_SHORT_URL -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty short URL must be specified";
                    }
                    case MISSING_LONG_URL -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty long URL must be specified";
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
                }
                statusAndShortUrlMappingArray.getStatus().setMessage(message);

                return new ResponseEntity<>(
                        statusAndShortUrlMappingArray,
                        httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<Status>>
    changeLongUrl(ShortUrlAndLongUrl shortUrlAndLongUrl) {
        return shortUrlMappingService.changeLongUrl(shortUrlAndLongUrl)
            .map(status -> {
                ShortUrlStatus shortUrlMappingStatus = status.getStatus();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlMappingStatus) {
                    case MISSING_SHORT_URL -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty short URL must be specified";
                    }
                    case MISSING_LONG_URL -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty long URL must be specified";
                    }
                    case SHORT_URL_NOT_FOUND -> {
                        httpStatus = HttpStatus.NOT_FOUND;
                        message = String.format("Short URL '%s' was not found",
                                shortUrlAndLongUrl.getShortUrl());
                    }
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = "Long URL successfully changed";
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
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
                ShortUrlStatus shortUrlMappingStatus = status.getStatus();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlMappingStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = "Mappings successfully deleted";
                    }
                    case MISSING_USERNAME -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty username must be specified";
                    }
                    case MISSING_SHORT_URL -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty short URL must be specified";
                    }
                    case MISSING_LONG_URL -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty long URL must be specified";
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
                }

                status.setMessage(message);

                return new ResponseEntity<>(status, httpStatus);
            });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
