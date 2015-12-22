package net.sargue.mailgun.test;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.MailBuilder;
import net.sargue.mailgun.MailRequestCallback;
import net.sargue.mailgun.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class BasicTests {
    private static Configuration configuration;
    private static String toName, toEmail;

    @BeforeClass
    public static void init() throws IOException {
        /* can also be bootstrapped like this...
        configuration = new Configuration()
                .domain("somedomain.com")
                .apiKey("key-xxxxxxxxxxxxxxxxxxxxxxxxx")
                .from("Test account", "postmaster@somedomain.com");
        */

        try (FileReader reader = new FileReader("mailgun-test.properties")) {
            Properties properties = new Properties();
            properties.load(reader);
            configuration = new Configuration()
                    .domain(properties.getProperty("domain"))
                    .apiKey(properties.getProperty("apiKey"))
                    .from(properties.getProperty("fromName"),
                          properties.getProperty("fromEmail"));
            toName = properties.getProperty("toName");
            toEmail = properties.getProperty("toEmail");
        }
    }

    @Test
    public void sendBasicText() {
        Response response = MailBuilder.using(configuration)
                .to(toName, toEmail)
                .subject("This is a plain text test")
                .text("Hello world!")
                .build()
                .send();
        Assert.assertTrue(response.isOk());
        Assert.assertEquals(Response.ResponseType.OK,
                            response.responseType());
        Assert.assertEquals(200, response.responseCode());
    }

    @Test
    public void sendBasicTestMode() {
        Response response = MailBuilder.using(configuration)
                .from("firstrandom@address.com")
                .from("Random account", "random@domain.com")
                .to("doc@delorean.com")
                .cc("onecc@example.com")
                .cc("Named CC", "another@example.com")
                .bcc("noone@example.com")
                .bcc("Named BCC", "nobody@example.com")
                .parameter("o:testmode", "yes")
                .build()
                .send();
        Assert.assertTrue(response.isOk());
    }

    @Test
    public void sendBasicHTML() {
        Response response = MailBuilder.using(configuration)
                .to(toName, toEmail)
                .replyTo(configuration.from())
                .subject("This is a text and HTML message")
                .text("Hello world!")
                .html("Hello <strong>world</strong>!")
                .build()
                .send();
        Assert.assertTrue(response.isOk());
    }

    @Test
    public void sendWithAttachment() {
        Response response = MailBuilder.using(configuration)
                .to(toName, toEmail)
                .subject("This message has an text attachment")
                .text("Please find attached some text.")
                .multipart()
                .attachment("This is the content of the attachment",
                            "readme.txt")
                .build()
                .send();
        Assert.assertTrue(response.isOk());
    }

    @Test
    public void sendAsync() {
        MailBuilder.using(configuration)
                .to(toName, toEmail)
                .subject("This is a plain text test")
                .text("Hello world!")
                .build()
                .sendAsync(new MailRequestCallback() {
                    @Override
                    public void completed(Response response) {
                        Assert.assertEquals(Response.ResponseType.OK,
                                            response.responseType());
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        Assert.fail(throwable.getMessage());
                    }
                });
    }
}
