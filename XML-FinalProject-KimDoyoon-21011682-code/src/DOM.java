import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

public class DOM {
    public Document openDOMTraverse(boolean setValidation, File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            factory.setValidating(setValidation);
            Document result_doc = builder.parse(file);

            return result_doc;
        } catch (FactoryConfigurationError e) {
            // unable to get a document builder factory
            e.printStackTrace(System.err);
        } catch (ParserConfigurationException e) {
            // parser was unable to be configured
            e.printStackTrace(System.err);
        } catch (SAXException e) {
            // parsing error
            e.printStackTrace(System.err);
        } catch (IOException e) {
            // i/o error
            e.printStackTrace(System.err);
        }
        return null;
    }
    public Document NewDOMtree() {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.newDocument();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return null;
    }
    public static boolean validationCheck_DTD = false;
    public Document ValidationCheck_DTD(File xmlFile) {
        validationCheck_DTD = true;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            if (validationCheck_DTD) {
                factory.setValidating(validationCheck_DTD);
            }
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new DOM_SimpleErrorHandler());

            return builder.parse(xmlFile);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean validationCheck_XSD = false;
    public Document ValidationCheck_XSD(File xmlFile) {
        try {
            validationCheck_XSD = true;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            SchemaFactory schemaFactory = null;
            if (validationCheck_XSD) {
                factory.setValidating(false);
                factory.setNamespaceAware(true);

                DocumentBuilder temp1;
                temp1 = factory.newDocumentBuilder();
                Document temp2 = temp1.parse(xmlFile);
                String xsd_url = temp2.getDocumentElement().getNamespaceURI();

                schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

                String xsdpath = "";
                NamedNodeMap attr = temp2.getDocumentElement().getAttributes();
                for (int i = 0; i < temp2.getDocumentElement().getAttributes().getLength(); i++) {
                    if (attr.item(i).getNodeName().contains("schemaLocation")) {
                        xsdpath = attr.item(i).getNodeValue().substring(xsd_url.length() + 1);
                        break;
                    }
                }

                File xsd_file = new File(xmlFile.getParent() + "/" + xsdpath);

                factory.setSchema(schemaFactory.newSchema(new Source[] { new StreamSource(xsd_file) }));
            }
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new DOM_SimpleErrorHandler());

            return builder.parse(xmlFile);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
