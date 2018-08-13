package net.sargue.mailgun.test;

import net.sargue.mailgun.MailgunException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TestMailgunException {
    @Test
    void messageOnly() {
        MailgunException exception = new MailgunException("aMessage");

        assertEquals("aMessage", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void withCause() {
        Exception cause = new Exception();
        MailgunException exception = new MailgunException("aMessage", cause);

        assertEquals("aMessage", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
