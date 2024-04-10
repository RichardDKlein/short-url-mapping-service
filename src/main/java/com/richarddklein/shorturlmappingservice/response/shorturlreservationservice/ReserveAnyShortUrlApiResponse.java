package com.richarddklein.shorturlmappingservice.response.shorturlreservationservice;

public class ReserveAnyShortUrlApiResponse {
    private Status status;
    private ShortUrlReservation shortUrlReservation;

    public Status getStatus() {
        return status;
    }

    public ShortUrlReservation getShortUrlReservation() {
        return shortUrlReservation;
    }
}
