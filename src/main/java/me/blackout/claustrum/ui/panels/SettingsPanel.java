package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.RoundedButton;
import me.blackout.claustrum.ui.elements.RoundedPanel;
import me.blackout.claustrum.ui.elements.ScrollBar;
import me.blackout.claustrum.ui.elements.TextFieldUI;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JTextField bpathfield = new JTextField();

    public JPanel settings() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Panel.PANEL_BG);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel header = new JLabel("Settings");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 30f));
        header.setForeground(Panel.TEXT);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);

        // Settings list
        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
        settings.add(fieldSetting("Backup Location", bpathfield, this::browse));
        settings.setOpaque(false);

        // Set custom scroll UI
        JScrollPane scroll = new JScrollPane(settings);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Panel.PANEL_BG);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUI(new ScrollBar(Panel.ScrollThumb, Panel.ScrollThumb.darker()));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // Settings Item
    private JPanel fieldSetting(String title, JTextField pathField, Runnable onBrowse) {
        RoundedPanel item = new RoundedPanel(0); // Panel of item
        item.setLayout(new BorderLayout(0, 8));
        item.setBackground(Panel.PANEL_BG);
        item.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Set title
        JLabel label = new JLabel(title);
        label.setFont(Utils.spaceGrotesk.deriveFont(15f));
        label.setForeground(Panel.TEXT);
        item.add(label, BorderLayout.NORTH);

        // Config text field
        pathField.setEditable(false);
        pathField.setForeground(Panel.FIELDTEXT);
        pathField.setBackground(Panel.FIELD);
        pathField.setBorder(new EmptyBorder(6, 10, 6, 10));

        // Button
        RoundedButton btn = new RoundedButton("Browse", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.BUTTON_TEXT);
        btn.addActionListener(e -> onBrowse.run());

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.add(pathField, BorderLayout.CENTER);
        row.add(btn, BorderLayout.EAST);
        item.add(row, BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 0, 10, 0));
        outer.add(item, BorderLayout.CENTER);
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, outer.getPreferredSize().height));

        return outer;
    }

    private void browse() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose backup location");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            bpathfield.setText(path);
            /**TODO: ADD BACKUP PATH SAVE*/
        }
    }
}
