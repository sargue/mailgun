package net.sargue.mailgun.test;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.content.Body;
import org.junit.Before;
import org.junit.Test;

/**
 * This are not proper real test, just a placeholder to work with the examples.
 */
public class ExampleTest {

    private Configuration configuration;

    @Before
    public void setUp() {
        configuration = new Configuration()
            .domain("somedomain.com")
            .apiKey("key-xxxxxxxxxxxxxxxxxxxxxxxxx")
            .from("Test account", "postmaster@somedomain.com");
    }

    @Test
    public void ex1() {
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
    public void ex2() {
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
    public void ex3() {
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
    public void ex4() {
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
    public void ex5() {
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
    public void ex6() {
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

    @Test
    public void templateExample() {
        Mail.using(configuration)
            .to("marty@mcfly.com")
            .subject("Activate your account")
            .template("account_activation")
            .parameter("v:name", "Doc Brown")
            .build()
            .send();
    }
}
