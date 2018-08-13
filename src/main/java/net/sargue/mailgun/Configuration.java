package net.sargue.mailgun;

import net.sargue.mailgun.adapters.jersey2.Jersey2Adapter;
import net.sargue.mailgun.content.ContentConverter;
import net.sargue.mailgun.log.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Holds the configuration parameters and some state needed by the library.
 * <p>
 * This is a mutable class.
 * <p>
 * Here you configure the Mailgun properties, credentials and some defaults
 * like the <tt>From</tt> address for the emails so you don't have to set it
 * everytime. This is also the place to set the general behaviour like the mail filter
 * or the callback factory.
 * <p>
 * This class is not synchronized. It shouldn't be a problem if you are setting the
 * configuration from a thread and then using it from multiple threads.
 * <p>
 * Please note that you should close this object when done. A good practice is to have
 * just one instance (singleton) of this class for the whole lifetime of the application and
 * just close it upon shutdown (like, probably, some other resources).
 */
public class Configuration implements AutoCloseable {
    private static final Logger log = Logger.getLogger(Configuration.class);

    private static final ContentConverter<Object> defaultConverter = Object::toString;
    private static final MailSendFilter           defaultFilter    = mail -> true;

    private MailgunRegion              region                     = MailgunRegion.US;
    private String                     apiUrl                     = MailgunRegion.US.apiUrl();
    private String                     domain;
    private String                     apiKey;
    private int                        connectTimeout             = 0;
    private int                        readTimeout                = 0;
    private ParameterMap               defaultParameters          = new ParameterMap();
    private MailRequestCallbackFactory mailRequestCallbackFactory = null;
    private MailSendFilter             mailSendFilter             = defaultFilter;
    private List<Converter<?>>         converters                 = new ArrayList<>();
    private RestClientAdapter          restClientAdapter;

    /**
     * Internal class for encapsulated the user provided functions to convert between
     * some T and a String when generating content using, for example, the method 
     * {@link net.sargue.mailgun.content.Builder#text(Object)}.
     * 
     * @param <T> the class of objects this converter can convert to a String
     */
    private static final class Converter<T> {
        private Class<T>                    classOfConverter;
        private ContentConverter<? super T> contentConverter;

        Converter(Class<T> classOfConverter, ContentConverter<? super T> contentConverter) {
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
        this();
        this.domain = domain;
        this.apiKey = apiKey;
        from(from);
    }

    /**
     * Sets the Mailgun region. It also sets the api URL for that region, like calling
     * {@link #apiUrl(String)}.
     *
     * @param region the Mailgun region to set this configuration to
     * @return this configuration, updated
     * @since 2.0
     */
    public Configuration region(MailgunRegion region) {
        this.region = region;
        apiUrl(region.apiUrl());
        return this;
    }

    /**
     * Returns this configuration's Mailgun region.
     *
     * @return this configuration's Mailgun region
     * @since 2.0
     */
    public MailgunRegion region() {
        return region;
    }

    /**
     * Sets de Mailgun domain for this configuration.
     *
     * @param domain the Mailgun domain
     * @return this configuration, updated
     */
    public Configuration domain(String domain) {
        this.domain = domain;
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
     * Sets the Mailgun api key for this configuration.
     *
     * @param apiKey the Mailgun api key
     * @return this configuration, updated
     */
    public Configuration apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
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
     * Sets the default sender address for messages that do not specify a
     * <tt>From</tt> address themselves.
     * <p>
     * The address can be a simple email address (<tt>doc@delorean.com</tt>) or
     * a full address with a name ({@code Emmet Brown <doc@delorean.com>}).
     * The latter form can also be achieved using the
     * {@link #from(String, String)} method.
     * <p>
     * Note that this value is treated as a single value as opposed to the
     * general default parameters stored in this configuration.
     *
     * @param from the default sender address
     * @return this configuration, updated
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
     * @return this configuration, updated
     */
    public Configuration from(String name, String email) {
        return from(MailBuilder.email(name, email));
    }

    /**
     * Returns the configured default sender address.
     *
     * @return the configured default sender address
     */
    public Optional<String> from() {
        return defaultParameters.getFirst("from");
    }

    /**
     * Sets the mailgun API endpoint. If not set defaults to the latest public
     * one for the region.
     *
     * @param apiUrl    the mailgun API URL
     * @return this configuration, updated
     */
    public Configuration apiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        return this;
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
     * Computes the full URL of the messages endpoint of the Mailgun API.
     * <p>
     * That's generally {@link #apiUrl()} + a slash character if needed + {@link #domain()} +
     * <tt>/messages</tt>.
     *
     * @return the full URL where requests are to be sent
     */
    public String fullUrl() {
        StringBuilder sb = new StringBuilder(apiUrl);
        if (apiUrl.charAt(apiUrl.length() - 1) != '/')
            sb.append('/');
        return sb.append(domain).append("/messages").toString();
    }

    /**
     * Connect timeout interval, in milliseconds.
     * <p>
     * A value of zero (0) is equivalent to an interval of infinity.
     * <p>
     * The default value is infinity (0).
     *
     * @param connectTimeout the connect timeout interval, in milliseconds
     * @return this configuration, updated
     */
    public Configuration connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
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
     * Read timeout interval, in milliseconds.
     * <p>
     * A value of zero (0) is equivalent to an interval of infinity.
     * <p>
     * The default value is infinity (0).
     *
     * @param readTimeout the read timeout interval, in milliseconds
     * @return this configuration, updated
     */
    public Configuration readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
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
     * @return this configuration, updated
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
     * @return this configuration, updated
     */
    public Configuration clearDefaultParameter(String name) {
        defaultParameters.remove(name);
        return this;
    }

    /**
     * Returns the internal map of default parameters.
     * <p>
     * This is not a copy. Changes to this map are persistent.
     *
     * @return the internal map of default parameters
     */
    public ParameterMap defaultParameters() {
        return defaultParameters;
    }

    /**
     * Registers a {@link MailRequestCallbackFactory} to use when sending the
     * message asynchronously without specifying a callback. See
     * {@link Mail#sendAsync()}
     *
     * @param factory a factory for creating default request callbacks
     * @return this configuration, updated
     */
    public Configuration registerMailRequestCallbackFactory(MailRequestCallbackFactory factory) {
        mailRequestCallbackFactory = factory;
        return this;
    }

    /**
     * Removes the default request callback, if any.
     *
     * @return this configuration, updated
     */
    public Configuration unregisterMailRequestCallbackFactory() {
        mailRequestCallbackFactory = null;
        return this;
    }

    /**
     * Returns the configured default request callback factory.
     *
     * @return the configured default request callback factory, if any
     */
    public Optional<MailRequestCallbackFactory> mailRequestCallbackFactory() {
        return Optional.ofNullable(mailRequestCallbackFactory);
    }

    /**
     * Registers a filter to decide if a mail should be sent or not.
     * <p>
     * This filter acts upon invokation of the different send methods in the
     * {@link Mail} class preventing the actual request to Mailgun if the
     * filter returns false.
     *
     * @param mailSendFilter the filter to apply to all messages
     * @return this configuration, updated
     */
    public Configuration registerMailSendFilter(MailSendFilter mailSendFilter) {
        this.mailSendFilter = mailSendFilter;
        return this;
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
     * Sets the REST client adapter for this configuration.
     * <p>
     * Usually the library should be able to auto detect the installed REST client library
     * among those supported by default (currently only Jersey 2). This method allows to set a
     * custom adapter for another library or to circunvent the lookup mechanism and fix a
     * specific library. That can be useful is there is more than one library installed.
     *
     * @param restClientAdapter the REST client adapter
     * @return this configuration, updated
     * @since 2.0
     */
    public Configuration restClientAdapter(RestClientAdapter restClientAdapter) {
        this.restClientAdapter = restClientAdapter;
        return this;
    }

    /**
     * Gets the REST client adapter for this configuration.
     * <p>
     * If there is no custom adapter previously set with
     * {@link #restClientAdapter(RestClientAdapter)} then this method tries to locate a
     * suitable adapter depending on installed libraries.
     * <p>
     * It does so by trying to instante a class from that REST client library and, if no
     * exception (like {@link ClassNotFoundException}) is thrown, sets and adapter for that
     * library.
     * <p>
     * There is currently only provided support for Jersey 2 but that could change in the
     * future.
     *
     * @return the REST client adapter for this configuration, possibly launching a lookup of
     *         a suitable installed library
     * @since 2.0
     */
    public RestClientAdapter restClientAdapter() {
        if (restClientAdapter == null)
            lookupRestClientAdapter();
        return restClientAdapter;
    }

    /**
     * Registers a converter.
     * <p>
     * Converters are used mainly by the
     * {@link net.sargue.mailgun.content.Builder#text(Object)} method.
     * <p>
     * They are matched in declaration order checking for assignability
     * (inheritance). So you can have a a {@link java.util.Date} converter
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
     * Closes this resource, relinquishing any underlying resources.
     * <p>
     * This method is invoked automatically on objects managed by the try-with-resources
     * statement.
     * <p>
     * Once closed this configuration should not be used anymore to send messages.
     */
    @Override
    public void close() {
        restClientAdapter().close();
    }

    private void lookupRestClientAdapter() {
        log.debug("Auto-detecting REST client library.");
        // lookup for a supported REST client
        try {
            // try Jersey2
            restClientAdapter = new Jersey2Adapter(this);
            log.info("REST client library detected: Jersey 2");
        } catch (Throwable t) {
            throw new MailgunException("No REST client implementation found.");
        }
    }
}
