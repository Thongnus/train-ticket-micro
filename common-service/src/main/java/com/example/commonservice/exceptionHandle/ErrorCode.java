package com.example.commonservice.exceptionHandle;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication & Authorization Errors (1xxx)
    UNAUTHORIZED("1001", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS("1002", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("1003", "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("1004", "Invalid token", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("1005", "Access denied", HttpStatus.FORBIDDEN),
    ACCOUNT_DEACTIVATED("1006", "Account has been deactivated", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED("1007", "Account is locked", HttpStatus.FORBIDDEN),

    // Validation Errors (2xxx)
    VALIDATION_ERROR("2001", "Validation failed", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("2002", "Invalid input data", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("2003", "Required field is missing", HttpStatus.BAD_REQUEST),
    INVALID_FORMAT("2004", "Invalid data format", HttpStatus.BAD_REQUEST),
    BODY_MISSING("2005", "Request body is missing or invalid", HttpStatus.BAD_REQUEST),

    // Resource Errors (3xxx)
    RESOURCE_NOT_FOUND("3001", "Resource not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("3002", "User not found", HttpStatus.NOT_FOUND),
    ENDPOINT_NOT_FOUND("3003", "Endpoint not found", HttpStatus.NOT_FOUND),

    // Conflict Errors (4xxx)
    RESOURCE_ALREADY_EXISTS("4001", "Resource already exists", HttpStatus.CONFLICT),
    DUPLICATE_ENTRY("4002", "Duplicate entry", HttpStatus.CONFLICT),
    SEAT_LOCKED("4003", "Seat is already locked", HttpStatus.CONFLICT),
    OPTIMISTIC_LOCK("4004", "Resource was modified by another user", HttpStatus.CONFLICT),

    // Business Logic Errors (5xxx)
    BUSINESS_RULE_VIOLATION("5001", "Business rule violation", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE("5002", "Insufficient balance", HttpStatus.BAD_REQUEST),
    OPERATION_NOT_ALLOWED("5003", "Operation not allowed", HttpStatus.BAD_REQUEST),

    // System Errors (9xxx)
    INTERNAL_SERVER_ERROR("9001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("9002", "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    DATABASE_ERROR("9003", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR("9004", "External service error", HttpStatus.BAD_GATEWAY);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getStatusValue() {
        return httpStatus.value();
    }
}