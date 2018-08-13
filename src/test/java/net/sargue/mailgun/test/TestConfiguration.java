package net.sargue.mailgun.test;

import com.google.common.collect.Lists;
import net.sargue.mailgun.*;
import net.sargue.mailgun.content.ContentConverter;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TestConfiguration {

    private static final String FROM = "Doc Brown <doc@delorean.com>";

    @Test
    void emptyConstructor() {
        Configuration configuration = new Configuration();

        assertEquals(MailgunRegion.US, configuration.region());
        assertEquals(MailgunRegion.US.apiUrl(), configuration.apiUrl());
        assertEquals(0, configuration.connectTimeout());
        assertEquals(0, configuration.readTimeout());
        assertTrue(configuration.defaultParameters().keySet().isEmpty());
        assertFalse(configuration.mailRequestCallbackFactory().isPresent());
        assertTrue(configuration.mailSendFilter().filter(null));
    }

    @Test
    void configurationConstructor() {
        String domain = "example.com";
        String key = "1234";
        Configuration configuration = new Configuration(domain, key, FROM);

        assertEquals(domain, configuration.domain());
        assertEquals(key, configuration.apiKey());
        assertTrue(configuration.from().isPresent());
        assertEquals(FROM, configuration.from().get());
    }

    @Test
    void region() {
        Configuration configuration = new Configuration();
        Configuration c = configuration.region(MailgunRegion.EU);

        assertSame(configuration, c);
        assertEquals(MailgunRegion.EU, configuration.region());
        assertEquals(MailgunRegion.EU.apiUrl(), configuration.apiUrl());
    }

    @Test
    void domain() {
        Configuration configuration = new Configuration();
        Configuration c = configuration.domain("delorean.com");

        assertSame(configuration, c);
        assertEquals("delorean.com", configuration.domain());
    }

    @Test
    void apiKey() {
        Configuration configuration = new Configuration();
        Configuration c = configuration.apiKey("very_secret_key");

        assertSame(configuration, c);
        assertEquals("very_secret_key", configuration.apiKey());
    }

    @Test
    void from() {
        Configuration configuration = new Configuration().from(FROM);

        assertTrue(configuration.from().isPresent());
        assertEquals(FROM, configuration.from().get());
    }

    @Test
    void splitFrom() {
        Configuration configuration =
                new Configuration().from("Doc Brown", "doc@delorean.com");

        assertTrue(configuration.from().isPresent());
        assertEquals(FROM, configuration.from().get());
    }

    @Test
    void apiUrl() {
        Configuration configuration = new Configuration().apiUrl("anotherURL");

        assertEquals("anotherURL", configuration.apiUrl());
    }

    @Test
    void fullUrl() {
        Configuration configuration = new Configuration().apiUrl("https://localhost")
                                                         .domain("delorean.com");

        assertEquals("https://localhost/delorean.com/messages", configuration.fullUrl());
    }

    @Test
    void fullUrlWithSlash() {
        Configuration configuration = new Configuration().apiUrl("https://localhost/")
                                                         .domain("delorean.com");

        assertEquals("https://localhost/delorean.com/messages", configuration.fullUrl());
    }

    @Test
    void connectTimeout() {
        Configuration configuration = new Configuration().connectTimeout(42);

        assertEquals(42, configuration.connectTimeout());
    }

    @Test
    void readTimeout() {
        Configuration configuration = new Configuration().readTimeout(42);

        assertEquals(42, configuration.readTimeout());
    }

    @Test
    void addDefaultParameter() {
        Configuration configuration = new Configuration()
                .addDefaultParameter("foo", "bar");

        assertIterableEquals(configuration.defaultParameters().getValues("foo"),
                             Collections.singletonList("bar"));
    }

    @Test
    void addDefaultParameterMultiValue() {
        Configuration configuration = new Configuration()
                .addDefaultParameter("foo", "bar")
                .addDefaultParameter("foo", "bar2");

        assertIterableEquals(configuration.defaultParameters().getValues("foo"),
                             Lists.newArrayList("bar", "bar2"));
    }

    @Test
    void testFromDefault() {
        Configuration configuration = new Configuration()
                .from(FROM);
        assertIterableEquals(configuration.defaultParameters().getValues("from"),
                             Collections.singletonList(FROM));
    }

    @Test
    void testFromSingleValue() {
        Configuration configuration = new Configuration()
                .from("from1")
                .from(FROM);
        assertIterableEquals(configuration.defaultParameters().getValues("from"),
                             Collections.singletonList(FROM));
    }

    @Test
    void testResetDefault() {
        Configuration configuration = new Configuration()
                .addDefaultParameter("foo", "bar")
                .addDefaultParameter("foo", "bar2")
                .clearDefaultParameter("foo");

        assertFalse(configuration.defaultParameters().containsKey("foo"));
        assertTrue(configuration.defaultParameters().getValues("foo").isEmpty());
    }

    @Test
    void registerMailRequestCallbackFactory() {
        final MailRequestCallback mailRequestCallback = new MailRequestCallback() {
            @Override
            public void completed(Response response) { /*no op*/ }

            @Override
            public void failed(Throwable throwable) { /*no op*/ }
        };

        MailRequestCallbackFactory factory = mail -> mailRequestCallback;

        Configuration configuration = new Configuration()
                .registerMailRequestCallbackFactory(factory);

        assertTrue(configuration.mailRequestCallbackFactory().isPresent());
        assertSame(factory, configuration.mailRequestCallbackFactory().get());
        assertSame(mailRequestCallback,
                   configuration.mailRequestCallbackFactory().get().create(null));
    }

    @Test
    void unregisterMailRequestCallbackFactory() {
        Configuration configuration = new Configuration()
                .registerMailRequestCallbackFactory(mail -> null)
                .unregisterMailRequestCallbackFactory();

        assertFalse(configuration.mailRequestCallbackFactory().isPresent());
    }

    @Test
    void registerMailSendFilter() {
        MailSendFilter filter = mail -> false;
        Configuration configuration = new Configuration().registerMailSendFilter(filter);

        assertSame(filter, configuration.mailSendFilter());
    }

    @Test
    void restClientAdapter() {
        RestClientAdapter restClientAdapter = new RestClientAdapter() {
            @Override
            public Mail build(MailBuilder mailBuilder) {
                return null;
            }

            @Override
            public void close() {
                //no op
            }
        };
        Configuration configuration = new Configuration().restClientAdapter(restClientAdapter);

        assertSame(restClientAdapter, configuration.restClientAdapter());
    }

    @Test
    void registerConverter() {
        ContentConverter<String> contentConverter = String::trim;
        Configuration configuration = new Configuration().registerConverter(contentConverter,
                                                                            String.class);

        assertSame(contentConverter, configuration.converter(String.class));
    }

    @Test
    void doubleClose() {
        Configuration configuration = new Configuration();

        configuration.close();
        configuration.close();
    }
}
