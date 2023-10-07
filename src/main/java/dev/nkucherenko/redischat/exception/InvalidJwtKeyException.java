package dev.nkucherenko.redischat.exception;

public class InvalidJwtKeyException extends RuntimeException {
    public InvalidJwtKeyException() {
        super();
    }

    public InvalidJwtKeyException(Throwable cause) {
        super(cause);
    }

    public InvalidJwtKeyException(String message) {
        super(message);
    }
}
