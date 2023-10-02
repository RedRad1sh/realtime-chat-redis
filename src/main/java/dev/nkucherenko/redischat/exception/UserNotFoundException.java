package dev.nkucherenko.redischat.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
