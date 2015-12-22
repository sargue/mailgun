package net.sargue.mailgun;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Holds the configuration parameters needed by the library. This is a mutable
 * class.
 * <p>
 * Here you configure the Mailgun properties and credentials and some defaults
 * like the {@code From} address for the emails so you don't have to set it
 * everytime.
 */
public class Configuration {
    private String domain;
    private String apiKey;
    private String from;

    /**
     * Constructs an empy configuration.
     */
    public Configuration() {
    }

    /**
     * Constructs a basic configuration with the provided parameters.
     *
     * @param domain the Mailgun domain
     * @param apiKey the Mailgun api key
     * @param from   the default From address
     */
    public Configuration(String domain, String apiKey, String from) {
        this.domain = domain;
        this.apiKey = apiKey;
        this.from = from;
    }

    /**
     * Sets de Mailgun domain for this configuration.
     *
     * @param domain the Mailgun domain
     * @return this configuration
     */
    public Configuration domain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Sets the Mailgun api key for this configuration.
     *
     * @param apiKey the Mailgun api key
     * @return this configuration
     */
    public Configuration apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Sets the default sender address for messages that do not specify a
     * {@code From} address themselves.
     * <p>
     * The address can be a simple email address ({@code doc@delorean.com}) or
     * a full address with a name ({@code Emmet Brown <doc@delorean.com>}).
     * The latter form can also be achieved using the {@link #from(String, String)}
     * method.
     *
     * @param from the default sender address
     * @return this configuration
     */
    public Configuration from(String from) {
        this.from = from;
        return this;
    }

    /**
     * Sets the default sender address for messages that do not specify a
     * {@code From} address themselves.
     *
     * @param name  the default sender name
     * @param email the default sender email address
     * @return this configuration
     */
    public Configuration from(String name, String email) {
        return from(name + "<" + email + ">");
    }

    /**
     * Returns the configured Mailgun domain.
     *
     * @return the configured Mailgun domain
     */
    public String domain() {
        return domain;
    }

    /**
     * Returns the configured Mailgun api key.
     *
     * @return the configured Mailgun api key
     */
    public String apiKey() {
        return apiKey;
    }

    /**
     * Returns the configured default sender address.
     *
     * @return the configured default sender address
     */
    public String from() {
        return from;
    }

    HttpAuthenticationFeature httpAuthenticationFeature() {
        return HttpAuthenticationFeature
                .basicBuilder()
                .credentials("api", apiKey())
                .build();
    }
}
