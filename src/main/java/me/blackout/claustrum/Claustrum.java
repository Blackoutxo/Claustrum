package me.blackout.claustrum;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.OptionPane;
import me.blackout.claustrum.ui.panels.SettingsPanel;
import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.file.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;


public class Claustrum {
    public static String input, masterKey;
    public static byte[] salt;

    private static Panel panel;
    private final FileManager file = new FileManager();

    public void run() throws IOException, GeneralSecurityException, FontFormatException {

        // Register font
        Utils.registerFont();

        // Create file
        file.create();

        // Load config
        Utils.loadConfig();

        // Input Box
        String message = file.read(FileManager.KEY_FILE).isEmpty() ? "Set master key" : "Enter master key";

        input = OptionPane.showPassInput(null, message, "");

        // Generate salt once
        if (file.read(FileManager.SALT_FILE).isEmpty()) {
            salt = Utils.generateSalt();

            // Save the seasoning
            file.write(salt, FileManager.SALT_FILE);
        }

        // Set the master key
        masterKey = input;

        // Load file
        file.load(FileManager.KEY_FILE);

        // Create a settings instance
        SettingsPanel settings = new SettingsPanel();

        // Load settings state
        settings.loadState();

        // Start to clean clipboard
        new Utils().clear();

        // Backup Daily
        if (settings.autoBackUp == 2) {
            if (!Utils.getConfigValue("Last Backup", LocalDate.now().toString()).equals(LocalDate.now().toString())
                || Utils.getConfigValue("Last Backup", "").isEmpty())
                file.backup();
        }

        // Backup On unlock
        if (settings.autoBackUp == 1) {
            //if (Files.size(Path.of(FileManager.BACKUP_PATH)) > Files.size(Path.of(FileManager.KEY_FILE))) return;
            file.backup();
        }

        // Build the initial panel
        build();
    }

    public static void build() {
        if (panel != null) panel.dispose();

        // Init panel
        try { panel = new Panel(); } catch (IOException | FontFormatException ignored) {}
        panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set Icon for application
        try { Utils.setIcon(panel);  } catch (IOException ignored) {}

        // Assemble components and display window
        panel.pack();
        panel.setLocationRelativeTo(null);
        panel.setResizable(true);
        panel.setVisible(true);
    }
}
