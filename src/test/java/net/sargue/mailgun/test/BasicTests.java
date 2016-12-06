package net.sargue.mailgun.test;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.MailBuilder;
import net.sargue.mailgun.MailRequestCallback;
import net.sargue.mailgun.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class BasicTests {
    private static final String DOMAIN = "somedomain.com";
    private static final int PORT = 8124;
    private static final String FROM_EMAIL = "mockingyou@somedomain.com";
    private static final String FROM_NAME = "Test account";
    private static Configuration configuration;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    private static String expectedAuthHeader;

    @BeforeClass
    public static void init() throws IOException {
        configuration = new Configuration()
            .apiUrl("http://localhost:" + PORT + "/api")
            .domain(DOMAIN)
            .apiKey("key-thisisagibberishlongstring")
            .from(FROM_NAME, FROM_EMAIL);
        String userpass = "api:" + configuration.apiKey();
        expectedAuthHeader = Base64.encodeBase64String(userpass.getBytes());
    }

    private MappingBuilder expectedBasicPost() {
        return post(urlEqualTo("/api/" + DOMAIN + "/messages"))
            .withHeader("Authorization", equalTo("Basic " + expectedAuthHeader))
            .withHeader("Content-Type",
                        equalTo("application/x-www-form-urlencoded"));
    }

    private String mail(String name, String email) {
        return name + " <" + email + ">";
    }

    private BasicNameValuePair param(String name, String value) {
        return new BasicNameValuePair(name, value);
    }

    private void verifyMessageSent(List<NameValuePair> parametersList) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.addAll(parametersList);
        boolean fromFound = false;
        for (int i = 0; i < parameters.size() && !fromFound; i++)
            fromFound = parameters.get(i).getName().equals("from");
        if (!fromFound)
            parameters.add(param("from", mail(FROM_NAME, FROM_EMAIL)));
        String form = URLEncodedUtils.format(parameters, "UTF-8");
        verify(postRequestedFor(urlEqualTo("/api/somedomain.com/messages"))
                   .withRequestBody(equalTo(form)));
    }

    private void verifyMessageSent(NameValuePair... parameters) {
        verifyMessageSent(Arrays.asList(parameters));
    }

    @Test
    public void basicText() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Response response = MailBuilder.using(configuration)
            .to("marty@mcfly.com")
            .subject("This is a plain text test")
            .text("Hello world!")
            .build()
            .send();

        assertTrue(response.isOk());
        assertEquals(Response.ResponseType.OK,
                     response.responseType());
        assertEquals(200, response.responseCode());

        verifyMessageSent(
            param("to", "marty@mcfly.com"),
            param("subject", "This is a plain text test"),
            param("text", "Hello world!")
        );
    }

    @Test
    public void withCustomFrom() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Response response = MailBuilder.using(configuration)
            .from("Doc Brown", "doc@delorean.com")
            .to("marty@mcfly.com")
            .subject("This is a plain text test")
            .text("Hello world!")
            .build()
            .send();

        assertTrue(response.isOk());

        verifyMessageSent(
            param("from", mail("Doc Brown", "doc@delorean.com")),
            param("to", "marty@mcfly.com"),
            param("subject", "This is a plain text test"),
            param("text", "Hello world!")
        );
    }

    @Test
    public void withNullName() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Response response = MailBuilder.using(configuration)
            .from(null, "doc@delorean.com")
            .to("marty@mcfly.com")
            .subject("This is a plain text test")
            .text("Hello world!")
            .build()
            .send();

        assertTrue(response.isOk());

        verifyMessageSent(
            param("from", "doc@delorean.com"),
            param("to", "marty@mcfly.com"),
            param("subject", "This is a plain text test"),
            param("text", "Hello world!")
        );
    }

    @Test
    public void sendBasicTestMode() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

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
        assertTrue(response.isOk());

        verifyMessageSent(
            param("from", "firstrandom@address.com"),
            param("from", mail("Random account", "random@domain.com")),
            param("to", "doc@delorean.com"),
            param("cc", "onecc@example.com"),
            param("cc", mail("Named CC", "another@example.com")),
            param("bcc", "noone@example.com"),
            param("bcc", mail("Named BCC", "nobody@example.com")),
            param("o:testmode", "yes")
        );
    }

    @Test
    public void sendBasicHTML() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Response response = MailBuilder.using(configuration)
            .to("doc@delorean.com")
            .replyTo(configuration.from())
            .subject("This is a text and HTML message")
            .text("Hello world!")
            .html("Hello <strong>world</strong>!")
            .build()
            .send();
        assertTrue(response.isOk());

        verifyMessageSent(
            param("to", "doc@delorean.com"),
            param("h:Reply-To", mail(FROM_NAME, FROM_EMAIL)),
            param("subject", "This is a text and HTML message"),
            param("text", "Hello world!"),
            param("html", "Hello <strong>world</strong>!")
        );
    }

    @Test
    public void sendWithAttachment() {
        stubFor(post(urlEqualTo("/api/" + DOMAIN + "/messages"))
                    .withHeader("Authorization",
                                equalTo("Basic " + expectedAuthHeader))
                    .withHeader("Content-Type",
                                containing("multipart/form-data"))
                    .willReturn(aResponse().withStatus(200)));

        Response response = MailBuilder.using(configuration)
            .to("doc@delorean.com")
            .subject("This message has an text attachment")
            .text("Please find attached some text.")
            .multipart()
            .attachment("This is the content of the attachment",
                        "readme.txt")
            .build()
            .send();
        assertTrue(response.isOk());

        //TODO proper content checking
    }

    @Test
    public void sendWithInlineAttachment() {
        stubFor(post(urlEqualTo("/api/" + DOMAIN + "/messages"))
                .withHeader("Authorization",
                            equalTo("Basic " + expectedAuthHeader))
                .withHeader("Content-Type",
                            containing("multipart/form-data"))
                .willReturn(aResponse().withStatus(200)));

        Response response = MailBuilder.using(configuration)
                .to("doc@delorean.com")
                .subject("This message has an text attachment")
                .html("<html>Inline image here: <img src=\"cid:cartman.jpg\"></html>")
                .multipart()
                .inline(new ByteArrayInputStream("MockBytes".getBytes()), "cartman.jpg")
                .build()
                .send();
        assertTrue(response.isOk());

        //TODO proper content checking
    }

    @Test
    public void sendAsync() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        MailBuilder.using(configuration)
            .to("doc@delorean.com")
            .subject("This is a plain text test")
            .text("Hello world!")
            .build()
            .sendAsync(new MailRequestCallback() {
                @Override
                public void completed(Response response) {
                    assertEquals(Response.ResponseType.OK,
                                 response.responseType());

                    verifyMessageSent(
                        param("to", "doc@delorean.com"),
                        param("subject", "This is a plain text test"),
                        param("text", "Hello world!")
                    );
                }

                @Override
                public void failed(Throwable throwable) {
                    fail(throwable.getMessage());
                }
            });
    }

    @Test
    public void sendAsyncFireAndForget() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));
        MailBuilder.using(configuration)
                   .to("doc@delorean.com")
                   .subject("This is a plain text test")
                   .text("Hello world!")
                   .build()
                   .sendAsync();

        final RequestPatternBuilder postRequestedFor = postRequestedFor(
            urlEqualTo("/api/somedomain.com/messages"));
        await().atMost(5, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !wireMockRule.findAll(postRequestedFor).isEmpty();
            }
        });

        verify(postRequestedFor);
    }

    @Test
    public void responsePayloadTest() {
        String responseMessage =
            "{ \"id\": \"" +
            "<20160902095021.16212.7900.87F2C8F1@mydomain.com>" + "\", " +
            "\"message\": \"" + "Queued. Thank you." + "\" }";
        stubFor(expectedBasicPost().willReturn(
            aResponse()
                .withStatus(200)
                .withBody(responseMessage)
        ));

        Response response = MailBuilder
            .using(configuration)
            .to("doc@delorean.com")
            .subject("This is a plain text test")
            .text("Hello world!")
            .build()
            .send();

        assertEquals(responseMessage, response.responseMessage());
    }
}
