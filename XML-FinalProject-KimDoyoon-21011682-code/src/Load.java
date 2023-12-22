import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class Load {
    public String File_Load() {
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

        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}
