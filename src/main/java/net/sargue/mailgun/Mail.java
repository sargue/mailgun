package net.sargue.mailgun;

import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.*;

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
     * Sends the email.
     * <p>
     * This method send the request to the Mailgun service. It is a
     * <strong>blocking</strong> method so it will return upon request
     * completion.
     *
     * @return the response from the Mailgun service
     */
    public Response send() {
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
     * Sends the email asynchronously, ignoring the outcome.
     *
     * If you want to know if the message has been sent use
     * {@link #sendAsync(MailRequestCallback)} instead.
     */
    public void sendAsync() {
        prepareSend();
        request()
            .async()
            .post(entity());
    }

    Configuration configuration() {
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
