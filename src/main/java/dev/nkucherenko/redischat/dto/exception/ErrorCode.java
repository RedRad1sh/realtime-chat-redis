package dev.nkucherenko.redischat.dto.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    JWT_AUTHORIZATION_ERROR("A1", 401),
    WRONG_PASSWORD("A2", 401),
    USER_COLLISION("R1", 400),
    INTERNAL_ERROR("I0", 500),
    USER_NOT_EXIST("I1", 401);

    private final String businessCode;
    private final int httpCode;
}
