package me.blackout.claustrum;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.PasswordField;
import me.blackout.claustrum.ui.panels.SettingsPanel;
import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.file.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class Claustrum {
    public static String input, masterKey;
    public static byte[] salt;

    private final FileManager file = new FileManager();

    private final Color TEXT = new Color(56, 30, 114);
    private final Color FIELD = new Color(202, 196, 208);

    public void run() throws IOException, GeneralSecurityException, FontFormatException {

        // Register font
        Utils.registerFont();

        // Password field
        JPasswordField passwordField = new PasswordField(TEXT, FIELD, TEXT);

        // Create file
        file.create();

        // Input Box
        String message = file.read(FileManager.KEY_FILE).isEmpty() ? "Set master key" : "Enter master key";

        int result = JOptionPane.showConfirmDialog(
                null, passwordField, message,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) System.exit(0); // Exit on empty

        input = new String(passwordField.getPassword());

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

        // Load config
        Utils.loadConfig();

        // Backup On unlock
        if (SettingsPanel.autoBackUp == 1) file.backup();

        // Init panel here
        Panel panel = new Panel();
        panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set Icon for application
        Utils.setIcon(panel);

        // Assemble components and display window
        panel.pack();
        panel.setLocationRelativeTo(null);
        panel.setResizable(true);
        panel.setVisible(true);
    }
}
