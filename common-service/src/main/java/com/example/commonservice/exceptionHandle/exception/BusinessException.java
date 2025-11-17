package com.example.commonservice.exceptionHandle.exception;

import com.example.commonservice.exceptionHandle.ErrorCode;
import lombok.Getter;

// Business Exception
@Getter
public class BusinessException extends BaseException {
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}