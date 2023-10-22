package dev.nkucherenko.redischat.exception;

import dev.nkucherenko.redischat.dto.exception.ErrorCode;

public class InvalidJwtKeyException extends BusinessException {
    public ErrorCode getErrorCode() {
        return ErrorCode.JWT_AUTHORIZATION_ERROR;
    }

    public InvalidJwtKeyException(Throwable cause) {
        super(cause);
    }
}
