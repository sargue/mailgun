package net.sargue.mailgun.content;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.MailBuilder;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A helper designed to build easily basic dual content type HTML and plain
 * text messages. It's not supposed to be used for building cutting edge
 * responsive modern HTML messages. It's just for simple cases where you need
 * to send a message and you want to use some basic HTML like tables and some
 * formatting.
 * <p>
 * All null values used in text and content in general are simply replaced
 * by an empty String, no NPE or "null" text.
 * <p>
 * This class supports converters to format objects into text. It has some
 * basic built-in ones (for numbers, dates, etc.) and and extension mechanism
 * to add your own.
 */
public class Builder {
    private static final String CRLF = "\r\n";
    private static final String PRE_HTML =
        "<!DOCTYPE html><html><head>" + CRLF +
        "<meta name='viewport' content='width=device-width' />" +
        "<meta http-equiv='Content-Type' " +
        "content='text/html; charset=UTF-8' />" +
        "</head><body>" + CRLF;
    private static final String POST_HTML = "<br></body></html>";

    private MessageBuilder html = new MessageBuilder().a(PRE_HTML);
    private MessageBuilder text = new MessageBuilder();
    private Deque<String> ends = new ArrayDeque<>();

    private Configuration configuration;
    private MailBuilder mailBuilder;

    /**
     * Creates a new builder with the given configuration.
     *
     * The configuration object is queried for some objects needed by different
     * parts of this class. Basically, converters (numbers, dates...) and the
     * locale used by those converters, when applicable.
     *
     * You can also set any of those objects and override the configured ones.
     * Check the related methods of this class.
     *
     * @param configuration the configuration to use
     */
    public Builder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Builder(MailBuilder mailBuilder) {
        this.mailBuilder = mailBuilder;
        configuration = mailBuilder.configuration();
    }

    /**
     * Ends this builder and returns a Body. Checks about the closing
     * tags throwing and {@link IllegalStateException} if there are any pending.
     * <p>
     * The HTML version gets the <em>body</em> and <em>html</em> closing tags
     * and both receive a final new line.
     *
     * @return the mail content object
     * @throws IllegalStateException if there are pending tags to close
     */
    public Body build() {
        if (!ends.isEmpty())
            throw new IllegalStateException(
                "Pending some closing. Some end() missing. ends=" + ends);
        html.nl().a(POST_HTML);
        text.nl();
        return new Body(html, text);
    }

    /**
     * Convenience method for chaining the creation of the content body with
     * the creation of the mail envelope.
     *
     * If this builder was created associated to a MailBuilder, that one (with
     * the content updated) is returned.
     *
     * Else it returns a new {@link MailBuilder} with this content and using the
     * same * configuration that this Builder was created with.
     *
     * @return a {@link MailBuilder} with this content
     */
    public MailBuilder mail() {
        if (mailBuilder != null)
            return mailBuilder.content(build());
        else
            return MailBuilder.using(configuration).content(build());
    }

    /**
     * Closes the last opened tag or section.
     *
     * @return this builder
     * @throws IllegalStateException if there are no pending tags to close
     */
    public Builder end() {
        if (ends.isEmpty())
            throw new IllegalStateException("No pending tag/section to close.");
        html.a(ends.pop());
        return this;
    }

    /*
     *
     *
     *
     *
     *              Text formatting and conversion
     *
     *
     *
     *
     */

    /**
     * Adds text to both the HTML and the plain text version. The HTML version
     * is escaped of all XML tags.
     *
     * @param s the text to append
     * @return this builder
     */
    public Builder text(String s) {
        html.a(Util.escapeXml(s));
        text.a(s);
        return this;
    }

    public <T> Builder text(T value) {
        //noinspection unchecked
        return text(value,
                    configuration.converter((Class<T>) value.getClass()));
    }

    public <T> Builder text(T value, ContentConverter<T> converter) {
        return text(converter.toString(value));
    }

    /*
     *
     *
     *
     *
     *              HTML tags
     *
     *
     *
     *
     */

    /**
     * Adds a {@code <h1>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder h1(String content) {
        return tag("h1").text(content).end();
    }

    /**
     * Opens a {@code <h1>} tag.
     *
     * @return this builder
     */
    public Builder h1() {
        return tag("h1");
    }


    /**
     * Opens a {@code <h2>} tag.
     *
     * @return this builder
     */
    public Builder h2() {
        return tag("h2");
    }

    /**
     * Adds a {@code <h2>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder h2(String content) {
        return tag("h2").text(content).end();
    }

    /**
     * Opens a {@code <h3>} tag.
     *
     * @return this builder
     */
    public Builder h3() {
        return tag("h3");
    }

    /**
     * Adds a {@code <h3>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder h3(String content) {
        return tag("h3").text(content).end();
    }

    /**
     * Opens a {@code <h4>} tag.
     *
     * @return this builder
     */
    public Builder h4() {
        return tag("h4");
    }

    /**
     * Adds a {@code <h4>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder h4(String content) {
        return tag("h4").text(content).end();
    }

    /**
     * Opens a {@code <h5>} tag.
     *
     * @return this builder
     */
    public Builder h5() {
        return tag("h5");
    }

    /**
     * Adds a {@code <h5>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder h5(String content) {
        return tag("h5").text(content).end();
    }

    /**
     * Opens a {@code <h6>} tag.
     *
     * @return this builder
     */
    public Builder h6() {
        return tag("h6");
    }

    /**
     * Adds a {@code <h6>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder h6(String content) {
        return tag("h6").text(content).end();
    }

    /**
     * Appends an {@code <br>} to the HTML version and two new lines to the
     * text version.
     *
     * @return this builder
     */
    public Builder br() {
        html.a("<br>");
        text.nl(2);
        return this;
    }

    /**
     * Opens a {@code <p>} tag.
     *
     * @return this builder
     */
    public Builder p() {
        return tag("p");
    }

    /**
     * Adds a {@code <p>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder p(String content) {
        return tag("p").text(content).end();
    }

    /**
     * Opens a {@code <pre>} tag.
     * <p>
     * @return this builder
     */
    public Builder pre() {
        return tag("pre");
    }

    /**
     * Adds a {@code <pre>} block with text content. The plain text version will
     * get only the content.
     * <p>
     * @param content the text content
     * @return this builder
     */
    public Builder pre(String content) {
        return tag("pre").text(content).end();
    }

    /**
     * Opens a {@code <em>} tag.
     * <p>
     *
     * @return this builder
     */
    public Builder em() {
        return tag("em");
    }

    /**
     * Adds a {@code <em>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder em(String content) {
        return tag("em").text(content).end();
    }

    /**
     * Opens a {@code <strong>} tag.
     * <p>
     *
     * @return this builder
     */
    public Builder strong() {
        return tag("strong");
    }

    /**
     * Adds a {@code <strong>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return this builder
     */
    public Builder strong(String content) {
        return tag("strong").text(content).end();
    }

    /*
     *
     *
     *
     *
     *              Tables
     *
     *
     *
     *
     */

    /**
     * Starts a table. Adds a {@code <table>} tag with 1 px collapsed border.
     *
     * @return this builder
     */
    public Builder table() {
        return tag("table", "border='1' cellpadding='0' cellspacing='0'");
    }

    /**
     * Starts a new row. Same as {@code tag("tr")}
     *
     * @return this builder
     */
    public Builder row() {
        return tag("tr");
    }

    /**
     * Adds a new row with one column.
     *
     * @param <T>       the type parameter
     * @param firstCell the first cell content
     * @return this builder
     */
    public <T> Builder row(T firstCell) {
        return tag("tr").cell(firstCell).end();
    }

    /**
     * Adds a new row with two columns.
     *
     * @param <T>        the type parameter
     * @param firstCell  the first cell content
     * @param secondCell the second cell content
     * @return this builder
     */
    public <T> Builder row(T firstCell, T secondCell) {
        return tag("tr").cell(firstCell, false).cell(secondCell).end();
    }

    /**
     * Adds a new row with two columns, the first one being a header cell.
     *
     * @param <T>   the type parameter
     * @param label the header cell content
     * @param data  the second cell content
     * @return this builder
     */
    public <T> Builder rowh(String label, T data) {
        return tag("tr").cellHeader(label, false).cell(data).end();
    }

    /**
     * Adds a new row with three columns.
     *
     * @param <T>        the type parameter
     * @param firstCell  the first cell content
     * @param secondCell the second cell content
     * @param thirdCell  the third cell content
     * @return this builder
     */
    public <T> Builder row(T firstCell,
                           T secondCell,
                           T thirdCell)
    {
        return tag("tr").cell(firstCell, false)
                        .cell(secondCell, false)
                        .cell(thirdCell)
                        .end();
    }

    /**
     * Adds a new row with four columns.
     *
     * @param <T>        the type parameter
     * @param firstCell  the first cell content
     * @param secondCell the second cell content
     * @param thirdCell  the third cell content
     * @param fourthCell the four cell content
     * @return this builder
     */
    public <T> Builder row(T firstCell,
                           T secondCell,
                           T thirdCell,
                           T fourthCell)
    {
        return tag("tr").cell(firstCell, false)
                        .cell(secondCell, false)
                        .cell(thirdCell, false)
                        .cell(fourthCell)
                        .end();
    }

    /**
     * Starts a new cell. Same as {@code tag("td")}
     *
     * @return this builder
     */
    public Builder cell() {
        return tag("td");
    }

    /**
     * Starts a new cell header. Same as {@code tag("th")}
     *
     * @return this builder
     */
    public Builder cellHeader() {
        return tag("th");
    }

    /**
     * Adds a new cell with text content.
     *
     * @param <T>     the type parameter
     * @param content the content to show inside the cell
     * @return this builder
     */
    public <T> Builder cell(T content) {
        return cell(content, true);
    }

    /**
     * Adds a new cell header with text content.
     *
     * @param <T>     the type parameter
     * @param content the content to show inside the cell header
     * @return this builder
     */
    public <T> Builder cellHeader(T content) {
        return cellHeader().text(content).end();
    }

    private <T> Builder cell(T content, boolean lastCell) {
        cell().text(content).end();
        if (!lastCell) {
            text.a(',');
        }
        return this;
    }

    private Builder cellHeader(String label, boolean lastCell) {
        cellHeader().text(label).end();
        if (!lastCell) {
            text.a(',');
        }
        return this;
    }
    
   /*
    *
    *
    *
    *
    *              Links
    *
    *
    *
    *
    */
    
    /**
     * Adds a {@code <a>} tag with the following URL.
     *
     * @return this builder
     */
    public Builder link(String href) {
        return link(href, href);
    }

   /**
    * Adds a {@code <a>} tag with the following URL and title.
    *
    * @return this builder
    */
   public Builder link(String href, String title) {
       text.a(title).a(" : ").a(href);
       tag("a", "href='" + href + "' target='_blank'");
       html.a(title);
       return end();
   }

    /*
     *
     *
     *
     *
     *              Low level functions
     *
     *
     *
     *
     */

    /**
     * Starts an HTML tag. It has no effect on the plain text version.
     *
     * @param tag the tag name
     * @return this builder
     */
    public Builder tag(String tag) {
        ends.push("</" + tag + ">");
        html.a('<').a(tag).a('>');
        return this;
    }

    /**
     * Starts an HTML tag. It has no effect on the plain text version.
     *
     * @param tag      the tag name
     * @param attributes attributes full text, like "style='color:red'"
     * @return this builder
     */
    public Builder tag(String tag, String attributes) {
        ends.push("</" + tag + ">");
        html.a('<').a(tag).sp().a(attributes).a('>');
        return this;
    }
}
