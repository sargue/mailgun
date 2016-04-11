package net.sargue.mailgun.test;

import net.sargue.mailgun.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTests {
    @Test
    public void testConfigurationFrom() {
        String from = "Doc Brown <doc@delorean.com>";
        Configuration configuration = new Configuration().from(from);
        assertEquals(from, configuration.from());
    }

    @Test
    public void testConfigurationSplitFrom() {
        Configuration configuration =
            new Configuration().from("Doc Brown", "doc@delorean.com");
        assertEquals("Doc Brown <doc@delorean.com>", configuration.from());
    }

    @Test
    public void testConfigurationConstructor() {
        String domain = "example.com";
        String key = "1234";
        String from = "Doc Brown <doc@delorean.com>";
        Configuration configuration = new Configuration(domain, key, from);
        assertEquals(domain, configuration.domain());
        assertEquals(key, configuration.apiKey());
        assertEquals(from, configuration.from());
    }

    @Test
    public void testConfigurationURL() {
        Configuration configuration = new Configuration().apiUrl("anotherURL");
        assertEquals("anotherURL", configuration.apiUrl());
    }
}
