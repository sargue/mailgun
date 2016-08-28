package net.sargue.mailgun.content;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class is deprecated. Don't use it. See below for instructions on how to
 * migrate current code.
 * <h3>Migration guide</h3>
 * <p>
 * Where you had
 * <pre>
 * MailContent mailContent = new MailContent()
 *      [content stuff here]
 *      .close();
 *
 * MailBuilder.using(configuration)
 *      .content(mailContent)
 *      [mail envelope stuff here]
 *      .build()
 *      .send();
 * </pre>
 *
 * You can translate it to this beauty:
 * <pre>
 * Mail.bodyBuilder(configuration)
 *     [content stuff here]
 *     .mail()
 *     [mail envelope stuff here]
 *     .build()
 *     .send();
 * </pre>
 *
 * @deprecated You should use the new {@link Builder}, see description.
 */
@Deprecated
public class MailContent { //NOSONAR
    private static final String CRLF = "\r\n";

    private MessageBuilder text = new MessageBuilder();
    private MessageBuilder html = new MessageBuilder();
    private Deque<String> ends = new ArrayDeque<>();

    /**
     * Constructs an empty MailContent.
     */
    public MailContent() {
        html.a("<!DOCTYPE html><html><head>")
                .a(CRLF)
                .a("<meta name='viewport' content='width=device-width' />")
                .a("<meta http-equiv='Content-Type' ")
                .a("content='text/html; charset=UTF-8' />")
                .a("</head><body>").nl();
    }

    /**
     * Starts an HTML tag. It has no effect on the plain text version.
     *
     * @param tag the tag name
     * @return the changed mail content object
     */
    private MailContent tag(String tag) {
        ends.push("</" + tag + ">");
        html.a('<').a(tag).a('>');
        return this;
    }

    /**
     * Starts an HTML tag. It has no effect on the plain text version.
     *
     * @param tag      the tag name
     * @param atributs attributes full text, like "style='color:red'"
     * @return the changed mail content object
     */
    private MailContent tag(String tag, String atributs) {
        ends.push("</" + tag + ">");
        html.a('<').a(tag).sp().a(atributs).a('>');
        return this;
    }

    /**
     * Closes the last opened tag or section.
     *
     * @return the changed mail content object
     */
    public MailContent end() {
        if (ends.isEmpty())
            throw new IllegalStateException("No pending tag/section to close.");
        html.a(ends.pop());
        return this;
    }

    /**
     * Closes this MailContent. Checks about the closing tags throwing and
     * {@link IllegalStateException} if there are any pending.
     * <p>
     * The HTML version gets the <em>body</em> and <em>html</em> closing tags
     * and both receive a final new line.
     *
     * @return the changed mail content object
     */
    public MailContent close() {
        if (!ends.isEmpty())
            throw new IllegalStateException(
                    "Pending some closing. Some end() missing.");
        html.nl().a("<br></body></html>");
        text.nl();
        return this;
    }

    /**
     * @return the plain text version of the mail content
     */
    public String text() {
        return text.toString();
    }

    /**
     * @return the HTML version of the mail content
     */
    public String html() {
        return html.toString();
    }

    /**
     * Appends an {@code <br>} to the HTML version and two new lines to the
     * text version.
     *
     * @return the changed mail content object
     */
    public MailContent br() {
        html.a("<br>");
        text.nl(2);
        return this;
    }

    /**
     * Adds text to both the HTML and the plain text version. The HTML version
     * is escaped of all XML tags.
     *
     * @param s the text to append
     * @return the changed mail content object
     */
    public MailContent text(String s) {
        html.a(escapeXml(s));
        text.a(s);
        return this;
    }

    private String escapeXml(String target) {
        return Util.escapeXml(target);
    }

    /**
     * Opens a {@code <h1>} tag.
     *
     * @return the changed mail content object
     */
    public MailContent h1() {
        return tag("h1");
    }

    /**
     * Adds a {@code <h1>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent h1(String content) {
        return tag("h1").text(content).end();
    }

    /**
     * Opens a {@code <h2>} tag.
     *
     * @return the changed mail content object
     */
    public MailContent h2() {
        return tag("h2");
    }

    /**
     * Adds a {@code <h2>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent h2(String content) {
        return tag("h2").text(content).end();
    }

    /**
     * Opens a {@code <h3>} tag.
     *
     * @return the changed mail content object
     */
    public MailContent h3() {
        return tag("h3");
    }

    /**
     * Adds a {@code <h3>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent h3(String content) {
        return tag("h3").text(content).end();
    }

    /**
     * Opens a {@code <h4>} tag.
     *
     * @return the changed mail content object
     */
    public MailContent h4() {
        return tag("h4");
    }

    /**
     * Adds a {@code <h4>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent h4(String content) {
        return tag("h4").text(content).end();
    }

    /**
     * Opens a {@code <h5>} tag.
     *
     * @return the changed mail content object
     */
    public MailContent h5() {
        return tag("h5");
    }

    /**
     * Adds a {@code <h5>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent h5(String content) {
        return tag("h5").text(content).end();
    }

    /**
     * Opens a {@code <h6>} tag.
     *
     * @return the changed mail content object
     */
    public MailContent h6() {
        return tag("h6");
    }

    /**
     * Adds a {@code <h6>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent h6(String content) {
        return tag("h6").text(content).end();
    }

    /**
     * Opens a {@code <p>} tag.
     *
     * @return the changed mail content object
     */
    public MailContent p() {
        return tag("p");
    }

    /**
     * Adds a {@code <p>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent p(String content) {
        return tag("p").text(content).end();
    }

    /**
     * Opens a {@code <pre>} tag.
     * <p>
     * @return the changed mail content object
     */
    public MailContent pre() {
        return tag("pre");
    }

    /**
     * Adds a {@code <pre>} block with text content. The plain text version will
     * get only the content.
     * <p>
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent pre(String content) {
        return tag("pre").text(content).end();
    }

    /**
     * Opens a {@code <em>} tag.
     * <p>
     *
     * @return the changed mail content object
     */
    public MailContent em() {
        return tag("em");
    }

    /**
     * Adds a {@code <em>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent em(String content) {
        return tag("em").text(content).end();
    }

    /**
     * Opens a {@code <strong>} tag.
     * <p>
     *
     * @return the changed mail content object
     */
    public MailContent strong() {
        return tag("strong");
    }

    /**
     * Adds a {@code <strong>} block with text content. The plain text version will
     * get only the content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent strong(String content) {
        return tag("strong").text(content).end();
    }

    /**
     * Starts a table. Adds a {@code <table>} tag with 1 px collapsed border.
     *
     * @return the changed mail content object
     */
    public MailContent table() {
        return tag("table", "border='1' cellpadding='0' cellspacing='0'");
    }

    /**
     * Starts a new row. Same as {@code tag("tr")}
     *
     * @return the changed mail content object
     */
    public MailContent row() {
        return tag("tr");
    }

    /**
     * Adds a new row with one column.
     *
     * @param firstCell the first cell text content
     * @return the changed mail content object
     */
    public MailContent row(String firstCell) {
        return tag("tr").cell(firstCell).end();
    }

    /**
     * Adds a new row with two columns.
     *
     * @param firstCell  the first cell text content
     * @param secondCell the second cell text content
     * @return the changed mail content object
     */
    public MailContent row(String firstCell, String secondCell) {
        return tag("tr").cell(firstCell).cell(secondCell).end();
    }

    /**
     * Adds a new row with three columns.
     *
     * @param firstCell  the first cell text content
     * @param secondCell the second cell text content
     * @param thirdCell  the third cell text content
     * @return the changed mail content object
     */
    public MailContent row(String firstCell,
                           String secondCell,
                           String thirdCell) {
        return tag("tr").cell(firstCell).cell(secondCell).cell(thirdCell).end();
    }

    /**
     * Adds a new row with four columns.
     *
     * @param firstCell  the first cell text content
     * @param secondCell the second cell text content
     * @param thirdCell  the third cell text content
     * @param fourthCell the four cell text content
     * @return the changed mail content object
     */
    public MailContent row(String firstCell,
                           String secondCell,
                           String thirdCell,
                           String fourthCell) {
        return tag("tr").cell(firstCell)
                .cell(secondCell)
                .cell(thirdCell)
                .cell(fourthCell)
                .end();
    }

    /**
     * Starts a new cell. Same as {@code tag("td")}
     *
     * @return the changed mail content object
     */
    public MailContent cell() {
        return tag("td");
    }

    /**
     * Adds a new cell with text content.
     *
     * @param content the text content
     * @return the changed mail content object
     */
    public MailContent cell(String content) {
        return tag("td").text(content).end();
    }
}
