package me.blackout.claustrum;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.PasswordField;
import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.file.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Claustrum {
    public static String input, masterKey;
    public static byte[] salt;

    private Color TEXT = new Color(56, 30, 114);
    private Color FIELD = new Color(202, 196, 208);
    private Color BACKGROUND = new Color(28, 27, 31);

    public void run() throws IOException, GeneralSecurityException, FontFormatException {
        // Open file manager
        FileManager file = new FileManager();

        // Register font
        Utils.registerFont();

        // Password field
        JPasswordField passwordField = new PasswordField(TEXT, FIELD, TEXT);

        // Create file
        file.create();

        // Input Box
        String message = file.read(file.KEY_FILE).isEmpty() ? "Set master key" : "Enter master key";

        int result = JOptionPane.showConfirmDialog(
                null, passwordField, message,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) System.exit(0); // Exit on empty

        input = new String(passwordField.getPassword());

        // Generate salt once
        if (file.read(file.SALT_FILE).isEmpty()) {
            salt = Utils.generateSalt();

            // Save the seasoning
            file.write(salt, file.SALT_FILE);
        }

        // Set the master key
        masterKey = input;

        // Load file
        file.load(file.KEY_FILE);

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
