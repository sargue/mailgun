package net.sargue.mailgun;

import net.sargue.mailgun.adapters.MailImplementationHelper;
import net.sargue.mailgun.log.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

/**
 * A ready to send Mail.
 * <p>
 * Built using a {@link MailBuilder} this object is the last step before sending. It has
 * access to all parameters and attachments.
 * <p>
 * The specific implementation of this interface usually depends on the REST client library
 * used to send the request to the Mailgun service.
 * <p>
 * <strong>Implementation notes</strong>
 * <p>
 * If you are writing a custom adapter for a unsupported REST client library you are going
 * to need to create a class implementing this interface. The class
 * {@link MailImplementationHelper} might prove useful in that case.
 */
public interface Mail {
    Logger log = Logger.getLogger(Mail.class);

    /**
     * Convenience shortcut to {@code MailBuilder.using(configuration)}
     *
     * @param configuration the configuration to use
     * @return a new {@link MailBuilder} which uses the specified configuration
     */
    static MailBuilder using(Configuration configuration) {
        return new MailBuilder(configuration);
    }

    /**
     * Retrieves the value of a given mail parameter. If there are multiple
     * values the first one is returned. If the parameter hasn't been set
     * {@link Optional#empty()} is returned.
     *
     * Can only be used on simple parameters (String), not attachments.
     *
     * @param param the name of the parameter
     * @return the first value of the parameter, if any
     */
    Optional<String> getFirstValue(String param);

    /**
     * Retrieves the values of a given mail parameter. If the parameter hasn't
     * been set an empty list is returned. The returned list must be read-only.
     *
     * Can only be used on simple parameters (String), not attachments.
     *
     * @param param the name of the parameter
     * @return the list of values for the parameter or an empty list
     */
    List<String> getValues(String param);

    /**
     * Retrieves the set of names of the parameters.
     *
     * @return the set of names of the parameters
     */
    Set<String> parameterKeySet();

    /**
     * Sends the email.
     * <p>
     * This method send the request to the Mailgun service. It is a
     * <strong>blocking</strong> method so it will return upon request
     * completion.
     *
     * @return the response from the Mailgun service or null if the message
     *         is not sent (filtered by {@link MailSendFilter}
     */
    Response send();

    /**
     * Sends the email asynchronously.
     * <p>
     * This method returns immediately, sending the request to the Mailgun
     * service in the background. It is a <strong>non-blocking</strong>
     * method.
     * <p>
     * If the callback is null it is ignored, no NPE. Notice that if you pass a null to this
     * method and there is a {@link MailRequestCallbackFactory} set in the configuration
     * <strong>it is not used</strong>. The callback is just ignored.
     * <p>
     * A default implementation is provided that uses the {@link ForkJoinPool#commonPool()} to
     * submit the job using a call to the blocking method {@link #send()}.
     * <p>
     * Most REST clients provide non blocking calls that should be preferred over this
     * default method.
     *
     * @param callback the callback to be invoked upon completion or failure
     */
    default void sendAsync(MailRequestCallback callback) {
        log.debug("Using default implementation of Mail#sendAsync(MailRequestCallback)");

        if (!configuration().mailSendFilter().filter(this)) return;

        ForkJoinPool.commonPool().execute(() -> {
            try {
                Response response = send();
                if (callback != null)
                    callback.completed(response);
            } catch (Exception e) {
                if (callback != null)
                    callback.failed(e);
                else
                    log.warn("Sending failed without callback.", e);
            }
        });
    }

    /**
     * Sends the email asynchronously. It uses the configuration provided
     * default callback (see {@link MailRequestCallbackFactory}) if available, ignoring the
     * outcome otherwise.
     * <p>
     * If you want to use a specific callback for this call use
     * {@link #sendAsync(MailRequestCallback)} instead.
     * <p>
     * If you want to ignore the configured factory once just use
     * {@link #sendAsync(MailRequestCallback)} with a custom callback or null.
     * <p>
     * A default implementation is provided that calls {@link #sendAsync(MailRequestCallback)}
     * with a callback generated from the configured {@link MailRequestCallbackFactory}.
     */
    default void sendAsync() {
        log.debug("Using default implementation of Mail#sendAsync()");

        if (!configuration().mailSendFilter().filter(this)) return;

        MailRequestCallback callback = configuration().mailRequestCallbackFactory()
                                                      .map(factory -> factory.create(this))
                                                      .orElse(null);
        sendAsync(callback);
    }

    /**
     * Retrieves the configuration associated with this Mail.
     *
     * @return the underlying configuration
     */
    Configuration configuration();
}
