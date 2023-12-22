import org.w3c.dom.*;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class Insert extends JFrame {
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

    public void DOMNodeInsert(String text, String insertNode, String insertNode_value, String chooseNode, Document xml_doc_copy, int type, int start_loc, int end_loc) {
        switch (type) {
            case  Node.ELEMENT_NODE:
                if (chooseNode != null && !(chooseNode.isEmpty())) {
                    Elementnode_Insert(xml_doc_copy, text, chooseNode, insertNode, start_loc, end_loc);
                } else  {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "삽입할 위치의 다음 Element Node 또는 Parent Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case  Node.ATTRIBUTE_NODE:
                if (chooseNode != null && !(chooseNode.isEmpty())) {
                    Attributenode_Insert(xml_doc_copy, text, chooseNode, insertNode, insertNode_value, start_loc, end_loc);
                } else  {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "삽입할 Attribute가 속해있는 Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case Node.COMMENT_NODE:
                Commentnode_Insert(xml_doc_copy, insertNode);
                break;
            case Node.TEXT_NODE:
                if (chooseNode != null && !(chooseNode.isEmpty())) {
                    Textnode_Insert(xml_doc_copy, text, chooseNode, insertNode, start_loc, end_loc);
                } else  {
                    // #0-1: 유저가 어떤 element node도 선택하지 않은 경우
                    JOptionPane.showMessageDialog(null, "삽입할 Text Node가 속해있는 Element Node를 선택하지 않았습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
                }
                break;
            default:
                // #0-2: 유저가 어떤 type도 선택하지 않은 경우
                JOptionPane.showMessageDialog(null, "삽입할 Node의 type을 선택하지 않았습니다.", "Error - Select Node type", JOptionPane.ERROR_MESSAGE);
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
    public void Elementnode_Insert(Document xml_doc_copy, String text, String selectName, String eleName, int start_loc, int end_loc) {

        int num;
        num = node_loc(text, "[Element] " + selectName, start_loc - 10, end_loc);

        NodeList sameName = xml_doc_copy.getElementsByTagName(selectName);
        Node target_node = sameName.item(num);

        //Error Check Start

        // #1: 유저가 선택한 node가 없는 element node인 경우
        if (target_node == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree에 없는 Element Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #2: 유저가 선택한 node가 Root node인 경우
        if (target_node == xml_doc_copy.getDocumentElement() && !(check_append.isSelected())) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree의 Root Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #3: 유저가 아무 이름이 없는 node를 삽입하려고 하는 경우
        if (eleName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "새로 삽입하려고 하는 Element Node의 이름이 없습니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Error Check Finish

        Element new_node = xml_doc_copy.createElement(eleName);
        Text new_blank_node = xml_doc_copy.createTextNode("");
        Text first_blank_node = xml_doc_copy.createTextNode("");

        if (check_append.isSelected()) {
            if ((!target_node.hasChildNodes())) {
                target_node.appendChild(first_blank_node);
            }
            target_node.appendChild(new_node);
            target_node.appendChild(new_blank_node);
        } else {
            target_node.getParentNode().insertBefore(new_node, target_node);
            target_node.getParentNode().insertBefore(new_blank_node, target_node);
        }
        JOptionPane.showMessageDialog(null, "Insert Element Node Success!!!");
    }
    public void Attributenode_Insert(Document xml_doc_copy, String text, String selectName, String attrName, String attrvalue, int start_loc, int end_loc) {

        int flag = 0;
        int num;
        num = node_loc(text, "[Element] " + selectName, start_loc - 10, end_loc);

        NodeList sameName = xml_doc_copy.getElementsByTagName(selectName);
        Node target_node = sameName.item(num);
        Element target_element = (Element) target_node;

        //Error Check Start

        // #1: 유저가 선택한 node가 없는 element node인 경우
        if (target_element == null) {
            JOptionPane.showMessageDialog(null, "선택한 Element Node는 XML DOM Tree에 없는 Element Node입니다.", "Error - Element Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #2: 유저가 작성한 AttributeName이 이미 있는 Attribute인 경우
        if (target_element.hasAttribute(attrName)) {
            JOptionPane.showMessageDialog(null, "<html>새로 삽입하려고 하는 Attribute Name은 해당 Element Node에 이미 있는 Attribute입니다.<br>Update menu에서 변경해주시기 바랍니다.</html>", "Error - Attribute Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #3: 유저가 아무 이름이 없는 Attribute를 삽입하려고 하는 경우
        if (attrName.isEmpty() || attrName.equals("Attribute Name")) {
            JOptionPane.showMessageDialog(null, "새로 삽입하려고 하는 Attribute의 Name이 없습니다.", "Error - Attribute Node", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // #4: 유저가 아무 값이 없는 Attribute value를 삽입하려고 하는 경우
        if (attrvalue.isEmpty() || attrvalue.equals("Attribute value")) {
            attrvalue = "";
            flag = JOptionPane.showConfirmDialog(null, "새로 삽입하려고 하는 Attribute의 Value가 없습니다.", "Question - Attribute Node", JOptionPane.YES_NO_OPTION);
        }
        if (flag == 1) {
            return;
        }

        //Error Check Finish

        if (!(target_element.hasAttribute(attrName))) {
            Attr new_attrnode = xml_doc_copy.createAttribute(attrName);
            new_attrnode.setValue(attrvalue);
            target_element.setAttributeNode(new_attrnode);
            JOptionPane.showMessageDialog(null, "Insert Attribute Node Success!!!");
        }
    }
    public void Commentnode_Insert(Document xml_doc_copy, String comment_context) {

        int flag = 0;
        int len;
        len = xml_doc_copy.getChildNodes().getLength();

        NodeList childnodes = xml_doc_copy.getChildNodes();
        Comment new_comment = xml_doc_copy.createComment(comment_context);

        // Error Check start

        // #1: 유저가 빈 comment_context를 삽입하려고 하는 경우
        if (comment_context.isEmpty()) {
            flag = JOptionPane.showConfirmDialog(null, "<html>빈 Comment Node 삽입을 시도하고 있습니다.<br>삽입하시겠습니까?</html>", "Question - Comment node", JOptionPane.YES_NO_OPTION);
        }
        if (flag == 1) return;

        // Error Check finish

        xml_doc_copy.insertBefore(new_comment, childnodes.item(len - 1));
        JOptionPane.showMessageDialog(null, "Insert Comment Node Success!!!");
    }
    public void Textnode_Insert(Document xml_doc_copy, String text, String select_eleName, String new_text_value, int start_loc, int end_loc) {

        int flag1 = 0;
        int flag2 = 0;
        int num;
        num = node_loc(text, "[Element] " + select_eleName, start_loc - 10, end_loc);

        NodeList sameName = xml_doc_copy.getElementsByTagName(select_eleName);
        Node target_node = sameName.item(num);

        // Error Check start

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
        // #3: 유저가 빈 Text Node를 삽입하려고 하는 경우
        if (new_text_value.isEmpty()) {
            flag2 = JOptionPane.showConfirmDialog(null, "<html>빈 Text Node 삽입을 시도하고 있습니다.<br>삽입하시겠습니까?</html>", "Question - Text node", JOptionPane.YES_NO_OPTION);
        }
        if (flag2 == 1) return;

        // Error Check finish

        Text new_text = xml_doc_copy.createTextNode(new_text_value);
        target_node.appendChild(new_text);
        JOptionPane.showMessageDialog(null, "Insert Text Node Success!!!");
    }
    public int type = 0;
    public int start_node_loc = -1;
    public int end_node_loc = -1;
    public Document xml_doc_copy = null;
    public JButton button_enter_FI;
    public JCheckBox check_append;
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
    public Insert (Document xml_doc) {
        JOptionPane.showMessageDialog(null, "<html>해당 메뉴에서 새로 Node를 Insert할 때에는 다음 내용에 기반하여 Insert해주세요." +
                "<br>1. Element: 새로 삽입할 node의 다음에 위치한 element node 선택 or" +
                "<br>Last(First) Child로 입력하고 싶은 경우 체크박스를 체크하고, ParentNode를 선택 -> 새로 삽입할 element node name 입력" +
                "<br>2. Attribute: 새로 삽입할 Attribute가 속한 element node 선택 -> 새로 삽입할 attribute node name과 attribute node value 입력" +
                "<br>3. Comment: 새로 삽입할 comment node value 입력(Comment Node는 항상 Root Node 위에 삽입됩니다.)" +
                "<br>4. Text: 새로 삽입할 Text node의 Parent element node 선택 -> 새로 삽입할 text node value 입력</html>");

        // xml_doc -> xml_doc_copy - 복사본 생성
        try {
            xml_doc_copy = createNewDocument(xml_doc);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        setTitle("XML Final Project - Insert");

        Container cp_insert = getContentPane();
        cp_insert.setLayout(null);

        JLabel title_Insert = new JLabel("Please enter the name of the node to insert in the DOM tree.");
        Font Titelfont1 = new Font("맑은 고딕", Font.BOLD, 20);
        title_Insert.setFont(Titelfont1);
        title_Insert.setLocation(110, 5);
        title_Insert.setSize(700, 40);
        cp_insert.add(title_Insert);

        JTextField inserting_context = new JTextField("");
        Font Titelfont2 = new Font("맑은 고딕", Font.PLAIN, 15);
        inserting_context.setFont(Titelfont2);
        inserting_context.setLocation(170, 70);
        inserting_context.setSize(500, 40);
        inserting_context.setVisible(false);
        cp_insert.add(inserting_context);

        check_append = new JCheckBox("Insert Last Child");
        check_append.setSize(200,50);
        check_append.setLocation(680, 65);
        check_append.setVisible(false);
        cp_insert.add(check_append);

        JTextField inserting_attrname = new JTextField("");
        inserting_attrname.setFont(Titelfont2);
        inserting_attrname.setLocation(200, 70);
        inserting_attrname.setSize(220, 40);
        inserting_attrname.setText("Attribute Name");
        inserting_attrname.setForeground(Color.GRAY);
        inserting_attrname.setVisible(false);
        inserting_attrname.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inserting_attrname.getText().equals("Attribute Name")) {
                    inserting_attrname.setText("");
                    inserting_attrname.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inserting_attrname.getText().isEmpty()) {
                    inserting_attrname.setText("Attribute Name");
                    inserting_attrname.setForeground(Color.GRAY);
                }
            }
        });
        cp_insert.add(inserting_attrname);

        JTextField inserting_attrvalue = new JTextField("");
        inserting_attrvalue.setFont(Titelfont2);
        inserting_attrvalue.setLocation(430, 70);
        inserting_attrvalue.setSize(220, 40);
        inserting_attrvalue.setText("Attribute value");
        inserting_attrvalue.setForeground(Color.GRAY);
        inserting_attrvalue.setVisible(false);
        inserting_attrvalue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inserting_attrvalue.getText().equals("Attribute value")) {
                    inserting_attrvalue.setText("");
                    inserting_attrvalue.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inserting_attrvalue.getText().isEmpty()) {
                    inserting_attrvalue.setText("Attribute value");
                    inserting_attrvalue.setForeground(Color.GRAY);
                }
            }
        });
        cp_insert.add(inserting_attrvalue);

        JTextArea context_insert = new JTextArea();
        context_insert.setFont(Titelfont2);
        context_insert.setLocation(20, 115);
        context_insert.setSize(400, 300);
        context_insert.setEditable(false);
        context_insert.setLineWrap(true);
        context_insert.setWrapStyleWord(true);
        cp_insert.add(context_insert);
        this.DOMPrint(xml_doc, "  ", context_insert);
        context_insert.addCaretListener(e -> {
            start_node_loc = context_insert.getSelectionStart();
            end_node_loc = context_insert.getSelectionEnd();
        });

        JScrollPane context_scroll_Insert = new JScrollPane(context_insert);
        context_scroll_Insert.setLocation(20, 115);
        context_scroll_Insert.setSize(400, 300);
        cp_insert.add(context_scroll_Insert);
        javax.swing.SwingUtilities.invokeLater(() -> context_scroll_Insert.getVerticalScrollBar().setValue(0));

        JLabel context_insert_before = new JLabel("<Before>");
        context_insert_before.setFont(Titelfont1);
        context_insert_before.setLocation(160, 415);
        context_insert_before.setSize(700, 40);
        cp_insert.add(context_insert_before);

        JTextArea context_after_Insert = new JTextArea();
        context_after_Insert.setFont(Titelfont2);
        context_after_Insert.setLocation(430, 115);
        context_after_Insert.setSize(400, 300);
        context_after_Insert.setEditable(false);
        context_after_Insert.setLineWrap(true);
        context_after_Insert.setWrapStyleWord(true);
        cp_insert.add(context_after_Insert);

        JScrollPane context_after_scroll_Insert = new JScrollPane(context_after_Insert);
        context_after_scroll_Insert.setLocation(430, 115);
        context_after_scroll_Insert.setSize(400, 300);
        cp_insert.add(context_after_scroll_Insert);

        JLabel context_insert_after = new JLabel("<After>");
        context_insert_after.setFont(Titelfont1);
        context_insert_after.setLocation(590, 415);
        context_insert_after.setSize(700, 40);
        cp_insert.add(context_insert_after);

        ButtonGroup type_buttongroup = new ButtonGroup();

        JRadioButton button_element = new JRadioButton("Element");
        button_element.setLocation(210, 40);
        button_element.setSize(110, 30);
        cp_insert.add(button_element);
        button_element.addActionListener(e -> {
            type = Node.ELEMENT_NODE;
            check_append.setVisible(true);
            inserting_context.setVisible(true);
            inserting_attrname.setVisible(false);
            inserting_attrvalue.setVisible(false);
            check_append.setSelected(false);
            inserting_context.setText("");
            inserting_attrname.setText("Attribute Name");
            inserting_attrname.setForeground(Color.GRAY);
            inserting_attrvalue.setText("Attribute value");
            inserting_attrvalue.setForeground(Color.GRAY);
        });

        JRadioButton button_attribute = new JRadioButton("Attribute");
        button_attribute.setLocation(320, 40);
        button_attribute.setSize(110, 30);
        cp_insert.add(button_attribute);
        button_attribute.addActionListener(e -> {
            type = Node.ATTRIBUTE_NODE;
            check_append.setVisible(false);
            inserting_context.setVisible(false);
            inserting_attrname.setVisible(true);
            inserting_attrvalue.setVisible(true);
            check_append.setSelected(false);
            inserting_context.setText("");
            inserting_attrname.setText("Attribute Name");
            inserting_attrname.setForeground(Color.GRAY);
            inserting_attrvalue.setText("Attribute value");
            inserting_attrvalue.setForeground(Color.GRAY);
        });

        JRadioButton button_comment = new JRadioButton("Comment");
        button_comment.setLocation(440, 40);
        button_comment.setSize(110, 30);
        cp_insert.add(button_comment);
        button_comment.addActionListener(e -> {
            type = Node.COMMENT_NODE;
            check_append.setVisible(false);
            inserting_context.setVisible(true);
            inserting_attrname.setVisible(false);
            inserting_attrvalue.setVisible(false);
            check_append.setSelected(false);
            inserting_context.setText("");
            inserting_attrname.setText("Attribute Name");
            inserting_attrname.setForeground(Color.GRAY);
            inserting_attrvalue.setText("Attribute value");
            inserting_attrvalue.setForeground(Color.GRAY);
        });

        JRadioButton button_text = new JRadioButton("Text");
        button_text.setLocation(550, 40);
        button_text.setSize(110, 30);
        cp_insert.add(button_text);
        button_text.addActionListener(e -> {
            type = Node.TEXT_NODE;
            check_append.setVisible(false);
            inserting_context.setVisible(true);
            inserting_attrname.setVisible(false);
            inserting_attrvalue.setVisible(false);
            check_append.setSelected(false);
            inserting_context.setText("");
            inserting_attrname.setText("Attribute Name");
            inserting_attrname.setForeground(Color.GRAY);
            inserting_attrvalue.setText("Attribute value");
            inserting_attrvalue.setForeground(Color.GRAY);

        });

        type_buttongroup.add(button_element);
        type_buttongroup.add(button_attribute);
        type_buttongroup.add(button_comment);
        type_buttongroup.add(button_text);

        JButton button_preview_FI = new JButton("Preview");
        button_preview_FI.setLocation(480, 460);
        button_preview_FI.setSize(120, 40);
        cp_insert.add(button_preview_FI);
        button_preview_FI.addActionListener(e -> {
            try {
                xml_doc_copy = createNewDocument(xml_doc);
            } catch (ParserConfigurationException er) {
                throw new RuntimeException(er);
            }
            context_after_Insert.setText("");
            if (type == Node.ATTRIBUTE_NODE) {
                DOMNodeInsert(context_insert.getText(), inserting_attrname.getText(), inserting_attrvalue.getText(), context_insert.getSelectedText(), xml_doc_copy, type, start_node_loc, end_node_loc);
            } else {
                DOMNodeInsert(context_insert.getText(), inserting_context.getText(), "", context_insert.getSelectedText(), xml_doc_copy, type, start_node_loc, end_node_loc);
            }
            DOMPrint(xml_doc_copy, "  ", context_after_Insert);
            javax.swing.SwingUtilities.invokeLater(() -> context_after_scroll_Insert.getVerticalScrollBar().setValue(0));
            start_node_loc = -1;
            end_node_loc = -1;
        });

        JButton button_back_FI = new JButton("back");
        button_back_FI.setLocation(600, 460);
        button_back_FI.setSize(120, 40);
        cp_insert.add(button_back_FI);
        button_back_FI.addActionListener(e -> setVisible(false));

        button_enter_FI = new JButton("enter");
        button_enter_FI.setLocation(720, 460);
        button_enter_FI.setSize(120, 40);
        cp_insert.add(button_enter_FI);

        setSize(850, 540);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
