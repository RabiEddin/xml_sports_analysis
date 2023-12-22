import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

import javax.swing.*;

class DOM_SimpleErrorHandler implements ErrorHandler {
    @Override
    public void warning(SAXParseException e) throws SAXException {
        JOptionPane.showMessageDialog(null, e.getMessage());
        throw new SAXException();
    }
    @Override
    public void error(SAXParseException e) throws SAXException {
        JOptionPane.showMessageDialog(null, e.getMessage());
        throw new SAXException();
    }
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        JOptionPane.showMessageDialog(null, e.getMessage());
        throw new SAXException();
    }
}
