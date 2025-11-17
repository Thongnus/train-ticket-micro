package com.example.commonservice.entity;

import com.example.commonservice.exceptionHandle.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final int status;
    private final boolean success = false;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String code;

    private final String message;
    private final String path;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<FieldError> errors;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;



    public static ErrorResponse of(int status, String code, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .code(code)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(int status, String code, String message, String path, List<FieldError> errors) {
        return ErrorResponse.builder()
                .status(status)
                .code(code)
                .message(message)
                .path(path)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .status(errorCode.getStatusValue())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return ErrorResponse.builder()
                .status(errorCode.getStatusValue())
                .code(errorCode.getCode())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String path, List<FieldError> errors) {
        return ErrorResponse.builder()
                .status(errorCode.getStatusValue())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(path)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}