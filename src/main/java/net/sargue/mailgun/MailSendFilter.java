package net.sargue.mailgun;

import net.sargue.mailgun.adapters.MailImplementationHelper;

/**
 * A filter to decide if a mail should be sent or not.
 * <p>
 * This filter acts upon invocation of the different send methods in the
 * {@link Mail} class preventing the actual request to Mailgun if the
 * filter returns false.
 * <p>
 * Please note that support of the filtering system is dependant on the actual implementation
 * of the {@link Mail} which, on its own, is dependant on the specific REST client library
 * adapter. Of course all provided adapters honor the filter but if you are implementing
 * a custom adapter you should take this into consideration.
 * <p>
 * One implementation of this filter can be registered on a {@link Configuration}.
 *
 * @see MailImplementationHelper
 */
@FunctionalInterface
public interface MailSendFilter {

    /**
     * Decide if this mail should be sent.
     *
     * @param mail the mail to check
     * @return true if the process of sending the email should continue
     */
    boolean filter(Mail mail);
}
