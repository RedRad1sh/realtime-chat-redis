package dev.nkucherenko.redischat.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super();
    }

    public UserAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
