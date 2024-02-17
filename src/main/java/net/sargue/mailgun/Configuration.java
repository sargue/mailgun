package net.sargue.mailgun;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import net.sargue.mailgun.content.ContentConverter;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;

/**
 * Holds the configuration parameters needed by the library. This is a mutable
 * class.
 * <p>
 * Here you configure the Mailgun properties and credentials and some defaults
 * like the {@code From} address for the emails, so you don't have to set it
 * everytime.
 * <p>
 * This class is designed to be built once and used everywhere on your
 * application unless you require different settings. Internally it has
 * a single JAX-RS client for all associated requests.
 * <p>
 * This class is thread safe.
 * <p>
 * Remember to close it when you don't need it anymore to free up resources.
 */
public class Configuration {
    private String apiUrl = "https://api.mailgun.net/v3";
    private String domain;
    private String apiKey;
    private int connectTimeout = 0;
    private int readTimeout = 0;
    private MultivaluedMap<String,String> defaultParameters = new MultivaluedHashMap<>();

    private final Client client = JerseyClientBuilder.newClient();
    private MailRequestCallbackFactory mailRequestCallbackFactory = null;
    private MailSendFilter mailSendFilter = defaultFilter;
    private final List<Converter<?>> converters =
        Collections.synchronizedList(new ArrayList<>());

    private static final ContentConverter<Object> defaultConverter = Object::toString;

    private static final MailSendFilter defaultFilter = mail -> true;

    private static final class Converter<T> {
        private final Class<T> classOfConverter;
        private final ContentConverter<? super T> contentConverter;

        Converter(Class<T> classOfConverter,
                  ContentConverter<? super T> contentConverter)
        {
            this.classOfConverter = classOfConverter;
            this.contentConverter = contentConverter;
        }
    }

    /**
     * Constructs an empy configuration.
     */
    public Configuration() {
        // empty constructor
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
        from(from);
    }

    /**
     * Creates a copy of this configuration.
     *
     * @return a copy of this configuration
     * @deprecated it's not clear what a 'copy' is so this method will be removed
     */
    @Deprecated
    public Configuration copy() {
        Configuration copy = new Configuration();
        copy.apiUrl = apiUrl;
        copy.domain = domain;
        copy.apiKey = apiKey;
        copy.mailRequestCallbackFactory = mailRequestCallbackFactory;
        copy.mailSendFilter = mailSendFilter;
        //noinspection Convert2Diamond
        copy.defaultParameters = new MultivaluedHashMap<String,String>(defaultParameters); //NOSONAR
        copy.converters.addAll(converters);
        return copy;
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
     * The latter form can also be achieved using the
     * {@link #from(String, String)} method.
     * <p>
     * Note that this value is treated as a single value as opposed to the
     * general default parameters stored in this configuration.
     *
     * @param from the default sender address
     * @return this configuration
     */
    public Configuration from(String from) {
        defaultParameters.putSingle("from", from);
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
        return from(MailBuilder.email(name, email));
    }

    /**
     * Sets the mailgun API endpoint. If not set defaults to the latest public
     * one. This override is useful for mock testing.
     *
     * @param apiUrl    the mailgun API URL
     * @return this configuration
     */
    public Configuration apiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        return this;
    }

    /**
     * Connect timeout interval, in milliseconds.
     * <p>
     * A value of zero (0) is equivalent to an interval of infinity.
     * </p>
     * <p>
     * The default value is infinity (0).
     * </p>
     *
     * @param connectTimeout connect timeout interval, in milliseconds
     * @return this configuration
     */
    public Configuration connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        client.property(CONNECT_TIMEOUT, connectTimeout == 0 ? null : connectTimeout);
        return this;
    }

    /**
     * Read timeout interval, in milliseconds.
     * <p>
     * A value of zero (0) is equivalent to an interval of infinity.
     * </p>
     * <p>
     * The default value is infinity (0).
     * </p>
     *
     * @param readTimeout the read timeout interval, in milliseconds
     * @return this configuration
     */
    public Configuration readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        client.property(READ_TIMEOUT, readTimeout == 0 ? null : readTimeout);
        return this;
    }

    /**
     * Adds a new value to the specified default parameter.
     * <p>
     * This is only used if the parameter is not specified when building
     * the specific mail.
     * <p>
     * Please note that parameters are multivalued. This method adds a new
     * value. To set a new value you need to clear the default parameter first.
     *
     * @param name the name of the parameter
     * @param value the new value to add to the parameter
     * @return this configuration
     * @see #clearDefaultParameter(String)
     */
    public Configuration addDefaultParameter(String name, String value) {
        defaultParameters.add(name, value);
        return this;
    }

    /**
     * Removes all the values of the specified default parameter.
     *
     * @param name the name of the parameter
     * @return this configuration
     */
    public Configuration clearDefaultParameter(String name) {
        defaultParameters.remove(name);
        return this;
    }

    /**
     * Registers a {@link MailRequestCallbackFactory} to use when sending the
     * message asynchronously without specifying a callback. See
     * {@link Mail#sendAsync()}
     *
     * @param factory a factory for creating default request callbacks
     * @return this configuration
     */
    public Configuration registerMailRequestCallbackFactory(MailRequestCallbackFactory factory) {
        mailRequestCallbackFactory = factory;
        return this;
    }

    /**
     * Removes the default request callback, if any.
     *
     * @return this configuration
     */
    public Configuration unregisterMailRequestCallbackFactory() {
        mailRequestCallbackFactory = null;
        return this;
    }

    /**
     * Registers a filter to decide if a mail should be sent or not.
     * <p>
     * This filter acts upon invokation of the different send methods in the
     * {@link Mail} class preventing the actual request to Mailgun if the
     * filter returns false.
     *
     * @param mailSendFilter the filter to apply to all messages
     * @return this configuration
     */
    public Configuration registerMailSendFilter(MailSendFilter mailSendFilter) {
        this.mailSendFilter = mailSendFilter;
        return this;
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
        return defaultParameters.getFirst("from");
    }

    /**
     * Returns the configured mailgun API URL endpoint.
     *
     * @return the configured mailgun API URL endpoint.
     */
    public String apiUrl() {
        return apiUrl;
    }

    /**
     * Returns the configured connect timeout.
     *
     * @return the configured connect timeout, in milliseconds
     */
    public int connectTimeout() {
        return connectTimeout;
    }

    /**
     * Returns the configured read timeout.
     *
     * @return the configured read timeout, in milliseconds
     */
    public int readTimeout() {
        return readTimeout;
    }

    /**
     * Returns the internal map of default parameters.
     * <p>
     * This is not a copy. Changes to this map are persistent.
     *
     * @return the internal map of default parameters
     */
    public Map<String, List<String>> defaultParameters() {
        return defaultParameters;
    }

    /**
     * Returns the configured default request callback factory.
     *
     * @return the configured default request callback factory
     *         or null if there is none configured
     */
    public MailRequestCallbackFactory mailRequestCallbackFactory() {
        return mailRequestCallbackFactory;
    }

    /**
     * Retrieves this configuration's filter.
     *
     * @return this configuration's filter
     */
    public MailSendFilter mailSendFilter() {
        return mailSendFilter;
    }

    /**
     * Registers a converter.
     * <p>
     * Converters are used mainly by the
     * {@link net.sargue.mailgun.content.Builder#text(Object)} method.
     * <p>
     * They are matched in declaration order checking for assignability
     * (inheritance). So you can have a {@link java.util.Date} converter
     * which will be applied to a {@link java.sql.Timestamp} object. You will
     * want to register the more specific converters first.
     *
     * @param <T>            the type parameter
     * @param converter      the converter
     * @param classToConvert the class to convert
     * @return the configuration
     */
    public <T> Configuration registerConverter(ContentConverter<? super T> converter,
                                               Class<T> classToConvert)
    {
        converters.add(new Converter<>(classToConvert, converter));
        return this;
    }

    /**
     * Returns the converter that would be used to convert the given class to
     * a String.
     * <p>
     * The converters are matched in registered order checking for assignability
     * (inheritance). If no converter is found the default converter
     * ({@link Object#toString()} is returned.
     *
     * @param classToConvert the class of the object to convert
     * @param <T> the type of the class
     * @return the converter that would be used to process objects of the given
     *         class
     */
    @SuppressWarnings("unchecked")
    public <T> ContentConverter<T> converter(Class<T> classToConvert) {
        for (Converter<?> converter : converters)
            if (converter.classOfConverter.isAssignableFrom(classToConvert))
                return (ContentConverter<T>) converter.contentConverter;
        return (ContentConverter<T>) defaultConverter;
    }

    /**
     * Closes configuration and associated resources. Mainly the JAX-RS client.
     * <p>
     * Don't use this configuration after closing it.
     */
    public void close() {
        client.close();
    }

    private HttpAuthenticationFeature httpAuthenticationFeature() {
        return HttpAuthenticationFeature
                .basicBuilder()
                .credentials("api", apiKey())
                .build();
    }

    WebTarget getTarget() {
        return client.target(apiUrl).register(httpAuthenticationFeature());
    }
}
