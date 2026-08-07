package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.*;
import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.file.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.function.Consumer;

public class SettingsPanel extends JPanel {
    public static final JTextField bpathfield = new JTextField();
    public static final JTextField KpathField = new JTextField();

    private final FileManager file = new FileManager();

    public static String autoBackupState;

    public JPanel settings() {
        // Load config
        Utils.loadConfig();

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
        settings.add(fieldSetting("File Path Location", KpathField, () -> browse(KpathField)));
        settings.add(radioSettingItem("Auto-Backup", // AUTO BACK UP
                new String[]{"Off", "On unlock", "Daily"},
                selected -> {
                        autoBackupState = selected;
                        System.out.println("Auto-backup mode changed to: " + selected);
        }));
        settings.add(fieldSetting("Backup Location", bpathfield, () -> browse(bpathfield))); // BACKUP LOCATION

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
        // Load from config
        Utils.findTitleConfig(title).ifPresent(cfg -> pathField.setText(cfg.state()));

        // Panel
        RoundedPanel item = new RoundedPanel(0);
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
        btn.addActionListener(e -> {
            onBrowse.run();

            Optional<Utils.Config> option = Utils.findTitleConfig(title);

            option.ifPresent(Utils.config::remove);

            Utils.config.add(new Utils.Config(title, pathField.getText()));
            Utils.saveConfig();
        });

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

    private JPanel radioSettingItem(String title, String[] options, Consumer<String> onSelectionChanged) {
        // Load from config
        String savedValue = Utils.findTitleConfig(title)
                .map(Utils.Config::state)
                .orElse(options[0]);

        RoundedPanel item = new RoundedPanel(16);
        item.setLayout(new BorderLayout(0, 8));
        item.setBackground(Panel.PANEL_BG);
        item.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 14f));
        titleLabel.setForeground(Panel.TEXT);
        item.add(titleLabel, BorderLayout.NORTH);

        JPanel optionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        optionsRow.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        for (String option : options) {
            JRadioButton radio = new RadioButton(option);
            radio.setOpaque(false);
            radio.setForeground(Panel.TEXT);
            radio.setFont(Utils.spaceGrotesk.deriveFont(13f));
            if (option.equalsIgnoreCase(savedValue)) radio.setSelected(true);

            radio.addActionListener(e -> {
                onSelectionChanged.accept(option);

                Optional<Utils.Config> titlePresent = Utils.findTitleConfig(title);
                titlePresent.ifPresent(Utils.config::remove);

                Utils.config.add(new Utils.Config(title, option));
                Utils.saveConfig();
            });

            group.add(radio);
            optionsRow.add(radio);
        }
        item.add(optionsRow, BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 0, 10, 0));
        outer.add(item, BorderLayout.CENTER);
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, outer.getPreferredSize().height));
        return outer;
    }

    private void browse(JTextField text) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose backup location");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            text.setText(path);
        }
    }
}
