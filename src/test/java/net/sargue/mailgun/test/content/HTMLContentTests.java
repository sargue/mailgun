package net.sargue.mailgun.test.content;

import net.sargue.mailgun.content.Body;
import org.junit.jupiter.api.Test;

import static net.sargue.mailgun.test.content.ContentTests.CRLF;
import static net.sargue.mailgun.test.content.ContentTests.POST_HTML;
import static net.sargue.mailgun.test.content.ContentTests.PRE_HTML;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HTMLContentTests {
    @Test
    public void empty() {
        Body body = Body.builder().build();
        assertEquals(PRE_HTML + POST_HTML, body.html());
        assertEquals("", body.text());
    }

    @Test
    public void h1() {
        Body body = Body.builder()
                        .h1()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<h1></h1>" + CRLF + POST_HTML, body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void h1text() {
        Body body = Body.builder()
                        .h1("h1")
                        .build();
        assertEquals(PRE_HTML + "<h1>h1</h1>" + CRLF + POST_HTML, body.html());
        assertEquals("h1" + CRLF, body.text());
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
        assertEquals(PRE_HTML + "<h2></h2>" + CRLF + POST_HTML, body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void h2text() {
        Body body = Body.builder()
                        .h2("h2")
                        .build();
        assertEquals(PRE_HTML + "<h2>h2</h2>" + CRLF + POST_HTML, body.html());
        assertEquals("h2" + CRLF, body.text());
    }

    @Test
    public void h3() {
        Body body = Body.builder()
                        .h3()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<h3></h3>" + CRLF + POST_HTML, body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void h3text() {
        Body body = Body.builder()
                        .h3("h3")
                        .build();
        assertEquals(PRE_HTML + "<h3>h3</h3>" + CRLF + POST_HTML, body.html());
        assertEquals("h3" + CRLF, body.text());
    }

    @Test
    public void h4() {
        Body body = Body.builder()
                        .h4()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<h4></h4>" + CRLF + POST_HTML, body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void h4text() {
        Body body = Body.builder()
                        .h4("h4")
                        .build();
        assertEquals(PRE_HTML + "<h4>h4</h4>" + CRLF + POST_HTML, body.html());
        assertEquals("h4" + CRLF, body.text());
    }

    @Test
    public void h5() {
        Body body = Body.builder()
                        .h5()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<h5></h5>" + CRLF + POST_HTML, body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void h5text() {
        Body body = Body.builder()
                        .h5("h5")
                        .build();
        assertEquals(PRE_HTML + "<h5>h5</h5>" + CRLF + POST_HTML, body.html());
        assertEquals("h5" + CRLF, body.text());
    }

    @Test
    public void h6() {
        Body body = Body.builder()
                        .h6()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<h6></h6>" + CRLF + POST_HTML, body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void h6text() {
        Body body = Body.builder()
                        .h6("h6")
                        .build();
        assertEquals(PRE_HTML + "<h6>h6</h6>" + CRLF + POST_HTML, body.html());
        assertEquals("h6" + CRLF, body.text());
    }

    @Test
    public void br() {
        Body body = Body.builder()
                        .br()
                        .build();
        assertEquals(PRE_HTML + "<br>" + POST_HTML, body.html());
        assertEquals(CRLF + CRLF, body.text());
    }

    @Test
    public void p() {
        Body body = Body.builder()
                        .p()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<p></p>" + CRLF + POST_HTML, body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void ptext() {
        Body body = Body.builder()
                        .p("p")
                        .build();
        assertEquals(PRE_HTML + "<p>p</p>" + CRLF + POST_HTML, body.html());
        assertEquals("p" + CRLF, body.text());
    }

    @Test
    public void pre() {
        Body body = Body.builder()
                        .pre()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<pre></pre>" + CRLF + POST_HTML, body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void pretext() {
        Body body = Body.builder()
                        .pre("pre")
                        .build();
        assertEquals(PRE_HTML + "<pre>pre</pre>" + CRLF + POST_HTML, body.html());
        assertEquals("pre" + CRLF, body.text());
    }

    @Test
    public void em() {
        Body body = Body.builder()
                        .em()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<em></em>" + POST_HTML, body.html());
        assertEquals("", body.text());
    }

    @Test
    public void emtext() {
        Body body = Body.builder()
                        .em("em")
                        .build();
        assertEquals(PRE_HTML + "<em>em</em>" + POST_HTML, body.html());
        assertEquals("em" + "", body.text());
    }

    @Test
    public void strong() {
        Body body = Body.builder()
                        .strong()
                        .end()
                        .build();
        assertEquals(PRE_HTML + "<strong></strong>" + POST_HTML, body.html());
        assertEquals("", body.text());
    }

    @Test
    public void strongtext() {
        Body body = Body.builder()
                        .strong("strong")
                        .build();
        assertEquals(PRE_HTML + "<strong>strong</strong>" + POST_HTML,
                     body.html());
        assertEquals("strong" + "", body.text());
    }

    @Test
    public void emptyTable() {
        Body body = Body.builder()
                        .table()
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<table border='1' cellpadding='0' cellspacing='0'></table>" +
                     CRLF + POST_HTML,
                     body.html());
        assertEquals(CRLF, body.text());
    }

    @Test
    public void tableEmptyRow() {
        Body body = Body.builder()
                        .table()
                        .row()
                        .end()
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr></tr>" + CRLF +
                     "</table>" +
                     CRLF + POST_HTML,
                     body.html());
        assertEquals(CRLF + CRLF, body.text());
    }

    @Test
    public void tableRow1Cell() {
        Body body = Body.builder()
                        .table()
                        .row("cell 1")
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><td>cell 1</td></tr>" + CRLF +
                     "</table>" +
                     CRLF + POST_HTML,
                     body.html());
        assertEquals("cell 1" + CRLF + CRLF, body.text());
    }

    @Test
    public void tableRow2Cells() {
        Body body = Body.builder()
                        .table()
                        .row("cell 1", "cell 2")
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><td>cell 1</td><td>cell 2</td></tr>" + CRLF +
                     "</table>" +
                     CRLF + POST_HTML,
                     body.html());
        assertEquals("cell 1,cell 2" + CRLF + CRLF, body.text());
    }

    @Test
    public void tableRow3Cells() {
        Body body = Body.builder()
                        .table()
                        .row("cell 1", "cell 2", "cell 3")
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><td>cell 1</td><td>cell 2</td><td>cell 3</td></tr>" + CRLF +
                     "</table>" +
                     CRLF + POST_HTML,
                     body.html());
        assertEquals("cell 1,cell 2,cell 3" + CRLF + CRLF, body.text());
    }

    @Test
    public void tableRow4Cells() {
        Body body = Body.builder()
                        .table()
                        .row("cell 1", "cell 2", "cell 3", "cell 4")
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><td>cell 1</td><td>cell 2</td>" +
                     "<td>cell 3</td><td>cell 4</td></tr>" + CRLF +
                     "</table>" +
                     CRLF + POST_HTML,
                     body.html());
        assertEquals("cell 1,cell 2,cell 3,cell 4" + CRLF + CRLF, body.text());
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
        assertEquals(PRE_HTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><th>cell header 1</th></tr>" + CRLF +
                     "</table>" +
                     CRLF + POST_HTML,
                     body.html());
        assertEquals("cell header 1" + CRLF + CRLF, body.text());
    }


    @Test
    public void tableRowWithHeader() {
        Body body = Body.builder()
                        .table()
                        .rowh("label", "data")
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<table border='1' cellpadding='0' cellspacing='0'>" +
                     "<tr><th>label</th><td>data</td></tr>" + CRLF +
                     "</table>" +
                     CRLF + POST_HTML,
                     body.html());
        assertEquals("label,data" + CRLF + CRLF, body.text());
    }
    
    @Test
    public void link() {
        Body body = Body.builder()
                        .link("http://www.google.com", "Google")
                        .build();
        assertEquals(PRE_HTML +
                     "<a href='http://www.google.com' target='_blank'>Google</a>" +
                     POST_HTML,
                     body.html());
        assertEquals("Google : http://www.google.com" + "", body.text());
    }

    @Test
    public void emptyTag() {
        Body body = Body.builder()
                        .tag("span")
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<span></span>" +
                     POST_HTML,
                     body.html());
    }

    @Test
    public void unclosedTag() {
        assertThrows(IllegalStateException.class, () -> Body.builder().tag("span").build());
    }

    @Test
    public void simpleTag() {
        Body body = Body.builder()
                        .tag("span")
                        .text("Hello world")
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<span>Hello world</span>" +
                     POST_HTML,
                     body.html());
    }


    @Test
    public void tagWithAttributes() {
        Body body = Body.builder()
                        .tag("span", "style='color:red'")
                        .text("Hello world in color")
                        .end()
                        .build();
        assertEquals(PRE_HTML +
                     "<span style='color:red'>Hello world in color</span>" +
                     POST_HTML,
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
        assertEquals(PRE_HTML +
                     "<p>Hello world <span style='color:red'>in color</span></p>" +
                     CRLF + POST_HTML,
                     body.html());
    }


    @Test
    public void nullStringHandling() {
        Body body = Body.builder()
                        .text(null)
                        .build();
        assertEquals(PRE_HTML + POST_HTML, body.html());
        assertEquals("", body.text());
    }

    @Test
    public void nullObjectHandling() {
        Body body = Body.builder()
                        .text((Object) null)
                        .build();
        assertEquals(PRE_HTML + POST_HTML, body.html());
        assertEquals("", body.text());
    }
}
