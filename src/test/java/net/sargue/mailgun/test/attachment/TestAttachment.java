package net.sargue.mailgun.test.attachment;

import net.sargue.mailgun.attachment.Attachment;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestAttachment {

    @Test
    public void createFile() {
        File file = new File(".");
        Attachment attachment = Attachment.create(file);
        assertAttachment(file, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertNull(attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void createInputStream() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        Attachment attachment = Attachment.create(is);
        assertAttachment(is, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertNull(attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void createNamedInputStream() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        Attachment attachment = Attachment.create(is, "aName");
        assertAttachment(is, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertEquals("aName", attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void createNamedInputStreamWithMediaType() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        Attachment attachment = Attachment.create(is, "aName", "aMediaType");
        assertAttachment(is, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertEquals("aName", attachment.fileName());
        assertEquals("aMediaType", attachment.mediaType());
    }

    @Test
    public void createString() {
        String content = "hello world";
        Attachment attachment = Attachment.create(content, "aName");
        assertAttachment(content, attachment);
        assertEquals("attachment", attachment.bodyPartName());
        assertEquals("aName", attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void inlineInputStream() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        Attachment attachment = Attachment.inline(is, "aName");
        assertAttachment(is, attachment);
        assertEquals("inline", attachment.bodyPartName());
        assertEquals("aName", attachment.fileName());
        assertNull(attachment.mediaType());
    }

    @Test
    public void inlineFile() {
        File file = new File(".");
        Attachment attachment = Attachment.inline(file, "aName");
        assertAttachment(file, attachment);
        assertEquals("inline", attachment.bodyPartName());
        assertEquals("aName", attachment.fileName());
        assertNull(attachment.mediaType());
    }

    public static void assertAttachment(File file, Attachment attachment) {
        assertTrue(attachment.isFile());
        assertFalse(attachment.isInputStream());
        assertFalse(attachment.isString());
        assertThrows(IllegalArgumentException.class, attachment::getContentAsInputStream);
        assertThrows(IllegalArgumentException.class, attachment::getContentAsString);
        assertSame(file, attachment.getContentAsFile());
    }

    public static void assertAttachment(InputStream is, Attachment attachment) {
        assertFalse(attachment.isFile());
        assertTrue(attachment.isInputStream());
        assertFalse(attachment.isString());
        assertThrows(IllegalArgumentException.class, attachment::getContentAsFile);
        assertSame(is, attachment.getContentAsInputStream());
        assertThrows(IllegalArgumentException.class, attachment::getContentAsString);
    }

    public static void assertAttachment(String content, Attachment attachment) {
        assertFalse(attachment.isFile());
        assertFalse(attachment.isInputStream());
        assertTrue(attachment.isString());
        assertThrows(IllegalArgumentException.class, attachment::getContentAsFile);
        assertThrows(IllegalArgumentException.class, attachment::getContentAsInputStream);
        assertEquals(content, attachment.getContentAsString());
    }
}
