package net.sargue.mailgun;

/**
 * Type for the exceptions thrown by this library. This is an unchecked exception.
 */
public class MailgunException extends RuntimeException {
    public MailgunException(String message) {
        super(message);
    }

    public MailgunException(String message, Throwable cause) {
        super(message, cause);
    }
}
