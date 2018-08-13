package net.sargue.mailgun;

import net.sargue.mailgun.attachment.Attachment;
import net.sargue.mailgun.content.Body;
import net.sargue.mailgun.content.Builder;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A mutable builder for a {@code Mail}. This allows the creation of a
 * {@code Mail} by adding the desired parts in any order.
 * <p>
 * Can be instantiated using the constructor or a static factory method. In
 * any case the builder needs a configuration.
 */
public class MailBuilder {
    private Configuration    configuration;
    private ParameterMap     parameters  = new ParameterMap();
    private List<Attachment> attachments = new LinkedList<>();

    /**
     * Creates a {@code MailBuilder} with the provided configuration.
     * <p>
     * You can also use the static factory method. This constructor plays
     * well with dependency injection frameworks.
     *
     * @param configuration the configuration to use with this builder
     */
    public MailBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Creates a {@code MailBuilder} with the provided configuration.
     *
     * @param configuration the configuration to use with this builder
     * @return a new builder which uses the specified configuration
     */
    public static MailBuilder using(Configuration configuration) {
        return new MailBuilder(configuration);
    }

    /**
     * Returns the configuration used by this builder.
     *
     * @return the configuration used by this builder
     */
    public Configuration configuration() {
        return configuration;
    }

    /**
     * Sets the address of the sender.
     * <p>
     * The address can be a simple email address ({@code doc@delorean.com}) or
     * a full address with a name ({@code Emmet Brown <doc@delorean.com>}).
     * The latter form can also be achieved using the
     * {@link #from(String, String)} method.
     * <p>
     * You don't need to specify the sender address in every mail as it is
     * usually the same. You can specify a default {@code From} address in
     * the {@link Configuration} object.
     *
     * @param from the sender address
     * @return this builder
     */
    public MailBuilder from(String from) {
        return from(null, from);
    }
    
    /**
     * Sets the address of the sender.
     *
     * @param name  the name of the sender
     * @param email the address of the sender
     * @return this builder
     */
    public MailBuilder from(String name, String email) {
        return param("from", email(name, email));
    }

    /**
     * Adds a destination recipient's address.
     * <p>
     * The address can be a simple email address ({@code doc@delorean.com}) or
     * a full address with a name ({@code Emmet Brown <doc@delorean.com>}).
     * The latter form can also be achieved using the
     * {@link #to(String, String)} method.
     *
     * @param to an address
     * @return this builder
     */
    public MailBuilder to(String to) {
        return to(null, to);
    }

    /**
     * Adds a destination recipient's address.
     *
     * @param name  the name of the destination recipient
     * @param email the address of the destination recipient
     * @return this builder
     */
    public MailBuilder to(String name, String email) {
        return param("to", email(name, email));
    }

    /**
     * Adds a CC recipient's address.
     * <p>
     * The address can be a simple email address ({@code doc@delorean.com}) or
     * a full address with a name ({@code Emmet Brown <doc@delorean.com>}).
     * The latter form can also be achieved using the
     * {@link #cc(String, String)} method.
     *
     * @param cc an address
     * @return this builder
     */
    public MailBuilder cc(String cc) {
        return cc(null, cc);
    }

    /**
     * Adds a CC recipient's address.
     *
     * @param name  the name of the CC recipient
     * @param email the address of the CC recipient
     * @return this builder
     */
    public MailBuilder cc(String name, String email) {
        return param("cc", email(name, email));
    }

    /**
     * Adds a BCC recipient's address.
     * <p>
     * The address can be a simple email address ({@code doc@delorean.com}) or
     * a full address with a name ({@code Emmet Brown <doc@delorean.com>}).
     * The latter form can also be achieved using the
     * {@link #bcc(String, String)} method.
     *
     * @param bcc an address
     * @return this builder
     */
    public MailBuilder bcc(String bcc) {
        return bcc(null, bcc);
    }

    /**
     * Adds a BCC recipient's address.
     *
     * @param name  the name of the BCC recipient
     * @param email the address of the BCC recipient
     * @return this builder
     */
    public MailBuilder bcc(String name, String email) {
        return param("bcc", email(name, email));
    }

    /**
     * Sets the {@code reply-to} field.
     * <p>
     * This method adds the param {@code h:Reply-To} to the Mailgun request.
     *
     * @param email an email address
     * @return this builder
     */
    public MailBuilder replyTo(String email) {
        return param("h:Reply-To", email);
    }

    /**
     * Sets the subject of the message.
     *
     * @param subject the subject
     * @return this builder
     */
    public MailBuilder subject(String subject) {
        return param("subject", subject);
    }

    /**
     * Sets the plain-text version of the message body.
     *
     * @param text the body of the message in plain text
     * @return this builder
     */
    public MailBuilder text(String text) {
        return param("text", text);
    }

    /**
     * Sets the HTML version of the message body.
     *
     * @param html the body of the message in HTML
     * @return this builder
     */
    public MailBuilder html(String html) {
        return param("html", html);
    }

    /**
     * Sets the content of the message, both the plain text and HTML version.
     *
     * @param body the content of the message
     * @return this builder
     */
    public MailBuilder content(Body body) {
        return text(body.text()).html(body.html());
    }

    /**
     * Convenience shortcut to {@code Body.builder(configuration)}
     *
     * @return a new {@link Builder} associated with this MailBuilder
     */
    public Builder body() {
        return new Builder(this);
    }

    /**
     * Adds a custom parameter.
     *
     * @param name  the name of the parameter
     * @param value the value of the parameter
     * @return this builder
     */
    public MailBuilder parameter(String name, String value) {
        return param(name, value);
    }

    /**
     * Adds an attachment from a {@link File}.
     *
     * @param file a file to attach
     * @return this builder
     * @since 2.0
     */
    public MailBuilder attachment(File file) {
        attachments.add(Attachment.create(file));
        return this;
    }

    /**
     * Adds an attachment from a {@link InputStream}.
     *
     * @param is an stream to read the attachment
     * @return this builder
     * @since 2.0
     */
    public MailBuilder attachment(InputStream is) {
        attachments.add(Attachment.create(is));
        return this;
    }

    /**
     * Adds a named attachment.
     *
     * @param is       an stream to read the attachment
     * @param filename the filename to give to the attachment
     * @return this builder
     * @since 2.0
     */
    public MailBuilder attachment(InputStream is, String filename) {
        attachments.add(Attachment.create(is, filename));
        return this;
    }

    /**
     * Adds a named attachment with a custom MIME media type.
     *
     * @param is        an stream to read the attachment
     * @param filename  the filename to give to the attachment
     * @param mediaType the media type of the attachment
     * @return this builder
     * @since 2.0
     */
    public MailBuilder attachment(InputStream is, String filename, String mediaType) {
        attachments.add(Attachment.create(is, filename, mediaType));
        return this;
    }

    /**
     * Adds an attachment directly by content.
     *
     * @param content the content of the attachment
     * @param filename the filename of the attachment
     * @return this builder
     * @since 2.0
     */
    public MailBuilder attachment(String content, String filename) {
        attachments.add(Attachment.create(content, filename));
        return this;
    }

    /**
     * Adds a named inline attachment.
     *
     * @param is      an stream to read the attachment
     * @param cidName the name to give to the attachment as referenced by the HTML email body
     *                i.e. use cidName sample-image.png for the below example
     *                <p>
     *                    <img src="cid:sample-image.png" alt="sample">
     *                </p>
     * @return this builder
     * @since 2.0
     */
    public MailBuilder inline(InputStream is, String cidName) {
        attachments.add(Attachment.inline(is, cidName));
        return this;
    }

    /**
     * Adds a named inline attachment.
     *
     * @param file    a file to attach
     * @param cidName the name to give to the attachment as referenced by the HTML email body
     *                i.e. use cidName sample-image.png for the below example
     *                <p>
     *                    <img src="cid:sample-image.png" alt="sample">
     *                </p>
     * @return this builder
     * @since 2.0
     */
    public MailBuilder inline(File file, String cidName) {
        attachments.add(Attachment.inline(file, cidName));
        return this;
    }

    /**
     * The current parameters for this mail request.
     *
     * @return the current parameters for this mail request
     */
    public ParameterMap parameters() {
        return parameters;
    }

    /**
     * Gets the current values of a given parameter.
     * <p>
     * Example: {@code parameter("to") -> ["doc@delorean.com", "marty@mcfly.com"]}
     *
     * @param key the name of the parameter
     * @return the current value list of the parameter
     */
    public List<String> parameter(String key) {
        return parameters.getValues(key);
    }

    /**
     * Gets the current list of attachments.
     * <p>
     * Attachments are really like any other parameter to the Mailgun service but the content
     * isn't anymore a mere String but probably something different and most of the time
     * treated as an "application/octet-stream".
     * <p>
     * Also, the presence of attachments requires that the request to the Mailgun service
     * must be done using the <tt>multipart/form-data</tt> encoding. This is automatically
     * handled by each REST client adapter.
     *
     * @return the current list of attachments
     * @since 2.0
     */
    public List<Attachment> attachments() {
        return attachments;
    }

    /**
     * Finishes the building phase and returns a {@link Mail}.
     *
     * @return a {@link Mail} built from this builder
     */
    public Mail build() {
        defaultParameters();
        return configuration.restClientAdapter().build(this);
    }

    static String email(String name, String email) {
        return name == null ? email : name + " <" + email + ">";
    }

    private void defaultParameters() {
        ParameterMap def = configuration.defaultParameters();
        for (String key : def.keySet())
            if (!parameters.containsKey(key))
                parameters.addAll(key, def.getValues(key));
    }

    private MailBuilder param(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        parameters.add(name, value);
        return this;
    }
}
