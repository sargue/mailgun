package net.sargue.mailgun;

import net.sargue.mailgun.adapters.MailImplementationHelper;

/**
 * The adapter that bridges to a specific REST client library in order to contact the Mailgun
 * service.
 * <p>
 * This library needs a REST client to send requests to the Mailgun servers. It doesn't
 * implement a REST client as there are many excellent libraries out there. Version 1 used
 * the Jersey 2 REST client but that forces a dependency to a specific implementation. Chances
 * are that you are already using a REST client library so this mechanism allows to use
 * any library that you want.
 *
 * @see MailImplementationHelper
 * @since 2.0
 */
public interface RestClientAdapter extends AutoCloseable {
    /**
     * Builds a {@link Mail} instance from a {@link MailBuilder}.
     *
     * @param mailBuilder the object with configuration and specific parameters
     * @return the object ready to send the request to the Mailgun service
     */
    Mail build(MailBuilder mailBuilder);

    @Override
    void close();
}
