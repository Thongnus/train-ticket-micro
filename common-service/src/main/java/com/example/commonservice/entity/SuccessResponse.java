package com.example.commonservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {
    private final int status;
    private final boolean success = true;
    private final String message;
    private final T data;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public static <T> SuccessResponse<T> of(T data) {
        return SuccessResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> SuccessResponse<T> of(T data, String message) {
        return SuccessResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> SuccessResponse<T> created(T data) {
        return SuccessResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message("Created successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> SuccessResponse<T> created(T data, String message) {
        return SuccessResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}