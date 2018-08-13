package net.sargue.mailgun.test;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.content.Body;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * This are not proper real test, just a placeholder to work with the examples.
 */
public class ExampleTest {

    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration()
            .domain("somedomain.com")
            .apiKey("key-xxxxxxxxxxxxxxxxxxxxxxxxx")
            .from("Test account", "postmaster@somedomain.com")
            .registerMailSendFilter(mail -> false);
    }

    @Test
    public void readme1() {
        Mail.using(configuration)
            .to("marty@mcfly.com")
            .subject("This is the subject")
            .text("Hello world!")
            .build()
            .send();
    }

    @Test
    public void readme2() {
        Mail.using(configuration)
            .to("marty@mcfly.com")
            .subject("This message has a text attachment")
            .text("Please find attached a file.")
            .attachment(new File("/path/to/image.jpg"))
            .build()
            .send();
    }

    @Test
    public void readme3() {
        Mail.using(configuration)
            .to("marty@mcfly.com")
            .to("george@mcfly.com")
            .cc("lorraine@mcfly.com")
            .cc("dave@mcfly.com")
            .subject("This is the subject")
            .text("Hello world!")
            .build()
            .send();
    }

    @Test
    public void readme4() {
        Mail.using(configuration)
            .to("marty@mcfly.com")
            .subject("This message has a text attachment")
            .text("Please find attached a file.")
            .attachment(new File("/path/to/image.jpg"))
            .attachment(new File("/path/to/report.pdf"))
            .build()
            .send();
    }

    @Test
    public void readme5() {
        Mail.using(configuration)
            .body()
            .h1("This is a heading")
            .p("And this some text")
            .mail()
            .to("marty@mcfly.com")
            .subject("This is the subject")
            .build();
    }

    @Test
    public void readme6() {
        //@formatter:off
        Mail.using(configuration)
            .body()
            .h3("Monthly report")
            .p("Report of the number of time travels this month")
            .table()
                .row("Marty", "5")
                .row("Doc", "7")
                .row("Einstein", "0")
            .end()
            .mail()
            .to("marty@mcfly.com")
            .subject("Monthly Delorean usage")
            .build();
        //@formatter:on
    }

    @Test
    public void readme7() {
        Body body = Body.builder()
                        .h1("This is a heading")
                        .p("And this some text")
                        .build();

        Mail.using(configuration)
            .to("marty@mcfly.com")
            .subject("This is the subject")
            .content(body)
            .build();
    }

    @Test
    public void wiki1() {
        Body body = Body.builder()
                        .h1("This is a heading")
                        .p("And this some text")
                        .build();

        Body.builder(configuration)
            .h1("This is a heading")
            .p("And this some text")
            .mail()
            .to("marty@mcfly.com")
            .subject("This is the subject")
            .content(body)
            .build();
    }


    @Test
    public void wiki2() {
        Body body = Body.builder()
                        .h1("This is a heading")
                        .p("And this some text")
                        .build();

        Mail.using(configuration)
            .to("marty@mcfly.com")
            .subject("This is the subject")
            .content(body)
            .build();
    }

    @Test
    public void wiki3() {
        Mail.using(configuration)
            .body()
            .table()
            .rowh("ID", 2356)
            .rowh("Name", "Michael")
            .rowh("Address", "9303 Lyon Drive, Lyon Estates")
            .end()
            .mail()
            .to("marty@mcfly.com")
            .subject("Come see my home!")
            .build();
    }
}
