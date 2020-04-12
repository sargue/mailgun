package net.sargue.mailgun.content;

import net.sargue.mailgun.MailgunException;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;

class Util {
    private Util() {}

    static String escapeXml(String target) {
        try {
            DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
            df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Document document = df.newDocumentBuilder().newDocument();
            Text text = document.createTextNode(target);
            TransformerFactory factory = TransformerFactory.newInstance();
            // https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#transformerfactory
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = factory.newTransformer();
            DOMSource source = new DOMSource(text);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);
            return writer.toString();
        } catch (ParserConfigurationException | TransformerException e) {
            throw new MailgunException("Problem escaping XML", e);
        }
    }
}
