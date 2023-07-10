package ba.unsa.etf.rpr.exceptions;

/**
 * Custom Exception created for the purposes of this application
 * @author Kemal Lipovača
 */
public class CourtroomException extends Exception {
    public CourtroomException(String message) {
        super(message);
    }
    public CourtroomException(String message, Exception reason) {
        super(message, reason);
    }
}