package dataaccess;

/**
 * Indicates that a data search turned up empty, or a value already existed
 */
public class BadDataRequestException extends RuntimeException {
    public BadDataRequestException(String message) {
        super(message);
    }
    public BadDataRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
