package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.Claustrum;
import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.*;
import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.file.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.function.Consumer;

public class SettingsPanel extends JPanel {
    public static final JTextField bpathfield = new JTextField();
    public static final JTextField KpathField = new JTextField();

    public int autoBackUp = 0;
    public boolean backupClean = false;
    private static String input = "";

    private FileManager file = new FileManager();

    public final boolean duress = false;
    public int clipboardCT = 0;

    public void loadState() {
        Utils.darkTheme = Utils.getConfigValue("Theme", "Dark").equals("Dark");
        autoBackUp = Utils.getConfigValue("Auto Backup", "Off").equals("Off") ? 0
                : Utils.getConfigValue("Auto Backup", "Off").equals("Daily") ? 2 : 1;
        backupClean = Utils.getConfigValue("Backup Cleanup", "Off").equals("On");
        //duress = Utils.getConfigValue("Enable Duress", "On").equals("On");
        clipboardCT = Integer.parseInt(Utils.getConfigValue("Clipboard clear time", "2"));
    }

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(Panel.PANEL_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        try { Utils.registerFont(); } catch (IOException | FontFormatException ignored) {}

        JLabel header = new JLabel("Settings");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 30f));
        header.setForeground(Panel.TEXT);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Settings list
        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));

        // Theme selector
        settings.add(radioSettingItem("Theme",
                new String[]{"Light", "Dark"},
                selected -> {
                    Utils.darkTheme = selected.equals("Dark");
                    Utils.switchTheme(Utils.darkTheme);
        }));

        // File Path location
        settings.add(pathFieldSetting("File Path Location", KpathField, () -> browse(KpathField)));

        // Clip board clear time
        settings.add(fieldSetting("Clipboard clear time", String.valueOf(clipboardCT), clipboardCT));
        /**TODO: POSSIBLE ADDITION OF DURESS MODE BUT SEEMS TO GO IN VAIN*/

        // AUTO BACK UP
        settings.add(radioSettingItem("Auto Backup",
                new String[]{"Off", "On unlock", "Daily"},
                selected -> {}));

        // BACKUP LOCATION
        settings.add(pathFieldSetting("Backup Location", bpathfield, () -> browse(bpathfield)));

        // Change master key
        settings.add(buttonSettingItem("Master Key", "Change",
                () -> {
                   input = OptionPane.showMaskedInput(null, "Enter master key", "");

                   // Check current masterkey entry
                   if (!input.equals(Claustrum.masterKey)) {
                       System.exit(0);
                       return;
                   }

                   // Open new dialog to set masterkey
                   Claustrum.masterKey = OptionPane.showMaskedInput(null, "Set new master key", "");
                   OptionPane.showMessage(null, "Password Changed", "Your old master key has now been changed");

                   save();
                   Utils.saveConfig();
               }
        ));

        settings.setOpaque(false);

        // Impl variables
        loadState();

        // Set custom scroll UI
        JScrollPane scroll = new JScrollPane(settings);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Panel.PANEL_BG);
        scroll.setOpaque(false);

        scroll.getVerticalScrollBar().setUI(new ScrollBar(Panel.ScrollThumb, Panel.ThumbHover));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setPreferredSize(new Dimension(12, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    // Settings Item
    private JPanel pathFieldSetting(String title, JTextField pathField, Runnable onBrowse) {
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

    private JPanel fieldSetting(String title, String value, int assign) {
        JTextField textField = new JTextField(value);
        Utils.findTitleConfig(title).ifPresent(cfg -> textField.setText(cfg.state()));
        assign = Integer.parseInt(textField.getText());

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

        textField.addActionListener(e -> {
            // Add config
            Optional<Utils.Config> repeat = Utils.findTitleConfig(title);
            repeat.ifPresent(config -> Utils.config.remove(config));
            Utils.config.add(new Utils.Config(title, textField.getText()));
            Utils.saveConfig();
        });

        // Config text field
        textField.setEditable(true);
        textField.setForeground(Panel.FIELDTEXT);
        textField.setBackground(Panel.FIELD);
        textField.setBorder(new EmptyBorder(6, 10, 6, 10));

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.add(textField, BorderLayout.CENTER);
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

                if (titlePresent.isPresent()) return;

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

    private JPanel buttonSettingItem(String title, String buttonTitle, Runnable onClick) {
        RoundedButton button = new RoundedButton(buttonTitle, Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);

        // Panel
        RoundedPanel item = new RoundedPanel(0);
        item.setLayout(new BorderLayout(0, 8));
        item.setBackground(Panel.PANEL_BG);
        item.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Set title
        JLabel label = new JLabel(title);
        label.setFont(Utils.spaceGrotesk.deriveFont(15f));
        label.setBorder(new EmptyBorder(0, 0, 0,10));
        label.setForeground(Panel.TEXT);
        item.add(label, BorderLayout.WEST);

        // Add button to the row
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
       // row.setSize(new Dimension(20, getHeight()));
        row.add(button, BorderLayout.CENTER);
        item.add(row, BorderLayout.CENTER);

        // Run the runnable ere
        button.addActionListener(e -> {
            onClick.run();
        });

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 0, 10, 0));
        outer.add(item, BorderLayout.CENTER);
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.setMaximumSize(new Dimension(outer.getPreferredSize().width, outer.getPreferredSize().height));

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

    // Save for new encryption without all the catch block hassle
    private void save() {
        try {   file.saveEntries();
        } catch (IOException | GeneralSecurityException ignored) {}
    }
}
