package me.blackout.claustrum.utils;

import me.blackout.claustrum.Main;
import me.blackout.claustrum.utils.file.FileManager;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Utils {
    public static List<Entry> allEntries = new ArrayList<>();
    public static List<Config> config = new ArrayList<>();

    private static final FileManager file = new FileManager();
    public static Font spaceGrotesk;

    // Register font
    public static void registerFont() throws IOException, FontFormatException {
        InputStream is = Utils.class.getResourceAsStream("/fonts/SpaceGrotesk/static/SpaceGrotesk-Regular.ttf");
        spaceGrotesk = Font.createFont(Font.TRUETYPE_FONT, is);
    }

    // Save config
    public static void saveConfig() {
        for (Config cfg : config) {
            System.out.println(cfg);
            try {
                file.save(cfg.setting, cfg.state, FileManager.CLAUSTRUM_CONFIG, false, false);   } catch (GeneralSecurityException | IOException e) {throw new RuntimeException(e);
            }
        }
    }

    // Icon
    public static void setIcon(JFrame frame) throws IOException {
        URL iconURL = Main.class.getResource("/Claustrum.png");
        if (iconURL == null) return;

        BufferedImage original = ImageIO.read(iconURL);

        List<Image> icons = new ArrayList<>();
        int[] sizes = {16, 20, 24, 32, 40, 48, 64, 128, 256};
        for (int size : sizes) {
            icons.add(original.getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }

        frame.setIconImages(icons);
    }

    // Find by title
    public static Optional<Entry> findByTitle(String title) {
        return allEntries.stream()
                .filter(entry -> entry.title().equalsIgnoreCase(title))
                .findFirst();
    }

    public static Optional<Config> findTitleConfig(String title) {
        return config.stream()
                .filter(config -> config.setting().equalsIgnoreCase(title))
                .findFirst();
    }

    // Key generation
    public static Key generateKey(String masterKey) throws GeneralSecurityException, IOException {
        // Read file
        byte[] salt = Files.readAllBytes(Path.of(file.SALT_FILE));

        // Set-up key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(masterKey.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);

        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    // Salt *Gotta make it salty
    public static byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);

        return salt;
    }

    // Record
    public record Entry(String title, String password) {
    }

    public record Config(String setting, String state) {
    }
}
