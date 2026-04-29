package ir.ac.ut.ece.ie.api.exception;

public final class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
