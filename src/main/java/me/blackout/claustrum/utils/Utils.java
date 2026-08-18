package me.blackout.claustrum.utils;

import me.blackout.claustrum.Claustrum;
import me.blackout.claustrum.Main;
import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.panels.SettingsPanel;
import me.blackout.claustrum.utils.file.FileManager;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.*;
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
    public static List<String> favourites = new ArrayList<>();
    public static List<Config> config = new ArrayList<>();
    public static List<String> passkeys = new ArrayList<>();

    private final SettingsPanel settings = new SettingsPanel();
    private final int timeoutMillis = settings.clipboardCT * 60 * 1000;

    public static Font spaceGrotesk;

    // Systemic utils
    public void clear() {
        Timer timer = new Timer(timeoutMillis, e -> clearClipBoard());
        timer.start();
    }

    public static void clearClipBoard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection emptySelection = new StringSelection("");
        clipboard.setContents(emptySelection, null);
    }

    public static void switchTheme(boolean darkTheme) {
        if (!darkTheme) Panel.applyLightTheme(); else Panel.applyDarkTheme();
        Claustrum.build();
    }

    // Register font
    public static void registerFont() throws IOException, FontFormatException {
        InputStream inputStream = Utils.class.getResourceAsStream("/fonts/SpaceGrotesk/static/SpaceGrotesk-Regular.ttf");
        spaceGrotesk = Font.createFont(Font.TRUETYPE_FONT, inputStream);
    }

    // Save config
    public static void saveConfig() {
        StringBuilder sb = new StringBuilder();

        for (Config cfg : config) {
            String encryptedSetting = cfg.setting;
            String encryptedState = cfg.state;
            sb.append(encryptedSetting).append("|").append(encryptedState).append(System.lineSeparator());
        }

        try (FileWriter writer = new FileWriter(FileManager.CLAUSTRUM_CONFIG, false)) {
            writer.write(sb.toString());
        } catch (IOException ignored) {}
    }

    // Config
    public static void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FileManager.CLAUSTRUM_CONFIG))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() && !Utils.config.isEmpty()) continue;

                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) continue; // Skip malformed parts

                // Decrypt title & password
                String setting = parts[0];
                String state = parts[1];

                // Add to entry
                Utils.config.add(new Utils.Config(setting, state));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getConfigValue(String setting, String defaultValue) {
        return findTitleConfig(setting).map(Config::state).orElse(defaultValue);
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
        byte[] salt = Files.readAllBytes(Path.of(FileManager.SALT_FILE));

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
    public record Entry(String title, String password, List<String> tag) {
        public Entry(String title, String password) {
            this(title, password, new ArrayList<>());
        }
    }

    public record Config(String setting, String state) { }
}
