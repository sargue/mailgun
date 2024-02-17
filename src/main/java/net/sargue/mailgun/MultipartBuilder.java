package net.sargue.mailgun;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * A mutable builder for a MIME multipart message. It has the capability
 * to handle attachments.
 * <p>
 * It is obtained from the main {@link MailBuilder} through the method
 * {@code multipart()}.
 */
@SuppressWarnings("unused")
public class MultipartBuilder {
    private static final String ATTACHMENT_NAME = "attachment";

    private final Configuration configuration;
    private final FormDataMultiPart form = new FormDataMultiPart();

    MultipartBuilder(MailBuilder mailBuilder) {
        configuration = mailBuilder.configuration();

        MultivaluedMap<String, String> map = mailBuilder.form().asMap();
        for (Map.Entry<String, List<String>> entry : map.entrySet())
            for (String value : entry.getValue())
                form.field(entry.getKey(), value);
    }

    /**
     * Adds an attachment from a {@link File}.
     *
     * @param file a file to attach
     * @return this builder
     */
    public MultipartBuilder attachment(File file) {
        return bodyPart(new FileDataBodyPart(ATTACHMENT_NAME, file));
    }

    /**
     * Adds an attachment from a {@link InputStream}.
     *
     * @param is an stream to read the attachment
     * @return this builder
     */
    public MultipartBuilder attachment(InputStream is) {
        return bodyPart(new StreamDataBodyPart(ATTACHMENT_NAME, is));
    }

    /**
     * Adds a named attachment.
     *
     * @param is       an stream to read the attachment
     * @param filename the filename to give to the attachment
     * @return this builder
     */
    public MultipartBuilder attachment(InputStream is, String filename) {
        return bodyPart(new StreamDataBodyPart(ATTACHMENT_NAME, is, filename));
    }

    /**
     * Adds a named attachment with a custom MIME media type.
     *
     * @param is        an stream to read the attachment
     * @param filename  the filename to give to the attachment
     * @param mediaType the media type of the attachment
     * @return this builder
     */
    public MultipartBuilder attachment(InputStream is, String filename,
                                       MediaType mediaType) {
        return bodyPart(new StreamDataBodyPart(ATTACHMENT_NAME, is, filename,
                                               mediaType));
    }

    /**
     * Adds an attachment directly by content.
     *
     * @param content the content of the attachment
     * @param filename the filename of the attachment
     * @return this builder
     */
    public MultipartBuilder attachment(String content, String filename) {
        ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
        return bodyPart(new StreamDataBodyPart(ATTACHMENT_NAME, is, filename));
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
     */
    public MultipartBuilder inline(InputStream is, String cidName) {
        return bodyPart(new StreamDataBodyPart("inline", is, cidName));
    }

    /**
     * Finishes the building phase and returns a {@link Mail}.
     * <p>
     * This builder should not be used after invoking this method.
     *
     * @return a {@link Mail} built from this builder
     */
    public Mail build() {
        return new MailMultipart(configuration, form);
    }

    private MultipartBuilder bodyPart(BodyPart bodyPart) {
        form.bodyPart(bodyPart);
        return this;
    }
}
