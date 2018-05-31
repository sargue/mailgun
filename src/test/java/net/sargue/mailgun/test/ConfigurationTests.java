package net.sargue.mailgun.test;

import com.google.common.collect.Lists;
import net.sargue.mailgun.*;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class ConfigurationTests {

    public static final String FROM = "Doc Brown <doc@delorean.com>";

    @Test
    public void testConfigurationFrom() {
        Configuration configuration = new Configuration().from(FROM);
        assertEquals(FROM, configuration.from());
    }

    @Test
    public void testConfigurationSplitFrom() {
        Configuration configuration =
            new Configuration().from("Doc Brown", "doc@delorean.com");
        assertEquals(FROM, configuration.from());
    }

    @Test
    public void testConfigurationConstructor() {
        String domain = "example.com";
        String key = "1234";
        Configuration configuration = new Configuration(domain, key, FROM);
        assertEquals(domain, configuration.domain());
        assertEquals(key, configuration.apiKey());
        assertEquals(FROM, configuration.from());
    }

    @Test
    public void testConfigurationURL() {
        Configuration configuration = new Configuration().apiUrl("anotherURL");
        assertEquals("anotherURL", configuration.apiUrl());
    }

    @Test
    public void testFromDefault() {
        Configuration configuration = new Configuration()
            .from(FROM);
        assertEquals(configuration.defaultParameters().get("from"),
                     Collections.singletonList(FROM));
    }

    @Test
    public void testFromSingleValue() {
        Configuration configuration = new Configuration()
            .from("from1")
            .from(FROM);
        assertEquals(configuration.defaultParameters().get("from"),
                     Collections.singletonList(FROM));
    }

    @Test
    public void testCustomDefault() {
        Configuration configuration = new Configuration()
            .addDefaultParameter("foo", "bar");
        assertEquals(configuration.defaultParameters().get("foo"),
                     Collections.singletonList("bar"));
    }

    @Test
    public void testCustomDefaultMultiValue() {
        Configuration configuration = new Configuration()
            .addDefaultParameter("foo", "bar")
            .addDefaultParameter("foo", "bar2");
        assertEquals(configuration.defaultParameters().get("foo"),
                     Lists.newArrayList("bar", "bar2"));
    }

    @Test
    public void testResetDefault() {
        Configuration configuration = new Configuration()
            .addDefaultParameter("foo", "bar")
            .addDefaultParameter("foo", "bar2")
            .clearDefaultParameter("foo");
        assertFalse(configuration.defaultParameters().containsKey("foo"));
        assertNull(configuration.defaultParameters().get("foo"));
    }

    @Test
    public void testNoCallbackConfigured() {
        Configuration configuration = new Configuration();

        assertNull(configuration.mailRequestCallbackFactory());
    }

    @Test
    public void testCallbackConfigured() {
        final MailRequestCallback mailRequestCallback = new MailRequestCallback() {
            @Override
            public void completed(Response response) { /*no op*/ }

            @Override
            public void failed(Throwable throwable) { /*no op*/ }
        };

        MailRequestCallbackFactory factory = new MailRequestCallbackFactory() {
            @Override
            public MailRequestCallback create(Mail mail) {
                return mailRequestCallback;
            }
        };

        Configuration configuration = new Configuration()
            .registerMailRequestCallbackFactory(factory);

        assertSame(factory, configuration.mailRequestCallbackFactory());
        assertSame(mailRequestCallback,
                   configuration.mailRequestCallbackFactory().create(null));
    }

    @Test
    public void testCallbackUnregister() {
        Configuration configuration = new Configuration()
            .registerMailRequestCallbackFactory(new MailRequestCallbackFactory() {
                @Override
                public MailRequestCallback create(Mail mail) {
                    return null;
                }
            })
            .unregisterMailRequestCallbackFactory();

        assertNull(configuration.mailRequestCallbackFactory());
    }
}
