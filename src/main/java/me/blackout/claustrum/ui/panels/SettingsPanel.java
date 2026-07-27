package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.TextFieldUI;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public JPanel settings() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Margin
        panel.setBackground(Panel.PANEL_BG);

        // Header
        JLabel header = new JLabel("SETTINGS");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 40f));
        header.setForeground(Panel.TEXT);
        panel.add(header);

        // Grid bag layout
        JPanel ct = new JPanel(new GridBagLayout());
        ct.setBorder(new EmptyBorder(100, 0, 0, 0));
        ct.setBackground(Panel.PANEL_BG);

        GridBagConstraints gbc = new GridBagConstraints(); // Init GBC
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Path file
        JLabel pathFile = new JLabel("Path File");
        TextFieldUI pathField = new TextFieldUI("", Panel.FIELD, Panel.FIELDTEXT);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pathFile.setFont(Utils.spaceGrotesk.deriveFont(15f));
        pathFile.setForeground(Panel.TEXT);
        ct.add(pathFile, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
        ct.add(pathField, gbc);

        // Backup path file
        JLabel backupPath = new JLabel("Backup Path File");
        TextFieldUI backupPathField = new TextFieldUI("", Panel.FIELD, Panel.FIELDTEXT);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        backupPath.setFont(Utils.spaceGrotesk.deriveFont(15f));
        backupPath.setForeground(Panel.TEXT);
        ct.add(backupPath, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1;
        ct.add(backupPathField, gbc);

        panel.add(ct);
        return panel;
    }
}
