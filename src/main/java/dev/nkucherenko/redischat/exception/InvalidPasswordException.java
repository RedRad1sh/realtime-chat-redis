package dev.nkucherenko.redischat.exception;

import dev.nkucherenko.redischat.dto.exception.ErrorCode;

public class InvalidPasswordException extends BusinessException {
    public ErrorCode getErrorCode() {
        return ErrorCode.WRONG_PASSWORD;
    }

    public InvalidPasswordException() {
        super();
    }
}
