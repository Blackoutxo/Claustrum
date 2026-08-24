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

        // Button
        RoundedButton encryptFile = new RoundedButton("Encrypt File", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        encryptFile.addActionListener(e -> {
            browse(selectFile);
        });

        // text area
        JTextArea textArea = new JTextArea();
        textArea.setBackground(Panel.PANEL_BG.brighter());
        textArea.setForeground(Panel.TEXT);

        textArea.setBorder(new EmptyBorder(10, 10, 50, 10));

        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

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
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(16, 0, 0, 0)); // space above the button

        //bottomBar.add(addButton);

        add(center);
    }

    private void browse(JTextField text) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Choose backup location");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            text.setText(path);
        }
    }

    private void decrypt() {
        for (String string : fileData) {

        }
    }

    private void readEncrypt() {
        fileData.add(file.read(selectFile.getText()));
    }

    private void writeData() {
        String masterkey = OptionPane.showMaskedInput(null, "Enter master key", "A masterkey you use to secure this file separate from app's");
        StringBuilder line = new StringBuilder();

        Key key = null;
        try {
            key = Utils.generateKey(masterkey);
        } catch (GeneralSecurityException | IOException ignored) {}

        for (String string : fileData) {
            String data = null;
            try { data = file.encryptField(string, key);   } catch (GeneralSecurityException ignored) {}
            line.append(data);
        }

        try (FileWriter writer = new FileWriter(selectFile.getText(), true)) {
            writer.write(line.toString());
            writer.write(System.lineSeparator());
        } catch (IOException ignored) {}
    }
}
