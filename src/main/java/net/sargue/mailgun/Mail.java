package net.sargue.mailgun;

import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.*;
import java.util.List;

/**
 * Representation of a Mailgun's mail request.
 * <p>
 * It must be built using a {@link MailBuilder}.
 */
public abstract class Mail {
    private final Configuration configuration;

    Mail(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Convenience shortcut to {@code MailBuilder.using(configuration)}
     *
     * @param configuration the configuration to use
     * @return a new {@link MailBuilder} which uses the specified configuration
     */
    public static MailBuilder using(Configuration configuration) {
        return new MailBuilder(configuration);
    }

    /**
     * Retrieves the value of a given mail parameter. If there are multiple
     * values the first one is returned. If the parameter hasn't been set
     * null is returned.
     *
     * Can only be used on simple parameters (String). So don't use it on
     * <i>attachment</i> for example. Doing so will throw a
     * {@link IllegalStateException}.
     *
     * @param param the name of the parameter
     * @return the first value of the parameter, if any, null otherwise
     * @throws IllegalStateException if the parameter is not a simple (basic text) one
     */
    public abstract String getFirstValue(String param);

    /**
     * Retrieves the values of a given mail parameter. If the parameter hasn't
     * been set an empty list is returned.
     *
     * Can only be used on simple parameters (String). So don't use it on
     * <i>attachment</i> for example. Doing so will throw a
     * {@link IllegalStateException}.
     *
     * @param param the name of the parameter
     * @return the list of values for the parameter or an empty list
     * @throws IllegalStateException if the parameter is not a simple (basic text) one
     */
    public abstract List<String> getValues(String param);

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
    public Response send() {
        if (!configuration.mailSendFilter().filter(this)) return null;
        prepareSend();
        return new Response(request().post(entity()));
    }

    /**
     * Sends the email asynchronously.
     * <p>
     * This method returns immediately, sending the request to the Mailgun
     * service in the background. It is a <strong>non-blocking</strong>
     * method.
     *
     * @param callback the callback to be invoked upon completion or failure
     */
    public void sendAsync(final MailRequestCallback callback) {
        if (!configuration.mailSendFilter().filter(this)) return;
        prepareSend();
        request()
                .async()
                .post(entity(),
                      new InvocationCallback<javax.ws.rs.core.Response>() {
                          @Override
                          public void completed(javax.ws.rs.core.Response o) {
                              callback.completed(new Response(o));
                          }

                          @Override
                          public void failed(Throwable throwable) {
                              callback.failed(throwable);
                          }
                      });
    }

    /**
     * Sends the email asynchronously. It uses the configuration provided
     * default callback if available, ignoring the outcome otherwise.
     *
     * If you want to use a specific callback for this call use
     * {@link #sendAsync(MailRequestCallback)} instead.
     */
    public void sendAsync() {
        if (!configuration.mailSendFilter().filter(this)) return;
        MailRequestCallbackFactory factory = configuration.mailRequestCallbackFactory();
        if (factory == null) {
            prepareSend();
            request().async().post(entity());
        } else
            sendAsync(factory.create(this));
    }

    /**
     * Retrieves the configuration associated with this Mail.
     *
     * @return the underlying configuration
     */
    public Configuration configuration() {
        return configuration;
    }

    abstract Entity<?> entity(); //NOSONAR

    abstract void prepareSend();

    void configureClient(Client client) {
        //defaults to no-op
    }

    private Invocation.Builder request() {
        Client client = JerseyClientBuilder.newClient();
        configureClient(client);
        return client
                .register(configuration.httpAuthenticationFeature())
                .target(configuration.apiUrl())
                .path(configuration.domain())
                .path("messages")
                .request();
    }
}
