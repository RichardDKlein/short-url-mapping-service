package com.richarddklein.shorturlmappingservice.response.shorturlreservationservice;

import com.richarddklein.shorturlmappingservice.response.ShortUrlMappingStatus;

public class ShortUrlReservationResult {
    public ShortUrlMappingStatus status;
    public String shortUrl;

    public ShortUrlReservationResult(ShortUrlMappingStatus status, String shortUrl) {
        this.status = status;
        this.shortUrl = shortUrl;
    }
}
