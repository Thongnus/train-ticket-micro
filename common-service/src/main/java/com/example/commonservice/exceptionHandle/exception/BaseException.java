package com.example.commonservice.exceptionHandle.exception;

import com.example.commonservice.exceptionHandle.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String customMessage;
    private final transient Object data;

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
        this.data = null;
    }

    protected BaseException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.data = null;
    }

    protected BaseException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.data = null;
    }

    protected BaseException(ErrorCode errorCode, String customMessage, Object data) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.data = data;
    }

    public String getCode() {
        return errorCode.getCode();
    }

    public int getStatus() {
        return errorCode.getStatusValue();
    }

    @Override
    public String getMessage() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}