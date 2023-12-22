import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;

public class Main extends JFrame {
    public String filepath = null;
    public File file = null;
    public JLabel File_condition = null;

    public DOM dom_funciton = null;
    public Document xml_doc = null;
    private static boolean setValidation = false; //defaults
    public Main() {
        setTitle("XML Final Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 코드 실행 종료시 자동 종료

        Container cp_main = getContentPane();
        cp_main.setLayout(null);

        JLabel main_title = new JLabel("XML 기반 스포츠 경기 및 선수 데이터 관리 시스템");
        Font Titelfont = new Font("맑은 고딕", Font.BOLD, 20);
        main_title.setFont(Titelfont);
        main_title.setLocation(90, 10);
        main_title.setSize(500, 50);
        cp_main.add(main_title);

        File_condition = new JLabel("File isn't uploaded!!");
        Font Titelfont1 = new Font("맑은 고딕", Font.PLAIN, 15);
        File_condition.setFont(Titelfont1);
        File_condition.setForeground(Color.red);
        File_condition.setLocation(50, 55);
        File_condition.setSize(500, 70);
        cp_main.add(File_condition);

        JButton b1 = new JButton("Load");
        b1.setLocation(80, 150);
        b1.setSize(120, 40);
        cp_main.add(b1);
        b1.addActionListener(e -> {
            /* 파일 불러오기 */
            Load load = new Load();
            filepath = load.File_Load();
            if (filepath == null) {
                xml_doc = null;
                File_condition.setText("File isn't uploaded!!");
                File_condition.setForeground(Color.red);
                JOptionPane.showMessageDialog(null, "Open failed!!!");
                return;
            }

            file = new File(filepath);
            String check = "";

            if (file.getName().toLowerCase().endsWith(".xml")) {
                /* Validation check */
                String[] type = {"XML Schema Parsing", "DTD Parsing", "Nothing"};
                check = (String) JOptionPane.showInputDialog(null, "Check Validation", "Check Validation", JOptionPane.QUESTION_MESSAGE, null, type, type[2]);
                dom_funciton = new DOM();
                switch (check) {
                    case "XML Schema Parsing":
                        xml_doc = dom_funciton.ValidationCheck_XSD(file);
                        break;
                    case "DTD Parsing":
                        xml_doc = dom_funciton.ValidationCheck_DTD(file);
                        break;
                    case "Nothing":
                        xml_doc = dom_funciton.openDOMTraverse(setValidation, file);
                        break;
                    default:
                        break;
                }
            } else {
                File_condition.setText("File isn't uploaded!!");
                File_condition.setForeground(Color.red);
                JOptionPane.showMessageDialog(null, "Open the .xml file!!!");
            }

            if (xml_doc != null) {
                File_condition.setText("<html>File is uploaded!!!" + "<br>Validation: " + check + "<br>File: " + file.getName() + "</html>");
                File_condition.setForeground(Color.GREEN.darker());
                JOptionPane.showMessageDialog(null, "Open success!!!");
            } else {
                File_condition.setText("File isn't uploaded!!");
                File_condition.setForeground(Color.red);
                JOptionPane.showMessageDialog(null, "Open failed!!!");
            }
        });

        JButton b2 = new JButton("Print");
        b2.setLocation(240, 150);
        b2.setSize(120, 40);
        cp_main.add(b2);
        b2.addActionListener(e -> {
            if (File_condition.getForeground().equals(Color.GREEN.darker())) {
                new Print(xml_doc);
            }else {
                JOptionPane.showMessageDialog(null, "The file has not been uploaded, so you cannot use this menu.");
            }
        });

        JButton b3 = new JButton("Find");
        b3.setLocation(400, 150);
        b3.setSize(120, 40);
        cp_main.add(b3);
        b3.addActionListener(e -> {
            if (File_condition.getForeground().equals(Color.GREEN.darker())) {
                new Find(xml_doc);
            }else {
                JOptionPane.showMessageDialog(null, "The file has not been uploaded, so you cannot use this menu.");
            }
        });

        JButton b4 = new JButton("Save");
        b4.setLocation(80, 220);
        b4.setSize(120, 40);
        cp_main.add(b4);
        b4.addActionListener(e -> {
            if (File_condition.getForeground().equals(Color.GREEN.darker())) {
                new Save(xml_doc);
            }else {
                JOptionPane.showMessageDialog(null, "The file has not been uploaded, so you cannot use this menu.");
            }
        });

        JButton b5 = new JButton("Insert");
        b5.setLocation(240, 220);
        b5.setSize(120, 40);
        cp_main.add(b5);
        b5.addActionListener(e -> {
                if (File_condition.getForeground().equals(Color.GREEN.darker())) {
                    Insert insert = new Insert(xml_doc);
                    insert.button_enter_FI.addActionListener(e1 -> {
                        try {
                            xml_doc = Insert.createNewDocument(insert.xml_doc_copy);
                        } catch (ParserConfigurationException eI) {
                            throw new RuntimeException(eI);
                        }
                        insert.setVisible(false);
                    });
                }else {
                    JOptionPane.showMessageDialog(null, "The file has not been uploaded, so you cannot use this menu.");
                }
        });

        JButton b6 = new JButton("Update");
        b6.setLocation(400, 220);
        b6.setSize(120, 40);
        cp_main.add(b6);
        b6.addActionListener(e -> {
            if (File_condition.getForeground().equals(Color.GREEN.darker())) {
                Update update = new Update(xml_doc);
                update.button_enter_FU.addActionListener(e13 -> {
                    try {
                        xml_doc = Update.createNewDocument(update.xml_doc_copy);
                    } catch (ParserConfigurationException em) {
                        throw new RuntimeException(em);
                    }
                    update.setVisible(false);
                });
            }else {
                JOptionPane.showMessageDialog(null, "The file has not been uploaded, so you cannot use this menu.");
            }
        });

        JButton b7 = new JButton("Delete");
        b7.setLocation(80, 290);
        b7.setSize(120, 40);
        cp_main.add(b7);
        b7.addActionListener(e -> {
            if (File_condition.getForeground().equals(Color.GREEN.darker())) {
                Delete delete = new Delete(xml_doc);
                delete.button_enter_FD.addActionListener(e12 -> {
                    try {
                        xml_doc = Delete.createNewDocument(delete.xml_doc_copy);
                    } catch (ParserConfigurationException em) {
                        throw new RuntimeException(em);
                    }
                    delete.setVisible(false);
                });
            }else {
                JOptionPane.showMessageDialog(null, "The file has not been uploaded, so you cannot use this menu.");
            }
        });

        JButton b8 = new JButton("Make");
        b8.setLocation(240, 290);
        b8.setSize(120, 40);
        cp_main.add(b8);
        b8.addActionListener(e -> {
            if (File_condition.getForeground().equals(Color.GREEN.darker())) {
                new Save(xml_doc);
                xml_doc = null;
                DOM dom_funciton2 = new DOM();
                xml_doc = dom_funciton2.NewDOMtree();
                String New_rootname = JOptionPane.showInputDialog(null, "Enter the name of the new root node.", "Input - Root Node", JOptionPane.QUESTION_MESSAGE);
                Element root = xml_doc.createElement(New_rootname);
                xml_doc.appendChild(root);
                /* 이 부분 Root node 생성하는 코드 있어야됨. */
                File_condition.setText("Unsaved new file is created.");
                File_condition.setForeground(Color.GREEN.darker());
            } else {
                xml_doc = null;
                DOM dom_funciton2 = new DOM();
                xml_doc = dom_funciton2.NewDOMtree();
                String New_rootname = JOptionPane.showInputDialog(null, "Enter the name of the new root node.", "Input - Root Node", JOptionPane.QUESTION_MESSAGE);
                Element root = xml_doc.createElement(New_rootname);
                xml_doc.appendChild(root);
                /* 이 부분 Root node 생성하는 코드 있어야됨. */
                File_condition.setText("Unsaved new file is created.");
                File_condition.setForeground(Color.GREEN.darker());
            }
        });

        JButton b9 = new JButton("Exit");
        b9.setLocation(400, 290);
        b9.setSize(120, 40);
        cp_main.add(b9);
        b9.addActionListener(e -> {
//            new FileValidation();
            new Save(xml_doc);
            System.exit(0);
        });

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public static void main (String[] args) {
        new Main();
    }
}
