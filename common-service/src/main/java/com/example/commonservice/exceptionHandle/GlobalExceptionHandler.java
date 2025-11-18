package com.example.commonservice.exceptionHandle;

import com.example.commonservice.entity.ErrorResponse;
import com.example.commonservice.entity.FieldError;
import com.example.commonservice.exceptionHandle.ErrorCode;
import com.example.commonservice.exceptionHandle.exception.BaseException;
import com.example.commonservice.exceptionHandle.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== Custom Business Exceptions =====

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request) {
        log.warn("Business exception: {} - {}", ex.getCode(), ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ex.getStatus(),
                ex.getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException ex,
            HttpServletRequest request) {
        log.warn("NotFound exception: {} ", ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
//    @ExceptionHandler(SeatLockedException.class)
//    public ResponseEntity<ErrorResponse> handleSeatLockedException(
//            SeatLockedException ex,
//            HttpServletRequest request) {
//        log.warn("Seat locked exception: {}", ex.getMessage());
//
//        ErrorResponse response = ErrorResponse.of(
//                ex.getStatus(),
//                ex.getCode(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//
//        return ResponseEntity.status(ex.getStatus()).body(response);
//    }

    // ===== Validation Exceptions =====

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.warn("Validation error on path: {}", request.getRequestURI());

        List<FieldError> fieldErrors = extractFieldErrors(ex.getBindingResult());

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Validation failed",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        log.warn("Constraint violation on path: {}", request.getRequestURI());

        List<FieldError> errors = ex.getConstraintViolations().stream()
                .map(this::mapConstraintViolation)
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Validation failed",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.BODY_MISSING,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        String message = String.format("Invalid value '%s' for parameter '%s'",
                ex.getValue(), ex.getName());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_FORMAT,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ===== Security Exceptions =====

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {
        log.warn("Bad credentials attempt from: {}", request.getRemoteAddr());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.BAD_CREDENTIALS,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        log.warn("Access denied for path: {}", request.getRequestURI());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.ACCESS_DENIED,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ===== Not Found Exceptions =====

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request) {
        log.warn("No handler found for: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.ENDPOINT_NOT_FOUND,
                String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handlePropertyReference(
            PropertyReferenceException ex,
            HttpServletRequest request) {
        log.warn("Invalid property reference: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_INPUT,
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ===== System Exceptions =====

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unhandled exception on path: {}", request.getRequestURI(), ex);

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // ===== Helper Methods =====

    private List<FieldError> extractFieldErrors(BindingResult bindingResult) {
        // Lưu error đầu tiên cho mỗi field với priority cao nhất
        Map<String, org.springframework.validation.FieldError> fieldErrorMap = new HashMap<>();

        for (org.springframework.validation.FieldError error : bindingResult.getFieldErrors()) {
            String field = error.getField();
            String[] codes = error.getCodes();

            if (!fieldErrorMap.containsKey(field)) {
                fieldErrorMap.put(field, error);
            } else {
                org.springframework.validation.FieldError existing = fieldErrorMap.get(field);
                if (isHigherPriority(codes, existing.getCodes())) {
                    fieldErrorMap.put(field, error);
                }
            }
        }

        List<FieldError> errors = new ArrayList<>();

        // Field errors
        fieldErrorMap.values().forEach(error ->
                errors.add(FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
        );

        // Global errors
        bindingResult.getGlobalErrors().forEach(error ->
                errors.add(FieldError.builder()
                        .field(error.getObjectName())
                        .message(error.getDefaultMessage())
                        .build())
        );

        return errors;
    }

    private boolean isHigherPriority(String[] newCodes, String[] existingCodes) {
        List<String> priority = List.of("NotBlank", "NotNull", "NotEmpty", "Size", "Pattern", "Email", "Min", "Max");

        int newPriority = getFirstMatchingPriorityIndex(newCodes, priority);
        int existingPriority = getFirstMatchingPriorityIndex(existingCodes, priority);

        return newPriority >= 0 && (existingPriority == -1 || newPriority < existingPriority);
    }

    private int getFirstMatchingPriorityIndex(String[] codes, List<String> priority) {
        for (String code : codes) {
            for (int i = 0; i < priority.size(); i++) {
                if (code.contains(priority.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private FieldError mapConstraintViolation(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath().toString();
        if (field == null || field.isBlank()) {
            field = violation.getRootBeanClass().getSimpleName();
        }

        return FieldError.builder()
                .field(field)
                .message(violation.getMessage())
                .rejectedValue(violation.getInvalidValue())
                .build();
    }
}