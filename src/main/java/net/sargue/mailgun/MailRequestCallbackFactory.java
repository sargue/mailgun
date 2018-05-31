package net.sargue.mailgun;

/**
 * A factory for creating instances of {@link MailRequestCallback}
 */
public interface MailRequestCallbackFactory {
    /**
     * Returns a {@link MailRequestCallback}. Could be new or the same.
     *
     * @param mail the mail object where we will be using the callback
     * @return a MailRequestCallback
     */
    MailRequestCallback create(Mail mail);
}
