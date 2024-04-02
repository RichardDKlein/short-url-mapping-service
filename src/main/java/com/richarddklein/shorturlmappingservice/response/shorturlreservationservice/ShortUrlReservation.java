package com.richarddklein.shorturlmappingservice.response.shorturlreservationservice;

public class ShortUrlReservation {
    private String shortUrl;
    private String isAvailable;
    private int version;

    public String getShortUrl() {
        return shortUrl;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public int getVersion() {
        return version;
    }
}
