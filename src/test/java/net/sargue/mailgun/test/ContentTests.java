package net.sargue.mailgun.test;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.content.Body;
import net.sargue.mailgun.content.ContentConverter;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class ContentTests {
    private final String emptyText = "\r\n";

    @Test
    public void mailContentMigration() {
        @SuppressWarnings("deprecation")
        net.sargue.mailgun.content.MailContent mailContent =
            new net.sargue.mailgun.content.MailContent().close();
        Body body = Body.builder().build();
        assertEquals("same HTML content",
                     body.html(),
                     mailContent.html());
        assertEquals("same plain text content",
                     body.text(),
                     mailContent.text());
    }

    @Test
    public void empty() {
        Body content = Body.builder().build();
        String emptyHTML = "<!DOCTYPE html><html><head>\r\n" +
                           "<meta name='viewport' content='width=device-width' />" +
                           "<meta http-equiv='Content-Type' content='text/html; " +
                           "charset=UTF-8' />" +
                           "</head><body>\r\n\r\n<br></body></html>";
        assertEquals(emptyHTML, content.html());
        assertEquals(emptyText, content.text());
    }

    @Test
    public void bodyContentOverride() {
        Body content = Body.builder().build();
        content.html("html override");
        content.text("text override");
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

        cfg.registerConverter(converter, Date.class);

        Body body = Body.builder(cfg)
            .text(date)
            .build();

        assertEquals("26/10/1985" +  emptyText, body.text());
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

        assertEquals("26/10/1985" +  emptyText, body.text());
    }
}
