package net.sargue.mailgun.content;

import net.sargue.mailgun.Configuration;

/**
 * The body of an email message represented in two versions: HTML and plain
 * text.
 * <p>
 * Usually you don't build this class directly but using a {@link Builder}
 * helper. You get the reference to a builder using one of the static factory
 * methods on this class.
 */
public class Body {
    private String html;
    private String text;

    /**
     * Directly creates an email body given both HTML and plain text content.
     *
     * @param html the HTML representation of this email body content
     * @param text the plain text representation of this email body content
     */
    public Body(String html, String text) {
        this.html = html;
        this.text = text;
    }

    Body(MessageBuilder html, MessageBuilder text) {
        this(html.toString(), text.toString());
    }

    /**
     * @return a new builder with the basic default configuration
     */
    public static Builder builder() {
        return builder(new Configuration());
    }

    /**
     * @param configuration a configuration to be used by the builder
     * @return a new builder using the given configuration
     */
    public static Builder builder(Configuration configuration) {
        return new Builder(configuration);
    }

    /**
     * @return the HTML representation of this email body content
     */
    public String html() {
        return html;
    }

    /**
     * Sets the HTML representation of this email body content.
     *
     * @param html the new HTML representation of this email body content
     * @return this same Body object for call chaining
     */
    public Body html(String html) {
        this.html = html;
        return this;
    }

    /**
     * @return the plain text representation of this email body content
     */
    public String text() {
        return text;
    }

    /**
     * Sets the plain text representation of this email body content.
     *
     * @param text the new plain text representation of this email body content
     * @return this same Body object for call chaining
     */
    public Body text(String text) {
        this.text = text;
        return this;
    }
}
