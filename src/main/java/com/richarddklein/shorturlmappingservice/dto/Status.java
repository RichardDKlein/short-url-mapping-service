/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dto;

public class Status {
    private ShortUrlMappingStatus status;
    private String message;

    public Status(ShortUrlMappingStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status(ShortUrlMappingStatus status) {
        this.status = status;
    }

    public ShortUrlMappingStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlMappingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
