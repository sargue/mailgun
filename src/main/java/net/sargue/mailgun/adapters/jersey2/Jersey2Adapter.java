package net.sargue.mailgun.adapters.jersey2;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.MailBuilder;
import net.sargue.mailgun.RestClientAdapter;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;

public class Jersey2Adapter implements RestClientAdapter {
    private Client client;

    public Jersey2Adapter(Configuration configuration) throws ClassNotFoundException {
        Class.forName("org.glassfish.jersey.client.JerseyClientBuilder");

        client = ClientBuilder.newClient()
                              .register(MultiPartFeature.class)
                              .register(HttpAuthenticationFeature
                                            .basicBuilder()
                                            .credentials("api", configuration.apiKey())
                                            .build());

        if (configuration.connectTimeout() != 0)
            client.property(CONNECT_TIMEOUT, configuration.connectTimeout());
        if (configuration.readTimeout() != 0)
            client.property(READ_TIMEOUT, configuration.readTimeout());
    }

    @Override
    public Mail build(MailBuilder mailBuilder) {
        return new Jersey2Mail(mailBuilder, client);
    }

    @Override
    public void close() {
        client.close();
    }
}
