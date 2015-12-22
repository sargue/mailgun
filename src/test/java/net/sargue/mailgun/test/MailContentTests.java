package net.sargue.mailgun.test;

import net.sargue.mailgun.content.MailContent;
import org.junit.Assert;
import org.junit.Test;

public class MailContentTests {
    @Test
    public void empty() {
        MailContent content = new MailContent().close();
        String emptyHTML = "<!DOCTYPE html><html><head>\r\n" +
                "<meta name='viewport' content='width=device-width' />" +
                "<meta http-equiv='Content-Type' content='text/html; " +
                "charset=UTF-8' />" +
                "</head><body>\r\n\r\n<br></body></html>";
        String emptyText = "\r\n";
        Assert.assertEquals(emptyHTML, content.html());
        Assert.assertEquals(emptyText, content.text());
    }
}
