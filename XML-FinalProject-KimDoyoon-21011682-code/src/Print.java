import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import javax.swing.*;

public class Print extends JFrame {

    public void DOMPrint (Node node, String indent, JTextArea context_print) {
        if ( node == null )
            return;

        int type = node.getNodeType();

        switch (type) {
            case Node.DOCUMENT_NODE:
                context_print.append(indent + "[Document] " + node.getNodeName() + "\n");
                break;

            case Node.ELEMENT_NODE:
                context_print.append(indent + "[Element] " + node.getNodeName() + "\n");
                if (node.hasAttributes()) {
                    NamedNodeMap attr = node.getAttributes();
                    for (int i = 0; i < attr.getLength(); i++) {
                        context_print.append("  " + indent + "[Attribute] " + attr.item(i).getNodeName() + " = " + attr.item(i).getNodeValue() + "\n");
                    }
                }
                break;

            case Node.COMMENT_NODE:
                context_print.append(indent + "[COMMENT] " + node.getNodeValue() + "\n");
                break;

            case Node.TEXT_NODE:
                context_print.append(indent + "[TEXT] " + node.getNodeValue() + "\n");
                break;
        }

        NodeList children = node.getChildNodes();

        if (children != null) {
            int len = children.getLength();
            for (int i = 0; i < len; i++)
                DOMPrint(children.item(i), indent + "   ", context_print);
        }
    }
    public Print(Document xml_doc) {
        setTitle("XML Final Project - Print");

        Container cp_print = getContentPane();
        cp_print.setLayout(null);

        JLabel title_print = new JLabel("XML File context");
        Font Titelfont1 = new Font("맑은 고딕", Font.BOLD, 25);
        title_print.setFont(Titelfont1);
        title_print.setLocation(250, 5);
        title_print.setSize(300, 50);
        cp_print.add(title_print);

        JTextArea context_print = new JTextArea();
        Font Titelfont2 = new Font("맑은 고딕", Font.PLAIN, 15);
        context_print.setFont(Titelfont2);
        context_print.setLocation(60, 60);
        context_print.setSize(580, 350);
        context_print.setEditable(false);
        context_print.setLineWrap(true);
        context_print.setWrapStyleWord(true);
        cp_print.add(context_print);
        this.DOMPrint(xml_doc, "  ", context_print);

        JScrollPane context_scroll_print = new JScrollPane(context_print);
        context_scroll_print.setLocation(60, 60);
        context_scroll_print.setSize(580, 350);
        cp_print.add(context_scroll_print);
        javax.swing.SwingUtilities.invokeLater(() -> context_scroll_print.getVerticalScrollBar().setValue(0));

        JButton button_back_FP = new JButton("back");
        button_back_FP.setLocation(450, 420);
        button_back_FP.setSize(120, 40);
        cp_print.add(button_back_FP);
        button_back_FP.addActionListener(e -> setVisible(false));

        setSize(700, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
