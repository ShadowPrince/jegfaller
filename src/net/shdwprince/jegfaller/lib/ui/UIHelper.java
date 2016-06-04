package net.shdwprince.jegfaller.lib.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by sp on 5/29/16.
 */
public class UIHelper {
    protected JWindow window;
    protected JFileChooser fileChooser;

    protected static UIHelper instance;

    public static UIHelper instance() {
        if (instance == null) {
            instance = new UIHelper();
            instance.window = new JWindow();
            instance.fileChooser = new JFileChooser();
            instance.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            instance.fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        return instance;
    }

    public File selectFile2() {
        System.out.println("SELECT FILE INTER");
        int result = this.fileChooser.showOpenDialog(this.window);
        System.out.println("chooser end");
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = this.fileChooser.getSelectedFile();
            return selectedFile;
        } else {
            return null;
        }
    }

    public File selectFile() {
        FileDialog fd = new FileDialog(new JFrame(), "Choose a file", FileDialog.LOAD);
        fd.setVisible(true);
        String filename = fd.getFile();
        if (filename != null) {
            return new File(fd.getDirectory() + filename);
        } else {
            return null;
        }
    }
}
