package net.sargue.mailgun.test;

import com.google.common.collect.Lists;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.MailBuilder;
import net.sargue.mailgun.attachment.Attachment;
import net.sargue.mailgun.content.Body;
import net.sargue.mailgun.content.Builder;
import net.sargue.mailgun.test.attachment.TestAttachment;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestMailBuilder {

    private static final String FULL_ADDRESS = "Doc Brown <doc@delorean.com>";
    private static final String NAME         = "Doc Brown";
    private static final String EMAIL        = "doc@delorean.com";

    @Test
    public void constructor() {
        Configuration configuration = new Configuration();
        MailBuilder mailBuilder = new MailBuilder(configuration);

        assertSame(configuration, mailBuilder.configuration());
        assertTrue(mailBuilder.parameters().keySet().isEmpty());
        assertTrue(mailBuilder.attachments().isEmpty());
    }

    @Test
    public void factoryMethod() {
        Configuration configuration = new Configuration();
        MailBuilder mailBuilder = MailBuilder.using(configuration);

        assertSame(configuration, mailBuilder.configuration());
        assertTrue(mailBuilder.parameters().keySet().isEmpty());
        assertTrue(mailBuilder.attachments().isEmpty());
    }

    @Test
    public void mailFactory() {
        Configuration configuration = new Configuration();
        MailBuilder mailBuilder = Mail.using(configuration);

        assertSame(configuration, mailBuilder.configuration());
    }

    @Test
    public void from() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .from(FULL_ADDRESS);

        assertIterableEquals(Collections.singletonList(FULL_ADDRESS),
                             mailBuilder.parameter("from"));
    }

    @Test
    public void fromSplit() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .from(NAME, EMAIL);

        assertIterableEquals(Collections.singletonList(FULL_ADDRESS),
                             mailBuilder.parameter("from"));
    }

    @Test
    public void to() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .to(FULL_ADDRESS);

        assertIterableEquals(Collections.singletonList(FULL_ADDRESS),
                             mailBuilder.parameter("to"));
    }

    @Test
    public void toSplit() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .to(NAME, EMAIL);

        assertIterableEquals(Collections.singletonList(FULL_ADDRESS),
                             mailBuilder.parameter("to"));
    }
    
    @Test
    public void cc() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .cc(FULL_ADDRESS);

        assertIterableEquals(Collections.singletonList(FULL_ADDRESS),
                             mailBuilder.parameter("cc"));
    }

    @Test
    public void ccSplit() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .cc(NAME, EMAIL);

        assertIterableEquals(Collections.singletonList(FULL_ADDRESS),
                             mailBuilder.parameter("cc"));
    }
    
    @Test
    public void bcc() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .bcc(FULL_ADDRESS);

        assertIterableEquals(Collections.singletonList(FULL_ADDRESS),
                             mailBuilder.parameter("bcc"));
    }

    @Test
    public void bccSplit() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .bcc(NAME, EMAIL);

        assertIterableEquals(Collections.singletonList(FULL_ADDRESS),
                             mailBuilder.parameter("bcc"));
    }

    @Test
    public void replyTo() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .replyTo(EMAIL);

        assertIterableEquals(Collections.singletonList(EMAIL),
                             mailBuilder.parameter("h:Reply-To"));
    }

    @Test
    public void subject() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .subject("hello world");

        assertIterableEquals(Collections.singletonList("hello world"),
                             mailBuilder.parameter("subject"));
    }

    @Test
    public void text() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .text("hello world");

        assertIterableEquals(Collections.singletonList("hello world"),
                             mailBuilder.parameter("text"));
    }

    @Test
    public void html() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .html("hello world");

        assertIterableEquals(Collections.singletonList("hello world"),
                             mailBuilder.parameter("html"));
    }

    @Test
    public void content() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration());
        Body content = Body.builder().build();
        mailBuilder.content(content);

        assertIterableEquals(Collections.singletonList(content.text()),
                             mailBuilder.parameter("text"));
        assertIterableEquals(Collections.singletonList(content.html()),
                             mailBuilder.parameter("html"));
    }

    @Test
    public void body() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration());
        Builder builder = mailBuilder.body();

        assertSame(mailBuilder, builder.mail());
    }

    @Test
    public void parameter() {
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .parameter("aKey", "aValue");

        assertIterableEquals(Collections.singletonList("aValue"),
                             mailBuilder.parameter("aKey"));
    }

    @Test
    public void fileAttachment() {
        File file = new File("aFile");
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .attachment(file);

        List<Attachment> attachments = mailBuilder.attachments();
        assertEquals(1, attachments.size());
        Attachment attachment = attachments.get(0);
        TestAttachment.assertAttachment(file, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertNull(attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void inputStreamAttachment() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .attachment(is);

        List<Attachment> attachments = mailBuilder.attachments();
        assertEquals(1, attachments.size());
        Attachment attachment = attachments.get(0);
        TestAttachment.assertAttachment(is, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertNull(attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void inputStreamWithNameAttachment() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .attachment(is, "aName");

        List<Attachment> attachments = mailBuilder.attachments();
        assertEquals(1, attachments.size());
        Attachment attachment = attachments.get(0);
        TestAttachment.assertAttachment(is, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertEquals("aName", attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void inputStreamWithNameAndMediaTypeAttachment() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .attachment(is, "aName", "aMediaType");

        List<Attachment> attachments = mailBuilder.attachments();
        assertEquals(1, attachments.size());
        Attachment attachment = attachments.get(0);
        TestAttachment.assertAttachment(is, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertEquals("aName", attachment.fileName());
        assertEquals("aMediaType", attachment.mediaType());
    }

    @Test
    public void stringAttachment() {
        String content = "hello world";
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .attachment(content, "aFileName");

        List<Attachment> attachments = mailBuilder.attachments();
        assertEquals(1, attachments.size());
        Attachment attachment = attachments.get(0);
        TestAttachment.assertAttachment(content, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertEquals("aFileName", attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void inlineInputStreamAttachment() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .inline(is, "aCidName");

        List<Attachment> attachments = mailBuilder.attachments();
        assertEquals(1, attachments.size());
        Attachment attachment = attachments.get(0);
        TestAttachment.assertAttachment(is, attachment);
        assertEquals("inline", attachment.bodyPartName());
        assertEquals("aCidName", attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void inlineFileAttachment() {
        File file = new File("aFile");
        MailBuilder mailBuilder = MailBuilder.using(new Configuration())
                                             .inline(file, "aCidName");

        List<Attachment> attachments = mailBuilder.attachments();
        assertEquals(1, attachments.size());
        Attachment attachment = attachments.get(0);
        TestAttachment.assertAttachment(file, attachment);
        assertEquals("inline", attachment.bodyPartName());
        assertEquals("aCidName", attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void build() {
        Configuration configuration = new Configuration();
        configuration.addDefaultParameter("defKey", "defValue1");
        configuration.addDefaultParameter("defKey", "defValue2");
        MailBuilder mailBuilder = MailBuilder.using(configuration);
        Mail mail = mailBuilder.build();

        assertEquals(1, mail.parameterKeySet().size());
        assertTrue(mail.getFirstValue("defKey").isPresent());
        assertEquals("defValue1", mail.getFirstValue("defKey").get());
        assertEquals(2, mail.getValues("defKey").size());
        assertIterableEquals(Lists.newArrayList("defValue1", "defValue2"),
                             mail.getValues("defKey"));
        assertSame(configuration, mail.configuration());
    }
}
