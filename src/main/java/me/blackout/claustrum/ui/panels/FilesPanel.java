package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.RoundedButton;
import me.blackout.claustrum.ui.elements.RoundedPanel;
import me.blackout.claustrum.ui.elements.ScrollBar;
import me.blackout.claustrum.ui.elements.TextFieldUI;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class FilesPanel extends JPanel {
    private JTextField selectFile = new JTextField();

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
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        center.add(header);

        // File data card
        RoundedPanel fileDataCard= new RoundedPanel(10);
        fileDataCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        fileDataCard.setBackground(Panel.PANEL_BG.brighter());
        fileDataCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // text area
        JTextArea textArea = new JTextArea();
        textArea.setBackground(Panel.PANEL_BG);
        textArea.setForeground(Panel.TEXT);
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        fileDataCard.add(textArea);

        center.add(fileDataCard);

        // Scrollpane
        JScrollPane scroll = new JScrollPane(fileDataCard);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Panel.PANEL_BG);
        scroll.setOpaque(false);

        scroll.getVerticalScrollBar().setUI(new ScrollBar(Panel.ScrollThumb, Panel.ThumbHover));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setPreferredSize(new Dimension(12, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setOpaque(false);
        center.add(scroll);

        add(center);
        add(Box.createVerticalStrut(10));
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
}
