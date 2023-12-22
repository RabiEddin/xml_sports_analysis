import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.*;

public class Find extends JFrame {
    public void DOMPrint (Node node, String indent, JTextArea context_Find) {
        if ( node == null )
            return;

        int type = node.getNodeType();

        switch (type) {
            case Node.DOCUMENT_NODE:
                context_Find.append(indent + "[Document] " + node.getNodeName() + "\n");
                break;

            case Node.ELEMENT_NODE:
                context_Find.append(indent + "[Element] " + node.getNodeName() + "\n");
                if (node.hasAttributes()) {
                    NamedNodeMap attr = node.getAttributes();
                    for (int i = 0; i < attr.getLength(); i++) {
                        context_Find.append("  " + indent + "[Attribute] " + attr.item(i).getNodeName() + " = " + attr.item(i).getNodeValue() + "\n");
                    }
                }
                break;

            case Node.COMMENT_NODE:
                context_Find.append(indent + "[COMMENT] " + node.getNodeValue() + "\n");
                break;

            case Node.TEXT_NODE:
                context_Find.append(indent + "[TEXT] " + node.getNodeValue() + "\n");
                break;
        }

        NodeList children = node.getChildNodes();

        if (children != null) {
            int len = children.getLength();
            for (int i = 0; i < len; i++)
                DOMPrint(children.item(i), indent + "   ", context_Find);
        }
    }

    public void DOMNodeFind(String findNode, Document xml_doc, int type, JTextArea context_result_Find) {
        switch (type) {
            case Node.ELEMENT_NODE:
                Elementnode_Find(xml_doc, findNode, type, context_result_Find);
                break;
            case Node.ATTRIBUTE_NODE:
                Attributenode_Find(xml_doc, findNode, context_result_Find);
                break;
            case Node.COMMENT_NODE:
                Commentnode_Find(xml_doc, findNode, type, context_result_Find);
                break;
            case Node.TEXT_NODE:
                Textnode_Find(xml_doc, findNode, type, context_result_Find);
                break;
        }
    }
    public void Elementnode_Find(Node node, String eleName, int type, JTextArea context_result_Find) {
        if ( node == null)  return;

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == type && child.getNodeName().equals(eleName)) {
                context_result_Find.append("[" + getDepth(child) + ", " + getSiblingIndex(child) +"] " + child.getNodeName() + ": " + child.getNodeValue() + "\n");
            }

            Elementnode_Find(child, eleName, type, context_result_Find);
        }
    }
    public void Attributenode_Find(Node node, String attrName, JTextArea context_result_Find) {
        if ( node == null)  return;

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.hasAttributes()) {
                NamedNodeMap attr = child.getAttributes();
                for (int j = 0; j < attr.getLength(); j++) {
                    if (attr.item(j).getNodeName().equals(attrName)) {
                        context_result_Find.append(attr.item(j).getNodeName() + ": " + attr.item(j).getNodeValue() + "\n");
                    }
                }
            }

            Attributenode_Find(child, attrName, context_result_Find);
        }
    }
    public void Commentnode_Find (Node node, String cmtName, int type, JTextArea context_result_Find) {
        if ( node == null)  return;

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == type && child.getNodeValue() != null && child.getNodeValue().equals(cmtName)) {
                context_result_Find.append(child.getNodeName() + ": " + child.getNodeValue() + "\n");
            }

            Elementnode_Find(child, cmtName, type, context_result_Find);
        }
    }
    public void Textnode_Find (Node node, String txtName, int type, JTextArea context_result_Find) {
        if ( node == null)  return;

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == type && child.getNodeValue() != null && child.getNodeValue().equals(txtName)) {
                context_result_Find.append(child.getNodeName() + ": " + child.getNodeValue() + "\n");
            }

            Textnode_Find(child, txtName, type, context_result_Find);
        }
    }
    public int getDepth(Node node) {
        int index = 0;
        while ((node = node.getParentNode()) != null)  index++;
        return index;
    }
    protected int getSiblingIndex(Node node) {
        int index=1;

        while( (node = node.getPreviousSibling()) != null)
            if(node.getNodeType() != Node.TEXT_NODE
                    && node.getNodeType() != Node.COMMENT_NODE)
                index++;

        return index;
    }
    public int type = 0;
    public Find (Document xml_doc) {
        JOptionPane.showMessageDialog(null, "<html>해당 메뉴에서 Node를 검색할 때에는 다음 내용에 기반하여 검색해주세요." +
                "<br>1. Element – element node name 입력" +
                "<br>2. Attribute – attribute node name 입력" +
                "<br>3. Comment – comment node value 입력" +
                "<br>4. Text – text node value 입력</html>");

        setTitle("XML Final Project - Find");

        Container cp_Find = getContentPane();
        cp_Find.setLayout(null);

        JLabel title_Find = new JLabel("Please enter the name of the node to find in the DOM tree.");
        Font Titelfont1 = new Font("맑은 고딕", Font.BOLD, 20);
        title_Find.setFont(Titelfont1);
        title_Find.setLocation(50, 5);
        title_Find.setSize(700, 40);
        cp_Find.add(title_Find);

        JTextField finding_context = new JTextField("");
        Font Titelfont2 = new Font("맑은 고딕", Font.PLAIN, 15);
        finding_context.setFont(Titelfont2);
        finding_context.setLocation(100, 70);
        finding_context.setSize(500, 40);
        cp_Find.add(finding_context);

        JTextArea context_Find = new JTextArea();
        context_Find.setFont(Titelfont2);
        context_Find.setLocation(20, 115);
        context_Find.setSize(400, 300);
        context_Find.setEditable(false);
        context_Find.setLineWrap(true);
        context_Find.setWrapStyleWord(true);
        cp_Find.add(context_Find);
        this.DOMPrint(xml_doc, "  ", context_Find);

        JScrollPane context_scroll_Find = new JScrollPane(context_Find);
        context_scroll_Find.setLocation(20, 115);
        context_scroll_Find.setSize(400, 300);
        cp_Find.add(context_scroll_Find);
        javax.swing.SwingUtilities.invokeLater(() -> context_scroll_Find.getVerticalScrollBar().setValue(0));

        JTextArea context_result_Find = new JTextArea();
        context_result_Find.setFont(Titelfont2);
        context_result_Find.setLocation(430, 115);
        context_result_Find.setSize(250, 300);
        context_result_Find.setEditable(false);
        context_result_Find.setLineWrap(true);
        context_result_Find.setWrapStyleWord(true);
        cp_Find.add(context_result_Find);

        JScrollPane context_result_scroll_Find = new JScrollPane(context_result_Find);
        context_result_scroll_Find.setLocation(430, 115);
        context_result_scroll_Find.setSize(250, 300);
        cp_Find.add(context_result_scroll_Find);

        ButtonGroup type_buttongroup = new ButtonGroup();

        JRadioButton button_element = new JRadioButton("Element");
        button_element.setLocation(150, 40);
        button_element.setSize(110, 30);
        cp_Find.add(button_element);
        button_element.addActionListener(e -> {
            type = Node.ELEMENT_NODE;
            finding_context.setText("");
        });

        JRadioButton button_attribute = new JRadioButton("Attribute");
        button_attribute.setLocation(260, 40);
        button_attribute.setSize(110, 30);
        cp_Find.add(button_attribute);
        button_attribute.addActionListener(e -> {
            type = Node.ATTRIBUTE_NODE;
            finding_context.setText("");
        });

        JRadioButton button_comment = new JRadioButton("Comment");
        button_comment.setLocation(370, 40);
        button_comment.setSize(110, 30);
        cp_Find.add(button_comment);
        button_comment.addActionListener(e -> {
            type = Node.COMMENT_NODE;
            finding_context.setText("");
        });

        JRadioButton button_text = new JRadioButton("Text");
        button_text.setLocation(480, 40);
        button_text.setSize(110, 30);
        cp_Find.add(button_text);
        button_text.addActionListener(e -> {
            type = Node.TEXT_NODE;
            finding_context.setText("");
        });

        type_buttongroup.add(button_element);
        type_buttongroup.add(button_attribute);
        type_buttongroup.add(button_comment);
        type_buttongroup.add(button_text);

        JButton button_back_FF = new JButton("back");
        button_back_FF.setLocation(450, 420);
        button_back_FF.setSize(120, 40);
        cp_Find.add(button_back_FF);
        button_back_FF.addActionListener(e -> setVisible(false));

        JButton button_enter_FF = new JButton("enter");
        button_enter_FF.setLocation(570, 420);
        button_enter_FF.setSize(120, 40);
        cp_Find.add(button_enter_FF);
        button_enter_FF.addActionListener(e -> {
            context_result_Find.setText("");
            DOMNodeFind(finding_context.getText(), xml_doc, type, context_result_Find);
            javax.swing.SwingUtilities.invokeLater(() -> context_result_scroll_Find.getVerticalScrollBar().setValue(0));
        });

        setSize(700, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
