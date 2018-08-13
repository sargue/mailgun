package net.sargue.mailgun.test.content;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.content.Body;
import net.sargue.mailgun.content.ContentConverter;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTests {
    static final String CRLF = "\r\n";
    static final String PRE_HTML =
        "<!DOCTYPE html><html><head>\r\n" +
        "<meta name='viewport' content='width=device-width' />" +
        "<meta http-equiv='Content-Type' " +
        "content='text/html; charset=UTF-8' />" +
        "</head><body>\r\n";
    static final String POST_HTML = "\r\n<br></body></html>";

    @Test
    public void empty() {
        Body content = Body.builder().build();
        assertEquals(PRE_HTML + POST_HTML, content.html());
        assertEquals("", content.text());
    }

    @Test
    public void bodyContentOverride() {
        Body content = Body.builder().build();
        content.html("html override")
               .text("text override");
        assertEquals("html override", content.html());
        assertEquals("text override", content.text());
    }

    @Test
    public void dateFormat() {
        Date date = new GregorianCalendar(1985, 9, 26, 1, 22).getTime();

        Configuration cfg = new Configuration();
        ContentConverter<Date> converter = new ContentConverter<Date>() {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public String toString(Date value) {
                return df.format(value);
            }
        };

        cfg = cfg.registerConverter(converter, Date.class);

        Body body = Body.builder(cfg)
                        .text(date)
                        .build();

        assertEquals("26/10/1985", body.text());
    }

    @Test
    public void dateSubclassFormat() {
        Configuration cfg = new Configuration();
        ContentConverter<Date> converter = new ContentConverter<Date>() {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public String toString(Date value) {
                return df.format(value);
            }
        };

        cfg.registerConverter(converter, Date.class);

        Date date = new GregorianCalendar(1985, 9, 26, 1, 22).getTime();

        Body body = Body.builder(cfg)
                        .text(new Timestamp(date.getTime()))
                        .build();

        assertEquals("26/10/1985", body.text());
    }

    @Test
    public void basicText() {
        Body content = Body.builder()
                           .h1("This is the H1")
                           .p("This is a P")
                           .build();
        assertEquals(PRE_HTML + "<h1>This is the H1</h1>" + CRLF +
                     "<p>This is a P</p>" + CRLF + POST_HTML,
                     content.html());
        assertEquals("This is the H1" + CRLF + "This is a P" + CRLF,
                     content.text());
    }
}
