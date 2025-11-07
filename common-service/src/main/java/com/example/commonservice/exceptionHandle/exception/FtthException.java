package com.example.commonservice.exceptionHandle.exception;

import com.example.commonservice.exceptionHandle.ErrorCode;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FtthException extends RuntimeException {

    private final String code;
    private String message;

    public FtthException(ErrorCode error) {
        this.code = error.code;
        this.message = error.message;
    }

    public FtthException(ErrorCode error, String message) {
        this.code = error.code;
        if (StringUtils.isNotBlank(message)) {
            this.message = message;
        } else {
            this.message = error.message;
        }
    }

    public FtthException(String code) {
        this.code = code;
    }

    public FtthException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public FtthException(String code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
