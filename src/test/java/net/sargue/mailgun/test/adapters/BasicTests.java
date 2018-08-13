package net.sargue.mailgun.test.adapters;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.collect.Lists;
import net.sargue.mailgun.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.collect.Lists.newArrayList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
public abstract class BasicTests {
    private static final Logger log = LoggerFactory.getLogger(BasicTests.class);

    private static final String         DOMAIN     = "somedomain.com";
    private static final int            PORT       = 8124;
    private static final String         FROM_EMAIL = "mockingyou@somedomain.com";
    private static final String         FROM_NAME  = "Test account";
    private static       WireMockServer wireMockServer;

    private Configuration configuration;
    private String        expectedAuthHeader;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(wireMockConfig().port(PORT));
        wireMockServer.start();
        WireMock.configureFor(PORT);
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @BeforeEach
    void beforeEach() throws ClassNotFoundException {
        configuration = new Configuration()
                .apiUrl("http://localhost:" + PORT + "/api")
                .domain(DOMAIN)
                .apiKey("key-thisisagibberishlongstring")
                .from(FROM_NAME, FROM_EMAIL);
        configuration.restClientAdapter(restClientAdapter(configuration));
        String userpass = "api:" + configuration.apiKey();
        expectedAuthHeader = Base64.encodeBase64String(userpass.getBytes());
        wireMockServer.resetAll();
    }

    protected abstract RestClientAdapter restClientAdapter(Configuration configuration) throws ClassNotFoundException;

    private MappingBuilder expectedBasicPost() {
        return post(urlPathEqualTo("/api/" + DOMAIN + "/messages"))
            .withHeader("Authorization", equalTo("Basic " + expectedAuthHeader));
    }

    private String mail(String name, String email) {
        return name + " <" + email + ">";
    }

    private NameValuePair param(String name, String value) {
        return new BasicNameValuePair(name, value);
    }

    private void verifyMessageSent(List<NameValuePair> expected) {
        boolean fromFound = expected.stream()
                                    .map(NameValuePair::getName)
                                    .anyMatch(name -> name.equals("from"));
        if (!fromFound)
            expected.add(param("from", mail(FROM_NAME, FROM_EMAIL)));

        LoggedRequest req = wireMockServer.getAllServeEvents().get(0).getRequest();

        try {
            if ("application/x-www-form-urlencoded".equals(req.contentTypeHeader().mimeTypePart()))
                verifyMessageSentForm(expected);
            else
                verifyMessageSentMultipart(expected);
        } catch (VerificationException e) {
            log.info("Request = {}", req);
            throw e;
        }
    }

    private void verifyMessageSentForm(List<NameValuePair> expected) {
        verify(
            requestMadeFor(request -> {
                String body = request.getBodyAsString();
                List<NameValuePair> parameters = URLEncodedUtils.parse(body, UTF_8);
                MatchResult parameterMatch = parameterMatch(parameters, expected);
                if (!parameterMatch.isExactMatch()) {
                    log.info("Parameters don't match. Distance {}.",
                             parameterMatch.getDistance());
                    log.info("Expected parameters: {}", expected);
                    log.info("Received parameters: {}", parameters);
                    log.info("Request = {}", request);
                }
                return MatchResult.aggregate(
                    parameterMatch,
                    MatchResult.of(request.getUrl().equals("/api/" + DOMAIN + "/messages"))
                );
            })
        );
    }

    private void verifyMessageSentMultipart(List<NameValuePair> expected) {
        RequestPatternBuilder builder = postRequestedFor(urlEqualTo("/api/" + DOMAIN + "/messages"));
        for (NameValuePair pair : expected)
            withRequestBodyParameter(builder, pair.getName(), pair.getValue());
        verify(builder);
    }

    private void verifyMessageSent(NameValuePair... parameters) {
        verifyMessageSent(Lists.newArrayList(parameters));
    }

    private MatchResult parameterMatch(List<NameValuePair> p1, List<NameValuePair> p2) {
        int distance = 0;
        List<NameValuePair> p2copy = new LinkedList<>(p2);
        for (NameValuePair pair : p1) {
            if (!p2copy.remove(pair))
                distance++;
        }
        distance += p2copy.size();
        return MatchResult.partialMatch(distance);
    }

    private RequestPatternBuilder withRequestBodyParameter(RequestPatternBuilder builder,
                                                           String key,
                                                           String value)
    {
        return builder.withRequestBodyPart(
                aMultipart()
                        .withName(key)
                        .withBody(equalTo(value))
                        .build()
        );
    }

    @Test
    void basicText() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Mail mail = MailBuilder.using(configuration)
                               .to("marty@mcfly.com")
                               .subject("This is a plain text test")
                               .text("Hello world!")
                               .build();
        Response response = mail.send();

        assertTrue(mail.getFirstValue("to").isPresent());
        assertEquals(mail.getFirstValue("to").get(), "marty@mcfly.com");
        assertEquals(mail.getValues("to"), newArrayList("marty@mcfly.com"));
        assertTrue(response.isOk());
        assertEquals(Response.ResponseType.OK, response.responseType());
        assertEquals(200, response.responseCode());

        verifyMessageSent(
            param("to", "marty@mcfly.com"),
            param("subject", "This is a plain text test"),
            param("text", "Hello world!")
        );
    }

    @Test
    void withCustomHeader() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Response response = MailBuilder.using(configuration)
                                       .to("marty@mcfly.com")
                                       .subject("This is a plain text test")
                                       .text("Hello world!")
                                       .parameter("h:CustomHeader", "The header value")
                                       .build()
                                       .send();

        assertTrue(response.isOk());
        assertEquals(Response.ResponseType.OK,
                     response.responseType());
        assertEquals(200, response.responseCode());

        verifyMessageSent(
            param("to", "marty@mcfly.com"),
            param("subject", "This is a plain text test"),
            param("text", "Hello world!"),
            param("h:CustomHeader", "The header value")
        );
    }

    @Test
    void withDefaultParameter() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Configuration cfg = configuration
                .addDefaultParameter("h:sender", "from@default.com");

        Response response = MailBuilder.using(cfg)
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
            param("text", "Hello world!"),
            param("h:sender", "from@default.com")
        );
    }

    @Test
    void withDefaultParameterOverridden() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Configuration cfg = configuration
                .addDefaultParameter("h:sender", "from@default.com");

        Response response = MailBuilder.using(cfg)
                                       .to("marty@mcfly.com")
                                       .subject("This is a plain text test")
                                       .text("Hello world!")
                                       .parameter("h:sender",
                                                  "from@specific.com")
                                       .build()
                                       .send();

        assertTrue(response.isOk());
        assertEquals(Response.ResponseType.OK,
                     response.responseType());
        assertEquals(200, response.responseCode());

        verifyMessageSent(
            param("to", "marty@mcfly.com"),
            param("subject", "This is a plain text test"),
            param("text", "Hello world!"),
            param("h:sender", "from@specific.com")
        );
    }

    @Test
    void withCustomFrom() {
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
    void withNullName() {
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
    void sendBasicTestMode() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        String to = "doc@delorean.com";
        String cc1 = "onecc@example.com";
        String cc2Name = "Named CC";
        String cc2Mail = "another@example.com";
        String bcc1 = "noone@example.com";
        String bcc2Name = "Named BCC";
        String bcc2Mail = "nobody@example.com";
        Mail mail = MailBuilder.using(configuration)
                               .from("firstrandom@address.com")
                               .from("Random account", "random@domain.com")
                               .to(to)
                               .cc(cc1)
                               .cc(cc2Name, cc2Mail)
                               .bcc(bcc1)
                               .bcc(bcc2Name, bcc2Mail)
                               .parameter("o:testmode", "yes")
                               .build();
        Response response = mail.send();

        assertTrue(mail.getFirstValue("to").isPresent());
        assertEquals(mail.getFirstValue("to").get(), to);
        assertEquals(mail.getValues("to"), newArrayList(to));
        assertTrue(mail.getFirstValue("cc").isPresent());
        assertEquals(mail.getFirstValue("cc").get(), cc1);
        assertEquals(mail.getValues("cc"),
                     newArrayList(cc1, cc2Name + " <" + cc2Mail + ">"));
        assertTrue(mail.getFirstValue("bcc").isPresent());
        assertEquals(mail.getFirstValue("bcc").get(), bcc1);
        assertEquals(mail.getValues("bcc"),
                     newArrayList(bcc1, bcc2Name + " <" + bcc2Mail + ">"));
        assertTrue(response.isOk());

        verifyMessageSent(
            param("from", "firstrandom@address.com"),
            param("from", mail("Random account", "random@domain.com")),
            param("to", to),
            param("cc", cc1),
            param("cc", mail(cc2Name, cc2Mail)),
            param("bcc", bcc1),
            param("bcc", mail(bcc2Name, bcc2Mail)),
            param("o:testmode", "yes")
        );
    }

    @Test
    void sendBasicHTML() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));

        Response response = MailBuilder.using(configuration)
                                       .to("doc@delorean.com")
                                       .replyTo(mail(FROM_NAME, FROM_EMAIL))
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
    void sendWithAttachment() {
        stubFor(
            post(urlEqualTo("/api/" + DOMAIN + "/messages"))
                .withHeader("Authorization", equalTo("Basic " + expectedAuthHeader))
                .withHeader("Content-Type", containing("multipart/form-data"))
                .willReturn(aResponse().withStatus(200))
        );

        Response response = MailBuilder.using(configuration)
                                       .to("doc@delorean.com")
                                       .subject("This message has a text attachment")
                                       .text("Please find attached some text.")
                                       .attachment("This is the content of the attachment",
                                                   "readme.txt")
                                       .build()
                                       .send();

        assertTrue(response.isOk());
        RequestPatternBuilder builder = postRequestedFor(urlEqualTo("/api/" + DOMAIN + "/messages"));
        builder = withRequestBodyParameter(builder, "subject", "This message has a text attachment");
        builder = withRequestBodyParameter(builder, "to", "doc@delorean.com");
        builder = withRequestBodyParameter(builder, "text", "Please find attached some text.");
        verify(
            builder.withRequestBodyPart(
                aMultipart()
                    .withName("attachment")
                    .withHeader("Content-Type", containing("application/octet-stream"))
                    .withHeader("Content-Disposition", containing("readme.txt"))
                    .withBody(equalTo("This is the content of the attachment"))
                    .build()
            )
        );
    }

    @Test
    void sendWithFileAttachment() throws IOException {
        stubFor(
            post(urlEqualTo("/api/" + DOMAIN + "/messages"))
                .withHeader("Authorization", equalTo("Basic " + expectedAuthHeader))
                .withHeader("Content-Type", containing("multipart/form-data"))
                .willReturn(aResponse().withStatus(200))
        );

        Path tempFile = Files.createTempFile("mgn", null);
        Files.write(tempFile,
                    Lists.newArrayList("This is the content of the attachment"),
                    StandardCharsets.UTF_8);

        Response response = MailBuilder.using(configuration)
                                       .to("doc@delorean.com")
                                       .subject("This message has a text attachment")
                                       .text("Please find attached some text.")
                                       .attachment(tempFile.toFile())
                                       .build()
                                       .send();

        assertTrue(response.isOk());
        RequestPatternBuilder builder = postRequestedFor(urlEqualTo("/api/" + DOMAIN + "/messages"));
        builder = withRequestBodyParameter(builder, "subject", "This message has a text attachment");
        builder = withRequestBodyParameter(builder, "to", "doc@delorean.com");
        builder = withRequestBodyParameter(builder, "text", "Please find attached some text.");
        verify(
            builder.withRequestBodyPart(
                aMultipart()
                    .withName("attachment")
                    .withHeader("Content-Type", containing("application/octet-stream"))
                    .withHeader("Content-Disposition", containing(tempFile.toFile().getName()))
                    .withBody(containing("This is the content of the attachment"))
                    .build()
            )
        );

        Files.delete(tempFile);
    }

    @Test
    void sendWithInlineAttachment() {
        stubFor(
            post(urlEqualTo("/api/" + DOMAIN + "/messages"))
                .withHeader("Authorization", equalTo("Basic " + expectedAuthHeader))
                .withHeader("Content-Type", containing("multipart/form-data"))
                .willReturn(aResponse().withStatus(200))
        );

        Response response = MailBuilder.using(configuration)
                                       .to("doc@delorean.com")
                                       .subject("This message has a inline image")
                                       .html("<html>Inline image here: <img src=\"cid:cartman.jpg\"></html>")
                                       .inline(new ByteArrayInputStream("MockBytes".getBytes()), "cartman.jpg")
                                       .build()
                                       .send();

        assertTrue(response.isOk());
        RequestPatternBuilder builder = postRequestedFor(urlEqualTo("/api/" + DOMAIN + "/messages"));
        builder = withRequestBodyParameter(builder, "subject", "This message has a inline image");
        builder = withRequestBodyParameter(builder, "to", "doc@delorean.com");
        builder = withRequestBodyParameter(builder, "html", "<html>Inline image here: <img src=\"cid:cartman.jpg\"></html>");
        verify(
            builder.withRequestBodyPart(
                aMultipart()
                    .withName("inline")
                    .withHeader("Content-Type", containing("application/octet-stream"))
                    .withHeader("Content-Disposition", containing("cartman.jpg"))
                    .withBody(binaryEqualTo("MockBytes".getBytes()))
                    .build()
            )
        );
    }

    @Test
    void sendAsync() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));
        final AtomicBoolean callbackCalled = new AtomicBoolean(false);

        MailBuilder.using(configuration)
                   .to("doc@delorean.com")
                   .subject("This is a plain text test")
                   .text("Hello world!")
                   .build()
                   .sendAsync(new MailRequestCallback() {
                @Override
                public void completed(Response response) {
                    callbackCalled.set(true);
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
                    Assertions.fail(throwable);
                }
            });

        await().until(callbackCalled::get);
    }

    @Test
    void sendAsyncFireAndForget() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));
        MailBuilder.using(configuration)
                   .to("doc@delorean.com")
                   .subject("This is a plain text test")
                   .text("Hello world!")
                   .build()
                   .sendAsync();

        RequestPatternBuilder postRequestedFor = postRequestedFor(urlEqualTo("/api/somedomain.com/messages"));

        await().atMost(5, TimeUnit.SECONDS)
               .until(() -> !WireMock.findAll(postRequestedFor).isEmpty());

        verifyMessageSent(
            param("to", "doc@delorean.com"),
            param("subject", "This is a plain text test"),
            param("text", "Hello world!")
        );
    }

    @Test
    void sendAsyncDefaultCallback() {
        stubFor(expectedBasicPost().willReturn(aResponse().withStatus(200)));
        final AtomicBoolean callbackCalled = new AtomicBoolean(false);

        final String to = "doc@delorean.com";
        final String subject = "This is a plain text test";
        final String textBody = "Hello world!";
        final MailRequestCallback callback = new MailRequestCallback() {
            @Override
            public void completed(Response response) {
                callbackCalled.set(true);
                assertEquals(Response.ResponseType.OK,
                             response.responseType());

                verifyMessageSent(
                    param("to", to),
                    param("subject", subject),
                    param("text", textBody)
                );
            }

            @Override
            public void failed(Throwable throwable) {
                Assertions.fail(throwable);
            }
        };

        configuration.registerMailRequestCallbackFactory(mail -> {
            assertTrue(mail.getFirstValue("to").isPresent());
            assertTrue(mail.getFirstValue("subject").isPresent());
            assertTrue(mail.getFirstValue("text").isPresent());
            assertEquals(mail.getFirstValue("to").get(), to);
            assertEquals(mail.getFirstValue("subject").get(), subject);
            assertEquals(mail.getFirstValue("text").get(), textBody);

            return callback;
        });

        MailBuilder.using(configuration)
                   .to(to)
                   .subject(subject)
                   .text(textBody)
                   .build()
                   .sendAsync();

        await().until(callbackCalled::get);
    }

    @Test
    void responsePayloadTest() {
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

    @Test
    void testFilteredSend() {
        final AtomicBoolean filterExecuted = new AtomicBoolean(false);

        Configuration configuration = new Configuration()
            .registerMailSendFilter(mail -> {
                filterExecuted.set(true);
                return false;
            });

        Response response = MailBuilder
            .using(configuration)
            .build()
            .send();

        assertNull(response);
        assertTrue(filterExecuted.get());
    }
}
