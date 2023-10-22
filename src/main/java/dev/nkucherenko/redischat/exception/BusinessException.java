package dev.nkucherenko.redischat.exception;

import dev.nkucherenko.redischat.dto.exception.ErrorCode;

public class BusinessException extends RuntimeException {
    public ErrorCode getErrorCode() {
        return ErrorCode.INTERNAL_ERROR;
    }

    public BusinessException() {
        super();
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message) {
        super(message);
    }
}
