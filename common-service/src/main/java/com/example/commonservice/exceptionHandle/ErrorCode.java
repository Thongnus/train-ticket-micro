package com.example.commonservice.exceptionHandle;

public enum ErrorCode {
    BAD_CREDENTIALS("E000", "BAD_CREDENTIALS"),

    ;


    public final String code;
    public final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
