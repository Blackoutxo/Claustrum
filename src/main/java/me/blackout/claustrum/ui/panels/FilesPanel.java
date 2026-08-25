package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.*;
import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.file.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class FilesPanel extends JPanel {

    // Instances
    private FileManager file = new FileManager();

    // Pathways
    private JTextField selectFile = new JTextField();

    // Data vars
    private List<String> fileData = new ArrayList<>();
    private List<String> readableData = new ArrayList<>();

    public FilesPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Panel.PANEL_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        mainArea();
    }

    private void mainArea() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Panel.PANEL_BG);
        center.setBorder(new EmptyBorder(24, 24, 24, 16));

        // Header
        JLabel header = new JLabel("Files");
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 32f));
        header.setForeground(Panel.TEXT);
        header.setAlignmentX(CENTER_ALIGNMENT);
        header.setBorder(new EmptyBorder(16, 0, 16, 0));

        center.add(header);

        // text area
        JTextArea textArea = new JTextArea();
        textArea.setBackground(Panel.PANEL_BG.brighter());
        textArea.setForeground(Panel.TEXT);

        textArea.setBorder(new EmptyBorder(10, 10, 50, 10));

        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Buttons
        RoundedButton encryptFile = new RoundedButton("Encrypt File", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        encryptFile.addActionListener(e -> {
            textArea.setText("");
            selectFile.setText("");
            browse(selectFile);

            // Loop file data
            textArea.setText(file.read(selectFile.getText()));

            // Write data
            if (!selectFile.getText().isEmpty()) writeData(file.read(selectFile.getText()));
        });

        RoundedButton openFile = new RoundedButton("Open File", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        openFile.addActionListener(e -> {
            textArea.setText("");
            selectFile.setText("");
            browse(selectFile);

            if (selectFile.getText().isEmpty()) return;

            textArea.setText(open());
        });

        RoundedButton save = new RoundedButton("Save", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        save.addActionListener(e-> {

            writeData(textArea.getText());
        });

        // Scrollpane
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Panel.PANEL_BG.brighter());
        scroll.setOpaque(false);

        scroll.getVerticalScrollBar().setUI(new ScrollBar(Panel.ScrollThumb, Panel.ThumbHover));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setPreferredSize(new Dimension(12, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setOpaque(false);
        center.add(scroll);

        // Bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(16, 0, 0, 0));

        bottomBar.add(encryptFile);
        bottomBar.add(save);
        bottomBar.add(openFile);

        center.add(bottomBar, BorderLayout.SOUTH);

        add(center);
    }

    private void browse(JTextField text) {
        text.setText("");
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Choose backup location");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            text.setText(path);
        }
    }

    // File handling
    private String open() {
        if (selectFile.getText().isEmpty()) return "";
        String masterkey = OptionPane.showMaskedInput(null, "Enter master key", "Masterkey for this file");

        // Return on empty
        if (masterkey.isEmpty()) return "";

        // Generate a key
        Key key = null;
        try {   key = Utils.generateKey(masterkey);       } catch (GeneralSecurityException | IOException ignored) {}
        String data = null;
        try {   data = file.decryptField(file.read(selectFile.getText()), key);    } catch (GeneralSecurityException ignored) {}

        return data;
    }

    private void writeData(String value) {
        if (selectFile.getText().isEmpty()) return;
        String masterkey = OptionPane.showMaskedInput(null, "Enter master key", "Masterkey for this file");

        if (masterkey.isEmpty()) return;

        // Generate key
        Key key = null;
        try {   key = Utils.generateKey(masterkey);   } catch (GeneralSecurityException | IOException ignored) {}

        // Encrypt data
        String data = null;
        try {   data = file.encryptField(value, key);    } catch (GeneralSecurityException ignored) {}

        // write
        try (FileWriter writer = new FileWriter(selectFile.getText(), false)) {
            writer.write(data);
        } catch (IOException ignored) {}
    }
}
