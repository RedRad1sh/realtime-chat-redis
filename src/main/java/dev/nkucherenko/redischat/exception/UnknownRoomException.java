package dev.nkucherenko.redischat.exception;

public class UnknownRoomException extends BusinessException {
    public UnknownRoomException(String message) {
        super(message);
    }
}
