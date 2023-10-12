package dev.nkucherenko.redischat.exception;

import dev.nkucherenko.redischat.dto.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public ErrorCode getErrorCode() {
        return ErrorCode.USER_NOT_EXIST;
    }

    public UserNotFoundException() {
        super();
    }
}
