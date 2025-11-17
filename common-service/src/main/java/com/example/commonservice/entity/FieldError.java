package com.example.commonservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldError {
    private final String field;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Object rejectedValue;
}