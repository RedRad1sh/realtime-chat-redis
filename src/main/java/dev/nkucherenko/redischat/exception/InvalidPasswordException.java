package dev.nkucherenko.redischat.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super();
    }

    public InvalidPasswordException(Throwable cause) {
        super(cause);
    }
}
