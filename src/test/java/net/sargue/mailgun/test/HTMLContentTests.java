package net.sargue.mailgun.test;

import net.sargue.mailgun.content.Body;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HTMLContentTests {
    private static final String CRLF = "\r\n";
    private String preHTML =
        "<!DOCTYPE html><html><head>" + CRLF +
        "<meta name='viewport' content='width=device-width' />" +
        "<meta http-equiv='Content-Type' content='text/html; " +
        "charset=UTF-8' />" +
        "</head><body>" + CRLF;
    private String postHTML = CRLF + "<br></body></html>";
    private final String postText = CRLF;

    @Test
    public void h1() {
        Body body = Body.builder()
                        .h1()
                        .end()
                        .build();
        assertEquals(preHTML + "<h1></h1>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void h1text() {
        Body body = Body.builder()
                        .h1("h1")
                        .build();
        assertEquals(preHTML + "<h1>h1</h1>" + postHTML, body.html());
        assertEquals("h1" + postText, body.text());
    }

    @Test
    public void h1InsideAndOutside() {
        Body bodyInside = Body.builder()
                              .h1("This is the heading")
                              .build();
        Body bodyOutside = Body.builder()
                               .h1()
                               .text("This is the heading")
                               .end()
                               .build();
        assertEquals(bodyInside.html(), bodyOutside.html());
        assertEquals(bodyInside.text(), bodyOutside.text());
    }

    @Test
    public void h2() {
        Body body = Body.builder()
                        .h2()
                        .end()
                        .build();
        assertEquals(preHTML + "<h2></h2>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void h2text() {
        Body body = Body.builder()
                        .h2("h2")
                        .build();
        assertEquals(preHTML + "<h2>h2</h2>" + postHTML, body.html());
        assertEquals("h2" + postText, body.text());
    }

    @Test
    public void h3() {
        Body body = Body.builder()
                        .h3()
                        .end()
                        .build();
        assertEquals(preHTML + "<h3></h3>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void h3text() {
        Body body = Body.builder()
                        .h3("h3")
                        .build();
        assertEquals(preHTML + "<h3>h3</h3>" + postHTML, body.html());
        assertEquals("h3" + postText, body.text());
    }

    @Test
    public void h4() {
        Body body = Body.builder()
                        .h4()
                        .end()
                        .build();
        assertEquals(preHTML + "<h4></h4>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void h4text() {
        Body body = Body.builder()
                        .h4("h4")
                        .build();
        assertEquals(preHTML + "<h4>h4</h4>" + postHTML, body.html());
        assertEquals("h4" + postText, body.text());
    }

    @Test
    public void h5() {
        Body body = Body.builder()
                        .h5()
                        .end()
                        .build();
        assertEquals(preHTML + "<h5></h5>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void h5text() {
        Body body = Body.builder()
                        .h5("h5")
                        .build();
        assertEquals(preHTML + "<h5>h5</h5>" + postHTML, body.html());
        assertEquals("h5" + postText, body.text());
    }

    @Test
    public void h6() {
        Body body = Body.builder()
                        .h6()
                        .end()
                        .build();
        assertEquals(preHTML + "<h6></h6>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void h6text() {
        Body body = Body.builder()
                        .h6("h6")
                        .build();
        assertEquals(preHTML + "<h6>h6</h6>" + postHTML, body.html());
        assertEquals("h6" + postText, body.text());
    }

    @Test
    public void br() {
        Body body = Body.builder()
                        .br()
                        .build();
        assertEquals(preHTML + "<br>" + postHTML, body.html());
        assertEquals(CRLF + CRLF + postText, body.text());
    }

    @Test
    public void p() {
        Body body = Body.builder()
                        .p()
                        .end()
                        .build();
        assertEquals(preHTML + "<p></p>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void ptext() {
        Body body = Body.builder()
                        .p("p")
                        .build();
        assertEquals(preHTML + "<p>p</p>" + postHTML, body.html());
        assertEquals("p" + postText, body.text());
    }

    @Test
    public void pre() {
        Body body = Body.builder()
                        .pre()
                        .end()
                        .build();
        assertEquals(preHTML + "<pre></pre>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void pretext() {
        Body body = Body.builder()
                        .pre("pre")
                        .build();
        assertEquals(preHTML + "<pre>pre</pre>" + postHTML, body.html());
        assertEquals("pre" + postText, body.text());
    }

    @Test
    public void em() {
        Body body = Body.builder()
                        .em()
                        .end()
                        .build();
        assertEquals(preHTML + "<em></em>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void emtext() {
        Body body = Body.builder()
                        .em("em")
                        .build();
        assertEquals(preHTML + "<em>em</em>" + postHTML, body.html());
        assertEquals("em" + postText, body.text());
    }

    @Test
    public void strong() {
        Body body = Body.builder()
                        .strong()
                        .end()
                        .build();
        assertEquals(preHTML + "<strong></strong>" + postHTML, body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void strongtext() {
        Body body = Body.builder()
                        .strong("strong")
                        .build();
        assertEquals(preHTML + "<strong>strong</strong>" + postHTML,
                     body.html());
        assertEquals("strong" + postText, body.text());
    }

    @Test
    public void colortext() {
        Body body = Body.builder()
                        .color("red", "color")
                        .build();
        assertEquals(preHTML + "<span style='color:red'>color</span>" + postHTML,
                     body.html());
        assertEquals("color" + postText, body.text());
    }

    @Test
    public void emptyTable() {
        Body body = Body.builder()
                        .table()
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<table border='1' cellpadding='0' cellspacing='0'></table>" +
                     postHTML,
                     body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void tableEmptyRow() {
        Body body = Body.builder()
                        .table()
                        .row()
                        .end()
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr></tr>" +
                     "</table>" +
                     postHTML,
                     body.html());
        assertEquals(postText, body.text());
    }

    @Test
    public void tableRow1Cell() {
        Body body = Body.builder()
                        .table()
                        .row("cell 1")
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><td>cell 1</td></tr>" +
                     "</table>" +
                     postHTML,
                     body.html());
        assertEquals("cell 1" + postText, body.text());
    }

    @Test
    public void tableRow2Cells() {
        Body body = Body.builder()
                        .table()
                        .row("cell 1", "cell 2")
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><td>cell 1</td><td>cell 2</td></tr>" +
                     "</table>" +
                     postHTML,
                     body.html());
        assertEquals("cell 1,cell 2" + postText, body.text());
    }

    @Test
    public void tableRow3Cells() {
        Body body = Body.builder()
                        .table()
                        .row("cell 1", "cell 2", "cell 3")
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><td>cell 1</td><td>cell 2</td><td>cell 3</td></tr>" +
                     "</table>" +
                     postHTML,
                     body.html());
        assertEquals("cell 1,cell 2,cell 3" + postText, body.text());
    }

    @Test
    public void tableRow4Cells() {
        Body body = Body.builder()
                        .table()
                        .row("cell 1", "cell 2", "cell 3", "cell 4")
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><td>cell 1</td><td>cell 2</td>" +
                     "<td>cell 3</td><td>cell 4</td></tr>" +
                     "</table>" +
                     postHTML,
                     body.html());
        assertEquals("cell 1,cell 2,cell 3,cell 4" + postText, body.text());
    }

    @Test
    public void tableRowCellHeader() {
        Body body = Body.builder()
                        .table()
                        .row()
                        .cellHeader("cell header 1")
                        .end()
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><th>cell header 1</th></tr>" +
                     "</table>" +
                     postHTML,
                     body.html());
        assertEquals("cell header 1" + postText, body.text());
    }


    @Test
    public void tableRowWithHeader() {
        Body body = Body.builder()
                        .table()
                        .rowh("label", "data")
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><th>label</th><td>data</td></tr>" +
                     "</table>" +
                     postHTML,
                     body.html());
        assertEquals("label,data" + postText, body.text());
    }
    
    @Test
    public void link() {
        Body body = Body.builder()
                        .link("http://www.google.com", "Google")
                        .build();
        assertEquals(preHTML +
                     "<a href='http://www.google.com' target='_blank'>Google</a>" +
                     postHTML,
                     body.html());
        assertEquals("Google : http://www.google.com" + postText, body.text());
    }

    @Test
    public void emptyTag() {
        Body body = Body.builder()
                        .tag("span")
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<span></span>" +
                     postHTML,
                     body.html());
    }

    @Test(expected = IllegalStateException.class)
    public void unclosedTag() {
        Body.builder()
            .tag("span")
            .build();
    }

    @Test
    public void simpleTag() {
        Body body = Body.builder()
                        .tag("span")
                        .text("Hello world")
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<span>Hello world</span>" +
                     postHTML,
                     body.html());
    }


    @Test
    public void tagWithAttributes() {
        Body body = Body.builder()
                        .tag("span", "style='color:red'")
                        .text("Hello world in color")
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<span style='color:red'>Hello world in color</span>" +
                     postHTML,
                     body.html());
    }

    @Test
    public void nestedTag() {
        Body body = Body.builder()
                        .p()
                        .text("Hello world ")
                        .tag("span", "style='color:red'")
                        .text("in color")
                        .end()
                        .end()
                        .build();
        assertEquals(preHTML +
                     "<p>Hello world <span style='color:red'>in color</span></p>" +
                     postHTML,
                     body.html());
    }
}
