package com.example.commonservice.exceptionHandle.exception;

import lombok.Getter;

@Getter
public class HttpResponseException extends RuntimeException {
    private final int status;
    private final String path;

    public HttpResponseException(int status, String message, String path) {
        super(message);
        this.status = status;
        this.path = path;
    }

}