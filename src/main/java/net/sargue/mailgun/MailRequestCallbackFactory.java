package net.sargue.mailgun;

/**
 * A factory for creating instances of {@link MailRequestCallback}
 */
public interface MailRequestCallbackFactory {
    /**
     * Returns a {@link MailRequestCallback}. Could be new or the same.
     *
     * @return a MailRequestCallback
     */
    MailRequestCallback create();
}
