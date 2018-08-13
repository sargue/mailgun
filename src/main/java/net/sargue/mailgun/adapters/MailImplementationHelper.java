package net.sargue.mailgun.adapters;

import net.sargue.mailgun.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A helper class for custom REST client adapters. Helps implement the {@link Mail} interface.
 * <p>
 * Writing a custom REST client adapter means you need, basically, a function that creates
 * a {@link Mail} from a {@link MailBuilder} (see {@link RestClientAdapter}).
 * <p>
 * This class helps with a some common boilerplate for {@link Mail} implementations. Including
 * taking care of mail filtering and common methods. You only need to implement one
 * method although is recommended to also implement
 * {@link #sendAsyncFiltered(MailRequestCallback)} if the REST client library supports
 * asynchronous requests.
 * <p>
 * The provided adapters are implemented using this class so peeking at the source code could
 * be useful.
 *
 * @since 2.0
 */
public abstract class MailImplementationHelper implements Mail {
    private MailBuilder mailBuilder;

    protected MailImplementationHelper(MailBuilder mailBuilder) {
        this.mailBuilder = mailBuilder;
    }

    protected MailBuilder mailBuilder() {
        return mailBuilder;
    }

    @Override
    public Optional<String> getFirstValue(String param) {
        return mailBuilder.parameters().getFirst(param);
    }

    @Override
    public List<String> getValues(String param) {
        return mailBuilder.parameters().getValues(param);
    }

    @Override
    public Set<String> parameterKeySet() {
        return mailBuilder.parameters().keySet();
    }

    @Override
    public Configuration configuration() {
        return mailBuilder.configuration();
    }

    @Override
    public Response send() {
        if (!configuration().mailSendFilter().filter(this)) return null;

        return sendFiltered();
    }

    /**
     * Effectively sends this mail.
     * <p>
     * This method is just the same as {@link Mail#send()} but with filtering already done.
     *
     * @return the response from the Mailgun service
     * @see Configuration#registerMailSendFilter(MailSendFilter)
     */
    protected abstract Response sendFiltered();

    @Override
    public void sendAsync(MailRequestCallback callback) {
        if (!configuration().mailSendFilter().filter(this)) return;

        sendAsyncFiltered(callback);
    }

    /**
     * Effectively sends this mail asynchronously.
     * <p>
     * This method is just the same as {@link Mail#sendAsync(MailRequestCallback)} but with
     * filtering already done.
     * <p>
     * This is a default implementation which delegates to the
     * {@link Mail#sendAsync(MailRequestCallback)} default implementation. <strong>You should
     * override this method if the REST client library supports asynchronous requests.</strong>
     *
     * @see Configuration#registerMailSendFilter(MailSendFilter)
     */
    protected void sendAsyncFiltered(MailRequestCallback callback) {
        Mail.super.sendAsync(callback);
    }
}
