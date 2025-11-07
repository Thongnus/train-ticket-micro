package com.example.commonservice.exceptionHandle.exception;

import lombok.Getter;

@Getter
public class SystemErrorException extends RuntimeException {
    public SystemErrorException(String message) {
        super(message);
    }

    public SystemErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
