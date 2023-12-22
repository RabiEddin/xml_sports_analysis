import org.w3c.dom.*;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Delete extends JFrame {
    public void DOMPrint (Node node, String indent, JTextArea context_Insert) {
        if ( node == null )
            return;

        int type = node.getNodeType();

        switch (type) {
            case Node.DOCUMENT_NODE:
                context_Insert.append(indent + "[Document] " + node.getNodeName() + "\n");
                break;

            case Node.ELEMENT_NODE:
                context_Insert.append(indent + "[Element] " + node.getNodeName() + "\n");
                if (node.hasAttributes()) {
                    NamedNodeMap attr = node.getAttributes();
                    for (int i = 0; i < attr.getLength(); i++) {
                        context_Insert.append("  " + indent + "[Attribute] " + attr.item(i).getNodeName() + " = " + attr.item(i).getNodeValue() + "\n");
                    }
                }
                break;

            case Node.COMMENT_NODE:
                context_Insert.append(indent + "[COMMENT] " + node.getNodeValue() + "\n");
                break;

            case Node.TEXT_NODE:
                context_Insert.append(indent + "[TEXT] " + node.getNodeValue() + "\n");
                break;
        }

        NodeList children = node.getChildNodes();

        if (children != null) {
            int len = children.getLength();
            for (int i = 0; i < len; i++)
                DOMPrint(children.item(i), indent + "   ", context_Insert);
        }
    }

    public void DOMNodeDelete(Document xml_doc_copy, String tree_context, String DeleteNode, String attrnode_name, int type, int start_loc, int end_loc) {
        switch (type) {
            case  Node.ELEMENT_NODE:
                if (DeleteNode != null && !(DeleteNode.isEmpty())) {
                    Elementnode_Delete(xml_doc_copy, tree_context, DeleteNode, start_loc, end_loc);
                } else  {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "삭제할 Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case  Node.ATTRIBUTE_NODE:
                if (DeleteNode != null && !(DeleteNode.isEmpty())) {
                    Attributenode_Delete(xml_doc_copy, tree_context, DeleteNode, attrnode_name, start_loc, end_loc);
                } else  {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "삭제할 Attribute가 속해있는 Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case Node.COMMENT_NODE:
                if (DeleteNode != null && !(DeleteNode.isEmpty())) {
                    Commentnode_Delete(xml_doc_copy, tree_context, DeleteNode, start_loc, end_loc);
                } else  {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "삭제할 Comment Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case Node.TEXT_NODE:
                if (DeleteNode != null && !(DeleteNode.isEmpty())) {
                    Textnode_Delete(xml_doc_copy, tree_context, DeleteNode, attrnode_name, start_loc, end_loc);
                } else  {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "삭제할 Text Node가 속해있는 Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            default:
                // #0-2: 유저가 어떤 type도 선택하지 않은 경우
                JOptionPane.showMessageDialog(null, "삭제할 Node의 type을 선택하지 않았습니다.", "Error - Select Node type", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }
    public int node_loc (String text, String Name, int start_loc, int end_loc) {
        int cnt = 0;
        int index = -1;

        while ((index = text.indexOf(Name, index + 1)) != -1) {
            if (index == start_loc && index + Name.length() == end_loc) break;
            cnt++;
            index += Name.length();
        }

        return cnt;
    }
    public void Elementnode_Delete(Document xml_doc_copy, String tree_text, String DeleteName, int start_loc, int end_loc) {

        int flag = 0;
        int num = node_loc(tree_text, "[Element] " + DeleteName, start_loc - 10, end_loc);

        NodeList sameName = xml_doc_copy.getElementsByTagName(DeleteName);
        Node target_node = sameName.item(num);

        //  Error Check Start

        // #1: 유저가 선택한 node가 없는 element node인 경우
        if (target_node == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree에 없는 Element Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #2: 유저가 선택한 node가 Root Node인 경우
        if (target_node == xml_doc_copy.getDocumentElement()) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree의 Root Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #3:

        // Error Check Finish

        if (target_node.hasAttributes() || target_node.hasChildNodes()) {
            flag = JOptionPane.showConfirmDialog(null, "<html>해당 Node는 하위에 Child Node가 있거나 Attribute를 가지고 있습니다.<br>삭제하시겠습니까?</html>", "Check - Delete Check", JOptionPane.YES_NO_OPTION);
        }
        if (flag == 1) {
            return;
        }
        if (target_node.getNextSibling().getNodeType() == Node.TEXT_NODE) {
            target_node.getParentNode().removeChild(target_node.getNextSibling());
        }
        target_node.getParentNode().removeChild(target_node);
        JOptionPane.showMessageDialog(null, "Delete Element Node Success!!!");
    }
    public void Attributenode_Delete(Document xml_doc_copy, String tree_text, String select_eleName, String attrName, int start_loc, int end_loc) {

        int num = node_loc(tree_text, "[Element] " + select_eleName, start_loc - 10, end_loc);

        NodeList sameName = xml_doc_copy.getElementsByTagName(select_eleName);
        Node target_node = sameName.item(num);
        Element target_element = (Element) target_node;

        // Error Check Start

        // #1: 유저가 선택한 node가 없는 element node인 경운
        if (target_node == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree에 없는 Element Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #2: 유저가 삭제하고자 하는 Attribute Name을 입력하지 않은 경우
        if (attrName.isEmpty() || attrName.equals("Attribute Name")) {
            JOptionPane.showMessageDialog(null, "삭제하고자 하는 Attribute Name이 입력되지 않았습니다.", "Error - Attribute Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #3: 삭제하고자 하는 Attribute Name이 DOM 트리에 없는 경우
        if (!(target_element.hasAttribute(attrName))) {
            JOptionPane.showMessageDialog(null, "삭제하고자 하는 Attribute Name이 해당 Element Node에 없습니다.", "Error - Attribute Node", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Error Check Finish

        target_element.removeAttribute(attrName);
        JOptionPane.showMessageDialog(null, "Delete Attribute Node Success!!!");
    }
    public Node target_commentNode = null;
    public int comment_cnt = 0;
    public void find_commentnode (Node node, String comment_node, int loc) {
        if (node == null) return ;

        int type = node.getNodeType();

        if (type == Node.COMMENT_NODE && node.getNodeValue().equals(comment_node)) {
            if (comment_cnt == loc) {
                target_commentNode = node;
            }
            comment_cnt++;
        }

        NodeList children = node.getChildNodes();

        if (children != null) {
            int len = children.getLength();

            for (int i = 0; i < len; i++) {
                find_commentnode(children.item(i), comment_node, loc);
            }
        }
    }
    public void Commentnode_Delete(Document xml_doc_copy, String tree_text, String delete_commentName, int start_loc, int end_loc) {

        target_commentNode = null;
        comment_cnt = 0;
        int num = node_loc(tree_text, "[COMMENT] " + delete_commentName, start_loc - 10, end_loc);

        find_commentnode(xml_doc_copy, delete_commentName, num);

        // Error Check Start

        // #1: 선택한 comment node가 DOM 트리에 없는 경우
        if (target_commentNode == null) {
            JOptionPane.showMessageDialog(null, "선택한 Comment Node는 XML DOM Tree에 없는 Comment Node입니다.", "Error - Comment Node", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Error Check Finish

        if (target_commentNode.getNextSibling().getNodeType() == Node.TEXT_NODE) {
            target_commentNode.getParentNode().removeChild(target_commentNode.getNextSibling());
        }
        target_commentNode.getParentNode().removeChild(target_commentNode);
        JOptionPane.showMessageDialog(null, "Delete Comment Node Success!!!");
    }
    public void Textnode_Delete(Document xml_doc_copy, String tree_text, String select_eleName, String delete_textvalue, int start_loc, int end_loc) {

        int flag1 = 0;
        int num = node_loc(tree_text, "[Element] " + select_eleName, start_loc - 10, end_loc);

        NodeList sameName = xml_doc_copy.getElementsByTagName(select_eleName);
        Node target_node = sameName.item(num);

        // Error Check Start

        // #1: 유저가 선택한 node가 없는 element node인 경우
        if (target_node == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree에 없는 Element Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NodeList text_check = target_node.getChildNodes();

        for (int i = 0; i < text_check.getLength(); i++) { // 이게 해당 element 노드가 leaf 노드인지 확인하는 코드
            if (text_check.item(i).getNodeType() == Node.ELEMENT_NODE) {
                flag1 = 1;
                break;
            }
        }

        // #2: 유저가 선택한 element node가 leaf element node가 아닌 경우
        if (flag1 == 1) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 Leaf Element Node가 아닙니다.", "Error - Text Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #3: 유저가 빈 Text Value를 입력한 경우
        if (delete_textvalue.isEmpty() || delete_textvalue.equals("Text Value")) {
            JOptionPane.showMessageDialog(null, "삭제할 Text Node의 value를 입력하지 않았습니다.", "Error - Text Node", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Node delete_text_node = null;

        for (int i = 0; i < text_check.getLength(); i++) {
            if (text_check.item(i).getNodeType() == Node.TEXT_NODE && text_check.item(i).getNodeValue().equals(delete_textvalue)) {
                delete_text_node = text_check.item(i);
                break;
            }
        }

        // #4: 유저가 입력한 Text Value가 해당 element node에 없는 경우
        if (delete_text_node == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node에 없는 Text Node입니다.", "Error - Text Node", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Error Check Finish
        target_node.removeChild(delete_text_node);
        JOptionPane.showMessageDialog(null, "Delete Text Node Success!!!");
    }
    public int type = 0;
    public int start_node_loc = -1;
    public int end_node_loc = -1;
    public Document xml_doc_copy;
    public JButton button_enter_FD;
    public static Document createNewDocument(Document xml_doc) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document copied_document = builder.newDocument();

        NodeList document_children = xml_doc.getChildNodes();
        for (int i = 0; i < document_children.getLength(); i++) { // Document 노드랑 root 노드랑 그 사이에 comment가 있는 경우 해당 노드를 복사한다.
            switch (document_children.item(i).getNodeType()) {
                case Node.COMMENT_NODE:
                    Node copied_comment_node = copied_document.importNode(document_children.item(i), true);
                    copied_document.appendChild(copied_comment_node);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                    try {
                        DocumentType original_document_type = xml_doc.getDoctype();
                        if (original_document_type != null) {
                            DocumentType copied_document_type = copied_document.getImplementation().createDocumentType(
                                    original_document_type.getName(),
                                    original_document_type.getPublicId(),
                                    original_document_type.getSystemId()
                            );
                            copied_document.appendChild(copied_document_type);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    break;
                case Node.ELEMENT_NODE:
                    break;
            }
        }
        Node copied_node = copied_document.importNode(xml_doc.getDocumentElement(), true);
        copied_document.appendChild(copied_node);
        return copied_document;
    }
    public Delete (Document xml_doc) {
        JOptionPane.showMessageDialog(null, "<html>해당 메뉴에서 새로 Node를 Delete 때에는 다음 내용에 기반하여 Delete해주세요." +
                "<br>1. Element: 삭제할 element node의 name을 Textarea에서 선택 (만약, Child Node가 있는 경우 전부 삭제됨.)" +
                "<br>2. Attribute: 삭제할 attribute node의 name을 입력 & 해당 attribute가 소속되어 있는 element node name Textarea에서 선택" +
                "<br>3. Comment: 삭제할 comment node의 value를 Textarea에서 선택" +
                "<br>4. Text: 삭제할 text node가 속해있는 Element Node의 name을 Textarea에서 선택 -> 삭제할 Text Noded의 value를 입력</html>");

        // xml_doc -> xml_doc_copy - 복사본 생성
        try {
            xml_doc_copy = createNewDocument(xml_doc);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        setTitle("XML Final Project - Delete");

        Container cp_delete = getContentPane();
        cp_delete.setLayout(null);

        JLabel title_Delete = new JLabel("Choose the name of the node to delete in the DOM tree.");
        Font Titelfont1 = new Font("맑은 고딕", Font.BOLD, 20);
        title_Delete.setFont(Titelfont1);
        title_Delete.setLocation(110, 5);
        title_Delete.setSize(700, 40);
        cp_delete.add(title_Delete);

        JTextField deleting_attrname = new JTextField("");
        Font Titelfont2 = new Font("맑은 고딕", Font.PLAIN, 15);
        deleting_attrname.setFont(Titelfont2);
        deleting_attrname.setLocation(170, 70);
        deleting_attrname.setSize(500, 40);
        deleting_attrname.setText("Attribute Name");
        deleting_attrname.setForeground(Color.GRAY);
        deleting_attrname.setVisible(false);
        deleting_attrname.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (deleting_attrname.getText().equals("Attribute Name")) {
                    deleting_attrname.setText("");
                    deleting_attrname.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (deleting_attrname.getText().isEmpty()) {
                    deleting_attrname.setText("Attribute Name");
                    deleting_attrname.setForeground(Color.GRAY);
                }
            }
        });
        cp_delete.add(deleting_attrname);

        JTextField deleting_textname = new JTextField("");
        deleting_textname.setFont(Titelfont2);
        deleting_textname.setLocation(170, 70);
        deleting_textname.setSize(500, 40);
        deleting_textname.setText("Text Value");
        deleting_textname.setForeground(Color.GRAY);
        deleting_textname.setVisible(false);
        deleting_textname.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (deleting_textname.getText().equals("Text Value")) {
                    deleting_textname.setText("");
                    deleting_textname.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (deleting_textname.getText().isEmpty()) {
                    deleting_textname.setText("Text Value");
                    deleting_textname.setForeground(Color.GRAY);
                }
            }
        });
        cp_delete.add(deleting_textname);

        JTextArea context_delete_textarea = new JTextArea();
        context_delete_textarea.setFont(Titelfont2);
        context_delete_textarea.setLocation(20, 115);
        context_delete_textarea.setSize(400, 300);
        context_delete_textarea.setEditable(false);
        context_delete_textarea.setLineWrap(true);
        context_delete_textarea.setWrapStyleWord(true);
        cp_delete.add(context_delete_textarea);
        this.DOMPrint(xml_doc, "  ", context_delete_textarea);
        context_delete_textarea.addCaretListener(e -> {
            start_node_loc = context_delete_textarea.getSelectionStart();
            end_node_loc = context_delete_textarea.getSelectionEnd();
        });

        JScrollPane context_scroll_Delete = new JScrollPane(context_delete_textarea);
        context_scroll_Delete.setLocation(20, 115);
        context_scroll_Delete.setSize(400, 300);
        cp_delete.add(context_scroll_Delete);
        javax.swing.SwingUtilities.invokeLater(() -> context_scroll_Delete.getVerticalScrollBar().setValue(0));

        JLabel context_delete_before = new JLabel("<Before>");
        context_delete_before.setFont(Titelfont1);
        context_delete_before.setLocation(160, 415);
        context_delete_before.setSize(700, 40);
        cp_delete.add(context_delete_before);

        JTextArea context_after_Delete = new JTextArea();
        context_after_Delete.setFont(Titelfont2);
        context_after_Delete.setLocation(430, 115);
        context_after_Delete.setSize(400, 300);
        context_after_Delete.setEditable(false);
        context_after_Delete.setLineWrap(true);
        context_after_Delete.setWrapStyleWord(true);
        cp_delete.add(context_after_Delete);

        JScrollPane context_after_scroll_Delete = new JScrollPane(context_after_Delete);
        context_after_scroll_Delete.setLocation(430, 115);
        context_after_scroll_Delete.setSize(400, 300);
        cp_delete.add(context_after_scroll_Delete);

        JLabel context_delete_after = new JLabel("<After>");
        context_delete_after.setFont(Titelfont1);
        context_delete_after.setLocation(590, 415);
        context_delete_after.setSize(700, 40);
        cp_delete.add(context_delete_after);

        ButtonGroup type_buttongroup = new ButtonGroup();

        JRadioButton button_element = new JRadioButton("Element");
        button_element.setLocation(210, 40);
        button_element.setSize(110, 30);
        cp_delete.add(button_element);
        button_element.addActionListener(e -> {
            type = Node.ELEMENT_NODE;
            deleting_attrname.setVisible(false);
            deleting_attrname.setText("Attribute Name");
            deleting_attrname.setForeground(Color.GRAY);
            deleting_textname.setVisible(false);
            deleting_textname.setText("Text Value");
            deleting_textname.setForeground(Color.GRAY);
        });

        JRadioButton button_attribute = new JRadioButton("Attribute");
        button_attribute.setLocation(320, 40);
        button_attribute.setSize(110, 30);
        cp_delete.add(button_attribute);
        button_attribute.addActionListener(e -> {
            type = Node.ATTRIBUTE_NODE;
            deleting_attrname.setVisible(true);
            deleting_attrname.setText("Attribute Name");
            deleting_attrname.setForeground(Color.GRAY);
            deleting_textname.setVisible(false);
            deleting_textname.setText("Text Value");
            deleting_textname.setForeground(Color.GRAY);
        });

        JRadioButton button_comment = new JRadioButton("Comment");
        button_comment.setLocation(440, 40);
        button_comment.setSize(110, 30);
        cp_delete.add(button_comment);
        button_comment.addActionListener(e -> {
            type = Node.COMMENT_NODE;
            deleting_attrname.setVisible(false);
            deleting_attrname.setText("Attribute Name");
            deleting_attrname.setForeground(Color.GRAY);
            deleting_textname.setVisible(false);
            deleting_textname.setText("Text Value");
            deleting_textname.setForeground(Color.GRAY);
        });

        JRadioButton button_text = new JRadioButton("Text");
        button_text.setLocation(550, 40);
        button_text.setSize(110, 30);
        cp_delete.add(button_text);
        button_text.addActionListener(e -> {
            type = Node.TEXT_NODE;
            deleting_attrname.setVisible(false);
            deleting_attrname.setText("Attribute Name");
            deleting_attrname.setForeground(Color.GRAY);
            deleting_textname.setVisible(true);
            deleting_textname.setText("Text Value");
            deleting_textname.setForeground(Color.GRAY);
        });

        type_buttongroup.add(button_element);
        type_buttongroup.add(button_attribute);
        type_buttongroup.add(button_comment);
        type_buttongroup.add(button_text);

        JButton button_preview_FD = new JButton("Preview");
        button_preview_FD.setLocation(480, 460);
        button_preview_FD.setSize(120, 40);
        cp_delete.add(button_preview_FD);
        button_preview_FD.addActionListener(e -> {
            try {
                xml_doc_copy = createNewDocument(xml_doc);
            } catch (ParserConfigurationException er) {
                throw new RuntimeException(er);
            }
            context_after_Delete.setText("");
            if (type == Node.ATTRIBUTE_NODE) {
                DOMNodeDelete(xml_doc_copy, context_delete_textarea.getText(), context_delete_textarea.getSelectedText(), deleting_attrname.getText(), type, start_node_loc, end_node_loc);
            } else if (type == Node.TEXT_NODE) {
                DOMNodeDelete(xml_doc_copy, context_delete_textarea.getText(), context_delete_textarea.getSelectedText(), deleting_textname.getText(), type, start_node_loc, end_node_loc);
            } else {
                DOMNodeDelete(xml_doc_copy, context_delete_textarea.getText(), context_delete_textarea.getSelectedText(), "", type, start_node_loc, end_node_loc);
            }
            DOMPrint(xml_doc_copy, "  ", context_after_Delete);
            javax.swing.SwingUtilities.invokeLater(() -> context_after_scroll_Delete.getVerticalScrollBar().setValue(0));
            start_node_loc = -1;
            end_node_loc = -1;
        });

        JButton button_back_FD = new JButton("back");
        button_back_FD.setLocation(600, 460);
        button_back_FD.setSize(120, 40);
        cp_delete.add(button_back_FD);
        button_back_FD.addActionListener(e -> setVisible(false));

        button_enter_FD = new JButton("enter");
        button_enter_FD.setLocation(720, 460);
        button_enter_FD.setSize(120, 40);
        cp_delete.add(button_enter_FD);

        setSize(850, 540);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
