import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Save {
    public Save(Document xml_doc) {
        JFrame frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFileChooser fileChooser = new JFileChooser();

        FileFilter XMLfileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".xml");
            }
            @Override
            public String getDescription() {
                return "XML Files (*.xml)";
            }
        };

        fileChooser.setFileFilter(XMLfileFilter);

        int result = fileChooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
                DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
                LSSerializer serializer = impl.createLSSerializer();
                serializer.getDomConfig().setParameter("format-pretty-print", true);
                LSOutput output = impl.createLSOutput();

                FileOutputStream fos = new FileOutputStream(selectedFile.getAbsolutePath() + ".xml");
                output.setByteStream(fos);
                serializer.write(xml_doc, output);
                fos.close();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
