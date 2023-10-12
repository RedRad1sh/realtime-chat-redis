package dev.nkucherenko.redischat.exception;

import dev.nkucherenko.redischat.dto.exception.ErrorCode;

public class UserAlreadyExistsException extends BusinessException {
    public ErrorCode getErrorCode() {
        return ErrorCode.USER_COLLISION;
    }

    public UserAlreadyExistsException() {
        super();
    }
}
