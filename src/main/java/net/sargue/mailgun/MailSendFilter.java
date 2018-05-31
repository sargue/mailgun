package net.sargue.mailgun;

/**
 * A filter to decide if a mail should be sent or not.
 *
 * This filter acts upon invokation of the different send methods in the
 * {@link Mail} class preventing the actual request to Mailgun if the
 * filter returns false.
 *
 * One implementation of this filter can be registered on a {@link Configuration}.
 */
public interface MailSendFilter {

    /**
     * Decide if this mail should be sent.
     *
     * @param mail the mail to check
     * @return true if the process of sending the email should continue
     */
    boolean filter(Mail mail);
}
