package net.sargue.mailgun.attachment;

import net.sargue.mailgun.MailBuilder;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import static net.sargue.mailgun.attachment.Disposition.ATTACHMENT;
import static net.sargue.mailgun.attachment.Disposition.INLINE;

/**
 * A mail attachment.
 * <p>
 * An attachment has a {@link Disposition} which defines the parameter name to use on the
 * Mailgun service and a content. Also, optionally, a file name and a MIME media type.
 * <p>
 * From the point of view of the Mailgun service an attachment is just another parameter
 * which in turn is just a a key (parameter name) and content which is usually a String
 * but with parameters is more complex and usually binary data. Also, the Mailgun service
 * requires that attachments be sent using <tt>form/multi-part</tt> encoding.
 * <p>
 * This class and its subclasses help store this data in the {@link MailBuilder} with all
 * the necessari metadata that common parameters lack.
 *
 * @since 2.0
 */
public abstract class Attachment {
    private Disposition disposition;
    private String fileName;
    private String mediaType;

    Attachment(Disposition disposition, String fileName, String mediaType) {
        this.disposition = Objects.requireNonNull(disposition);
        this.fileName = fileName;
        this.mediaType = mediaType;
    }

    /**
     * Buids an attachment from a File, with no name and no custom media type.
     *
     * @param file the file to attach
     * @return the attachment containing the file
     */
    public static Attachment create(File file) {
        return new FileAttachment(ATTACHMENT, null, null, file);
    }

    /**
     * Buids an attachment from an input stream, with no name and no custom media type.
     *
     * @param is the data to attach
     * @return the attachment containing the data
     */
    public static Attachment create(InputStream is) {
        return new InputStreamAttachment(ATTACHMENT, null, null, is);
    }

    /**
     * Buids a named attachment from an input stream, with no custom media type.
     *
     * @param is       the data to attach
     * @param filename the filename to give to the attachment
     * @return the attachment containing the data
     */
    public static Attachment create(InputStream is, String filename) {
        return new InputStreamAttachment(ATTACHMENT, filename, null, is);
    }

    /**
     * Buids a named attachment from an input stream with an specific MIME media type.
     *
     * @param is        the data to attach
     * @param filename  the filename to give to the attachment
     * @param mediaType the media type of the content
     * @return the attachment containing the data
     */
    public static Attachment create(InputStream is, String filename, String mediaType) {
        return new InputStreamAttachment(ATTACHMENT, filename, mediaType, is);
    }

    /**
     * Buids a named attachment from String, with no custom media type.
     *
     * @param content  the data to attach
     * @param filename the filename to give to the attachment
     * @return the attachment containing the data
     */
    public static Attachment create(String content, String filename) {
        return new StringAttachment(ATTACHMENT, filename, null, content);
    }

    /**
     * Builds an inline attachment with a given name.
     * <p>
     * This could be useful for images, for example, which can be referenced from the
     * HTML body part.
     * <p>
     * Example:
     * <p>
     * {@code
     *
     *
     *
     * }
     *
     * @param is the content of the inline attachment
     * @param cidName the name
     * @return the inline attachment
     */
    public static Attachment inline(InputStream is, String cidName) {
        return new InputStreamAttachment(INLINE, cidName, null, is);
    }

    public static Attachment inline(File file, String cidName) {
        return new FileAttachment(INLINE, cidName, null, file);
    }

    public String bodyPartName() {
        return disposition.name().toLowerCase();
    }

    public String fileName() {
        return fileName;
    }

    public String mediaType() {
        return mediaType;
    }

    public boolean isFile() {
        return false;
    }

    public boolean isInputStream() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public File getContentAsFile() {
        throw new IllegalArgumentException("Content not a File.");
    }

    public InputStream getContentAsInputStream() {
        throw new IllegalArgumentException("Content not a InputStream.");
    }

    public String getContentAsString() {
        throw new IllegalArgumentException("Content not a String.");
    }
}
