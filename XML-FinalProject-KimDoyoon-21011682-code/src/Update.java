import org.w3c.dom.*;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Update extends JFrame {
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

    public void DOMNodeUpdate(Document xml_doc_copy, String tree_context, String UpdateNode, String NewName, String OriginName, int type, int start_loc, int end_loc) {
        switch (type) {
            case  Node.ELEMENT_NODE:
                if (UpdateNode != null && !(UpdateNode.isEmpty())) {
                    Elementnode_Update(xml_doc_copy, tree_context, UpdateNode, NewName, start_loc, end_loc);
                } else {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "변경할 Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case  Node.ATTRIBUTE_NODE:
                if (UpdateNode != null && !(UpdateNode.isEmpty())) {
                    Attributenode_Update(xml_doc_copy, tree_context, UpdateNode, NewName, OriginName, start_loc, end_loc);
                } else {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "변경할 Attribute가 속해있는 Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case Node.COMMENT_NODE:
                if (UpdateNode != null && !(UpdateNode.isEmpty())) {
                    Commentnode_Update(xml_doc_copy, tree_context, UpdateNode, NewName, start_loc, end_loc);
                } else {
                    // #0-1: 유저가 어떤 comment node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "변경할 Comment Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case Node.TEXT_NODE:
                if (UpdateNode != null && !(UpdateNode.isEmpty())) {
                    Textnode_Update(xml_doc_copy, tree_context, UpdateNode, OriginName, NewName, start_loc, end_loc);
                } else  {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "변경할 Text Node가 속해있는 Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            default:
                // #0-2: 유저가 어떤 type도 선택하지 않은 경우
                JOptionPane.showMessageDialog(null, "변경할 Node의 type을 선택하지 않았습니다.", "Error - Select Node type", JOptionPane.ERROR_MESSAGE);
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
    public void Elementnode_Update(Document xml_doc_copy, String tree_text, String UpdateName, String newName, int start_loc, int end_loc) {

        int num = node_loc(tree_text, "[Element] " + UpdateName, start_loc - 10, end_loc);
        int flag = 0;

        NodeList sameName = xml_doc_copy.getElementsByTagName(UpdateName);
        Node target_node = sameName.item(num);

        // Error Check start

        // #1: 유저가 선택한 node가 없는 element node인 경우
        if (target_node == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree에 없는 Element Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #2: 유저가 선택한 node가 Root node인 경우 -> 어차피 밑에서 체크함.
        // #3: 유저가 아무 이름도 없는 node로 변경하려고 하는 경우
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Node를 변경해서 새로 삽입하려고 하는 Element Node의 이름이 없습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Error Check Finish

        Node new_node = xml_doc_copy.createElement(newName);

        if (!(target_node.hasAttributes()) && !(target_node.hasChildNodes())) {
            target_node.getParentNode().replaceChild(new_node, target_node);
        } else {
            flag = JOptionPane.showConfirmDialog(null, "<html>해당 Node는 하위에 Child Node가 있거나 Attribute를 가지고 있습니다.<br>변경하시겠습니까?</html>", "Check - Update Check", JOptionPane.YES_NO_OPTION);
            if (flag == 0) {
                target_node.getParentNode().replaceChild(new_node, target_node);
            } else {
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Update Element Node Success!!!");
    }
    public void Attributenode_Update(Document xml_doc_copy, String tree_text, String select_eleName, String origin_attrName, String New_attrvalue, int start_loc, int end_loc) {

        int flag = 0;
        int num = node_loc(tree_text, "[Element] " + select_eleName, start_loc - 10, end_loc);

        NodeList sameName = xml_doc_copy.getElementsByTagName(select_eleName);
        Node target_node = sameName.item(num);
        Element target_element = (Element) target_node;

        // Error Check Start

        // #1: 유저가 선택한 node가 없는 element node인 경우
        if (target_node == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree에 없는 Element Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #2: Attribute Name이 DOM 트리에 없는 경우
        if (!(target_element.hasAttribute(origin_attrName))) {
            JOptionPane.showMessageDialog(null, "변경하고자 하는 Attribute Name이 해당 Element Node에 없습니다.", "Error - Attribute Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #3: Attribute value를 아무 값이 없는 value로 입력하는 경우
        if (New_attrvalue.isEmpty() || New_attrvalue.equals("New Attribute value")) {
            New_attrvalue = "";
            flag = JOptionPane.showConfirmDialog(null, "<html>빈 Attribute value로 변경하고자 합니다.<br>변경하시겠습니까?</html>", "Question - Attribute Node", JOptionPane.YES_NO_OPTION);
        }
        if (flag == 1) {
            return;
        }

        // Error Check Finish

        if (target_element.hasAttribute(origin_attrName)) {
            target_element.setAttribute(origin_attrName, New_attrvalue);
            JOptionPane.showMessageDialog(null, "Update Attribute Node Success!!!");
        }
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
    public void Commentnode_Update(Document xml_doc_copy, String tree_text, String Update_commentName, String New_commentName, int start_loc, int end_loc) {

        target_commentNode = null;
        comment_cnt = 0;
        int flag = 0;
        int num = node_loc(tree_text, "[COMMENT] " + Update_commentName, start_loc - 10, end_loc);

        find_commentnode(xml_doc_copy, Update_commentName, num);
        Comment new_commentnode = xml_doc_copy.createComment(New_commentName);

        // Error Check Start

        // #1: 선택한 comment node가 DOM 트리에 없는 경우
        if (target_commentNode == null) {
            JOptionPane.showMessageDialog(null, "선택한 Comment Node는 XML DOM Tree에 없는 Comment Node입니다.", "Error - Comment Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #2: 빈 comment node로 변경하려고 하는 경우
        if (New_commentName.isEmpty()) {
            flag = JOptionPane.showConfirmDialog(null, "<html>빈 Comment Node로 변경하고자 합니다.<br>변경하시겠습니까?</html>", "Question - Comment Node", JOptionPane.YES_NO_OPTION);
        }
        if (flag == 1) return;

        // Error Check Finish

        if (target_commentNode != null) {
            target_commentNode.getParentNode().replaceChild(new_commentnode, target_commentNode);
            JOptionPane.showMessageDialog(null, "Update Comment Node Success!!!");
        }
    }
    public void Textnode_Update(Document xml_doc_copy, String tree_text, String select_eleName, String origin_text, String New_text, int start_loc, int end_loc) {

        int num = node_loc(tree_text, "[Element] " + select_eleName, start_loc - 10, end_loc);
        int flag = 0;
        int flag2 = 0;

        NodeList sameName = xml_doc_copy.getElementsByTagName(select_eleName);
        Node target_node = sameName.item(num);

        // Error Check Start

        // #1: 유저가 선택한 node가 없는 element node인 경우
        if (target_node == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree에 없는 Element Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #2: 유저가 Origin text value를 선택하지 않은 경우
        if (origin_text.isEmpty() || origin_text.equals("Origin Text value")) {
            JOptionPane.showMessageDialog(null, "기존의 Text value를 입력하지 않았습니다.", "Error - Text Node", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NodeList text_check = target_node.getChildNodes();
        Node update_text_node = null;

        for (int i = 0; i < text_check.getLength(); i++) {
            if (text_check.item(i).getNodeType() == Node.TEXT_NODE) {
                if (text_check.item(i).getNodeValue().equals(origin_text)) {
                    flag = 1;
                    update_text_node = text_check.item(i);
                    break;
                }
            }
        }

        // #3: 선택한 text node가 없는 node인 경우
        if (flag == 0) {
            JOptionPane.showMessageDialog(null, "해당 Element Node는 Text Node가 없습니다.", "Error - Text Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #4: 유저가 New text value로 빈 값을 입력하려고 하는 경우
        if (New_text.isEmpty() || New_text.equals("New Text value")) {
            New_text = "";
            flag2 = JOptionPane.showConfirmDialog(null, "<html>빈 Text value로 변경하고자 합니다.<br>변경하시겠습니까?</html>", "Question - Text Node", JOptionPane.YES_NO_OPTION);
        }
        if (flag2 == 1) {
            return;
        }

        // Error Check Finish

        Node new_text_node = xml_doc_copy.createTextNode(New_text);

        target_node.replaceChild(new_text_node, update_text_node);
        JOptionPane.showMessageDialog(null, "Update Text Node Success!!!");

    }
    public int type = 0;
    public int start_node_loc = -1;
    public int end_node_loc = -1;
    public Document xml_doc_copy;
    public JButton button_enter_FU;
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
    public Update (Document xml_doc) {
        JOptionPane.showMessageDialog(null, "<html>해당 메뉴에서 새로 Node를 Update 때에는 다음 내용에 기반하여 Update해주세요." +
                "<br>1. Element: 변경할 element node의 name을 Textarea에서 선택 후 새로운 element node name 입력" +
                "<br>2. Attribute: 변경할 attribute node name과 새로운 value를 입력 & 해당 attribute가 소속되어 있는 element node의 name을 Textarea에서 선택" +
                "<br>3. Comment: 변경할 comment node value Textarea에서 선택 후 새로운 comment node value 입력" +
                "<br>4. Text: 변경할 text node value를 가지고 있는 element node의 name을 Textarea에서 선택 후 새로운 text node value 입력</html>");

        // xml_doc -> xml_doc_copy - 복사본 생성
        try {
            xml_doc_copy = createNewDocument(xml_doc);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        setTitle("XML Final Project - Update");

        Container cp_update = getContentPane();
        cp_update.setLayout(null);

        JLabel title_Update = new JLabel("Choose the name of the node to update in the DOM tree.");
        Font Titelfont1 = new Font("맑은 고딕", Font.BOLD, 20);
        title_Update.setFont(Titelfont1);
        title_Update.setLocation(110, 5);
        title_Update.setSize(700, 40);
        cp_update.add(title_Update);

        JTextField updating_context = new JTextField("");
        Font Titelfont2 = new Font("맑은 고딕", Font.PLAIN, 15);
        updating_context.setFont(Titelfont2);
        updating_context.setLocation(170, 70);
        updating_context.setSize(500, 40);
        updating_context.setVisible(false);
        cp_update.add(updating_context);

        JTextField Origin_textvalue = new JTextField();
        Origin_textvalue.setFont(Titelfont2);
        Origin_textvalue.setLocation(200, 70);
        Origin_textvalue.setSize(220, 40);
        Origin_textvalue.setText("Origin Text value");
        Origin_textvalue.setForeground(Color.GRAY);
        Origin_textvalue.setVisible(false);
        Origin_textvalue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Origin_textvalue.getText().equals("Origin Text value")) {
                    Origin_textvalue.setText("");
                    Origin_textvalue.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Origin_textvalue.getText().isEmpty()) {
                    Origin_textvalue.setText("Origin Text value");
                    Origin_textvalue.setForeground(Color.GRAY);
                }
            }
        });
        cp_update.add(Origin_textvalue);

        JTextField New_textvalue = new JTextField();
        New_textvalue.setFont(Titelfont2);
        New_textvalue.setLocation(430, 70);
        New_textvalue.setSize(220, 40);
        New_textvalue.setText("New Text value");
        New_textvalue.setForeground(Color.GRAY);
        New_textvalue.setVisible(false);
        New_textvalue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (New_textvalue.getText().equals("New Text value")) {
                    New_textvalue.setText("");
                    New_textvalue.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (New_textvalue.getText().isEmpty()) {
                    New_textvalue.setText("New Text value");
                    New_textvalue.setForeground(Color.GRAY);
                }
            }
        });
        cp_update.add(New_textvalue);

        JTextField updating_attrname = new JTextField("");
        updating_attrname.setFont(Titelfont2);
        updating_attrname.setLocation(200, 70);
        updating_attrname.setSize(220, 40);
        updating_attrname.setText("Attribute Name to update");
        updating_attrname.setForeground(Color.GRAY);
        updating_attrname.setVisible(false);
        updating_attrname.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (updating_attrname.getText().equals("Attribute Name to update")) {
                    updating_attrname.setText("");
                    updating_attrname.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (updating_attrname.getText().isEmpty()) {
                    updating_attrname.setText("Attribute Name to update");
                    updating_attrname.setForeground(Color.GRAY);
                }
            }
        });
        cp_update.add(updating_attrname);

        JTextField updating_attrvalue = new JTextField("");
        updating_attrvalue.setFont(Titelfont2);
        updating_attrvalue.setLocation(430, 70);
        updating_attrvalue.setSize(220, 40);
        updating_attrvalue.setText("New Attribute value");
        updating_attrvalue.setForeground(Color.GRAY);
        updating_attrvalue.setVisible(false);
        updating_attrvalue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (updating_attrvalue.getText().equals("New Attribute value")) {
                    updating_attrvalue.setText("");
                    updating_attrvalue.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (updating_attrvalue.getText().isEmpty()) {
                    updating_attrvalue.setText("New Attribute value");
                    updating_attrvalue.setForeground(Color.GRAY);
                }
            }
        });
        cp_update.add(updating_attrvalue);

        JTextArea context_update_textarea = new JTextArea();
        context_update_textarea.setFont(Titelfont2);
        context_update_textarea.setLocation(20, 115);
        context_update_textarea.setSize(400, 300);
        context_update_textarea.setEditable(false);
        context_update_textarea.setLineWrap(true);
        context_update_textarea.setWrapStyleWord(true);
        cp_update.add(context_update_textarea);
        this.DOMPrint(xml_doc, "  ", context_update_textarea);
        context_update_textarea.addCaretListener(e -> {
            start_node_loc = context_update_textarea.getSelectionStart();
            end_node_loc = context_update_textarea.getSelectionEnd();
        });

        JScrollPane context_scroll_Update = new JScrollPane(context_update_textarea);
        context_scroll_Update.setLocation(20, 115);
        context_scroll_Update.setSize(400, 300);
        cp_update.add(context_scroll_Update);
        javax.swing.SwingUtilities.invokeLater(() -> context_scroll_Update.getVerticalScrollBar().setValue(0));

        JLabel context_update_before = new JLabel("<Before>");
        context_update_before.setFont(Titelfont1);
        context_update_before.setLocation(160, 415);
        context_update_before.setSize(700, 40);
        cp_update.add(context_update_before);

        JTextArea context_after_Update = new JTextArea();
        context_after_Update.setFont(Titelfont2);
        context_after_Update.setLocation(430, 115);
        context_after_Update.setSize(400, 300);
        context_after_Update.setEditable(false);
        context_after_Update.setLineWrap(true);
        context_after_Update.setWrapStyleWord(true);
        cp_update.add(context_after_Update);

        JScrollPane context_after_scroll_Update = new JScrollPane(context_after_Update);
        context_after_scroll_Update.setLocation(430, 115);
        context_after_scroll_Update.setSize(400, 300);
        cp_update.add(context_after_scroll_Update);

        JLabel context_update_after = new JLabel("<After>");
        context_update_after.setFont(Titelfont1);
        context_update_after.setLocation(590, 415);
        context_update_after.setSize(700, 40);
        cp_update.add(context_update_after);

        ButtonGroup type_buttongroup = new ButtonGroup();

        JRadioButton button_element = new JRadioButton("Element");
        button_element.setLocation(210, 40);
        button_element.setSize(110, 30);
        cp_update.add(button_element);
        button_element.addActionListener(e -> {
            type = Node.ELEMENT_NODE;
            updating_context.setVisible(true);
            updating_context.setText("");
            updating_attrvalue.setVisible(false);
            updating_attrvalue.setText("New Attribute value");
            updating_attrvalue.setForeground(Color.GRAY);
            updating_attrname.setVisible(false);
            updating_attrname.setText("Attribute Name to update");
            updating_attrname.setForeground(Color.GRAY);
            Origin_textvalue.setVisible(false);
            Origin_textvalue.setText("Origin Text value");
            Origin_textvalue.setForeground(Color.GRAY);
            New_textvalue.setVisible(false);
            New_textvalue.setText("New Text value");
            New_textvalue.setForeground(Color.GRAY);
        });

        JRadioButton button_attribute = new JRadioButton("Attribute");
        button_attribute.setLocation(320, 40);
        button_attribute.setSize(110, 30);
        cp_update.add(button_attribute);
        button_attribute.addActionListener(e -> {
            type = Node.ATTRIBUTE_NODE;
            updating_context.setVisible(false);
            updating_context.setText("");
            updating_attrvalue.setVisible(true);
            updating_attrvalue.setText("New Attribute value");
            updating_attrvalue.setForeground(Color.GRAY);
            updating_attrname.setVisible(true);
            updating_attrname.setText("Attribute Name to update");
            updating_attrname.setForeground(Color.GRAY);
            Origin_textvalue.setVisible(false);
            Origin_textvalue.setText("Origin Text value");
            Origin_textvalue.setForeground(Color.GRAY);
            New_textvalue.setVisible(false);
            New_textvalue.setText("New Text value");
            New_textvalue.setForeground(Color.GRAY);
        });

        JRadioButton button_comment = new JRadioButton("Comment");
        button_comment.setLocation(440, 40);
        button_comment.setSize(110, 30);
        cp_update.add(button_comment);
        button_comment.addActionListener(e -> {
            type = Node.COMMENT_NODE;
            updating_context.setVisible(true);
            updating_context.setText("");
            updating_attrvalue.setVisible(false);
            updating_attrvalue.setText("New Attribute value");
            updating_attrvalue.setForeground(Color.GRAY);
            updating_attrname.setVisible(false);
            updating_attrname.setText("Attribute Name to update");
            updating_attrname.setForeground(Color.GRAY);
            Origin_textvalue.setVisible(false);
            Origin_textvalue.setText("Origin Text value");
            Origin_textvalue.setForeground(Color.GRAY);
            New_textvalue.setVisible(false);
            New_textvalue.setText("New Text value");
            New_textvalue.setForeground(Color.GRAY);
        });

        JRadioButton button_text = new JRadioButton("Text");
        button_text.setLocation(550, 40);
        button_text.setSize(110, 30);
        cp_update.add(button_text);
        button_text.addActionListener(e -> {
            type = Node.TEXT_NODE;
            updating_context.setVisible(false);
            updating_context.setText("");
            updating_attrvalue.setVisible(false);
            updating_attrvalue.setText("New Attribute value");
            updating_attrvalue.setForeground(Color.GRAY);
            updating_attrname.setVisible(false);
            updating_attrname.setText("Attribute Name to update");
            updating_attrname.setForeground(Color.GRAY);
            Origin_textvalue.setVisible(true);
            Origin_textvalue.setText("Origin Text value");
            Origin_textvalue.setForeground(Color.GRAY);
            New_textvalue.setVisible(true);
            New_textvalue.setText("New Text value");
            New_textvalue.setForeground(Color.GRAY);
        });

        type_buttongroup.add(button_element);
        type_buttongroup.add(button_attribute);
        type_buttongroup.add(button_comment);
        type_buttongroup.add(button_text);

        JButton button_preview_FU = new JButton("Preview");
        button_preview_FU.setLocation(480, 460);
        button_preview_FU.setSize(120, 40);
        cp_update.add(button_preview_FU);
        button_preview_FU.addActionListener(e -> {
            try {
                xml_doc_copy = createNewDocument(xml_doc);
            } catch (ParserConfigurationException er) {
                throw new RuntimeException(er);
            }
            context_after_Update.setText("");
            if (type == Node.ATTRIBUTE_NODE) {
                DOMNodeUpdate(xml_doc_copy, context_update_textarea.getText(), context_update_textarea.getSelectedText(), updating_attrname.getText(), updating_attrvalue.getText(), type, start_node_loc, end_node_loc);
            } else if (type == Node.TEXT_NODE){
                DOMNodeUpdate(xml_doc_copy, context_update_textarea.getText(), context_update_textarea.getSelectedText(), New_textvalue.getText(), Origin_textvalue.getText(), type, start_node_loc, end_node_loc);
            } else {
                DOMNodeUpdate(xml_doc_copy, context_update_textarea.getText(), context_update_textarea.getSelectedText(), updating_context.getText(), "", type, start_node_loc, end_node_loc);
            }
            DOMPrint(xml_doc_copy, "  ", context_after_Update);
            javax.swing.SwingUtilities.invokeLater(() -> context_after_scroll_Update.getVerticalScrollBar().setValue(0));
            start_node_loc = -1;
            end_node_loc = -1;
        });

        JButton button_back_FU = new JButton("back");
        button_back_FU.setLocation(600, 460);
        button_back_FU.setSize(120, 40);
        cp_update.add(button_back_FU);
        button_back_FU.addActionListener(e -> setVisible(false));

        button_enter_FU = new JButton("enter");
        button_enter_FU.setLocation(720, 460);
        button_enter_FU.setSize(120, 40);
        cp_update.add(button_enter_FU);

        setSize(850, 540);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
