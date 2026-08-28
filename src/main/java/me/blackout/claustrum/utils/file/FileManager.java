package me.blackout.claustrum.utils.file;

import me.blackout.claustrum.Claustrum;
import me.blackout.claustrum.ui.panels.SettingsPanel;
import me.blackout.claustrum.utils.Utils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.*;
import java.time.LocalDate;
import java.util.*;

public class FileManager {
    public SecureRandom secRandom = new SecureRandom();
    public Key key;

    public static String userPath =  "";
    public static String CLAUSTRUM_CONFIG = "Claustrum\\config.dat";

    public static String BACKUP_PATH = "";
    public static String KEY_PATH = "";
    public static String SALT_PATH = "";

    public static String KEY_FILE = "ClaustrumKey.dat";
    public static String SALT_FILE = "ClaustrumSalt.dat";
    public static String FAVOURITE_FILE = "FavouriteCLST.dat";

    /**
     * Create File
     * */
    public void create() throws IOException{
        nullPath();

        File file = new File(KEY_FILE);
        File saltyFile = new File(SALT_FILE);
        File favouriteFile = new File(FAVOURITE_FILE);
        File config = new File(CLAUSTRUM_CONFIG);

        createDirectory(); // Create directory

        // Check for existing file
        if (!file.exists()) file.createNewFile();
        if (!saltyFile.exists()) saltyFile.createNewFile();
        if (!favouriteFile.exists()) favouriteFile.createNewFile();
        if (!config.exists()) config.createNewFile();

//        // Create file
//        file.createNewFile(); // Key file
//        saltyFile.createNewFile(); // Salt file
//        favouriteFile.createNewFile(); // Favourite file
//        config.createNewFile(); // Config file
    }

    public static void nullPath() {
        userPath = Utils.getConfigValue("File Path Location", System.getProperty("user.home") + File.separator);
        KEY_PATH = Utils.getConfigValue("File Path Location", userPath + "Claustrum");
        SALT_PATH = Utils.getConfigValue("File Path Location", userPath + "Claustrum");
        BACKUP_PATH = Utils.getConfigValue("Backup Location", userPath + "CLSTBackup");
        CLAUSTRUM_CONFIG = userPath + CLAUSTRUM_CONFIG;

        // Guard against double-prepending if nullPath() ever runs twice
        if (!KEY_FILE.contains(File.separator)) KEY_FILE = KEY_PATH + File.separator + KEY_FILE;
        if (!SALT_FILE.contains(File.separator)) SALT_FILE = SALT_PATH + File.separator + SALT_FILE;
        if (!FAVOURITE_FILE.contains(File.separator)) FAVOURITE_FILE = KEY_PATH + File.separator + FAVOURITE_FILE;

        // Append default pathway to settings for displaying
        SettingsPanel.KpathField.setText(KEY_PATH);
    }

    public void createDirectory() {
        File file = new File(userPath + File.separator + "Claustrum");
        if (!file.exists()) file.mkdir();
    }

    /**
     * Read file
     */
    public String read(String file) {
        StringBuilder lines = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                lines.append(line).append(System.lineSeparator());
            }
        } catch (IOException ignored) {}

        return lines.toString();
    }

    /**
     * Load the Config & File
     */
    public void load(String file) throws IOException, GeneralSecurityException {
        key = Utils.generateKey(Claustrum.masterKey);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 3) continue; // Skip malformed parts

                // Decrypt title & password
                String title = decryptField(parts[0], key);
                String password = decryptField(parts[1], key);

                // Check for tags
                List<String> tag = new ArrayList<>();
                if (parts.length == 3) {
                    String tagsJoined = decryptField(parts[2], key);
                    if (!tagsJoined.isBlank()) {
                        tag = new ArrayList<>(Arrays.asList(tagsJoined.split(",")));
                    }
                }

                // Add to entry
                Utils.allEntries.add(new Utils.Entry(title, password, tag));
                loadFavourite();
            }
        }
    }

    private void loadFavourite() throws GeneralSecurityException, IOException {
        key = Utils.generateKey(Claustrum.masterKey);
        try (BufferedReader reader = new BufferedReader(new FileReader(FAVOURITE_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String decrypted = decryptField(line, key);
                Utils.favourites.add(decrypted);
            }
        }
    }

    /**
     *  Writing  & Saving
     */
    public void save(String title, String passkey, boolean append) throws GeneralSecurityException, IOException {
        // Set key
        key = Utils.generateKey(Claustrum.masterKey);

        // String into bytes
        String encryptedTitle = encryptField(title, key);
        String encryptedPassword = encryptField(passkey, key);

        String line = encryptedTitle + "|" + encryptedPassword;

        // Write the input into the save file
        try (FileWriter writer = new FileWriter(KEY_FILE, append)) { // Made ts to append (I kept overwriting the files as it wasn't append)......Bravo!

            writer.write(line);
            writer.write(System.lineSeparator());
        }
    }

    public void save(String title, String state, String path, boolean append, boolean encrypt) throws GeneralSecurityException, IOException {
        String line;

        if (encrypt) {
            key = Utils.generateKey(Claustrum.masterKey);

            // String into bytes
            String encryptedTitle = encryptField(title, key);
            String encryptedPassword = encryptField(state, key);

            line = encryptedTitle + "|" + encryptedPassword;
        } else {
            line = title + "|" + state;
        }

        // Write the input into the save file
        try (FileWriter writer = new FileWriter(path, append)) {
            writer.write(line);
            writer.write(System.lineSeparator());
        }
    }

    public void saveEntries() throws IOException, GeneralSecurityException {
        StringBuilder line = new StringBuilder();

        // Set key
        key = Utils.generateKey(Claustrum.masterKey);

        // Normal entries
        for (Utils.Entry entry : Utils.allEntries) {

            String title = encryptField(entry.title(), key);
            String password = encryptField(entry.password(), key);
            String joinTags = String.join(",", entry.tag());
            String tags = encryptField(joinTags, key);

            line.append(title).append("|").append(password).append("|").append(tags).append(System.lineSeparator());
        }

        try (FileWriter writer = new FileWriter(KEY_FILE, false)) {
            writer.write(line.toString());
        }
    }

    public void saveFavourite() throws GeneralSecurityException, IOException {
        key = Utils.generateKey(Claustrum.masterKey);

        try (FileWriter writer = new FileWriter(FAVOURITE_FILE, false)) {
            for (String title : Utils.favourites) {
                writer.write(encryptField(title, key));
                writer.write(title);
                writer.write(System.lineSeparator());
            }
        }
    }

    public void backup() throws IOException {
        String backupPath = Utils.getConfigValue("Backup Location", userPath +  "Claustrum") + File.separator + "CLSTBackup" + File.separator;

        File backupDir = new File(backupPath);
        if (!backupDir.exists()) backupDir.mkdirs();

        // pathway
        Path keySource = Path.of(KEY_FILE);
        Path saltSource = Path.of(SALT_FILE);
        Path favouriteSource = Path.of(FAVOURITE_FILE);
        Path configSource = Path.of(CLAUSTRUM_CONFIG);

        // Copy files to their backup path
        Files.copy(keySource, Path.of(backupPath, keySource.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(saltSource, Path.of(backupPath, saltSource.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(favouriteSource, Path.of(backupPath, favouriteSource.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(configSource, Path.of(backupPath, configSource.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);

        // Add or Check for existing last backup date
        Optional<Utils.Config> option = Utils.findTitleConfig("Last Backup");

        if (option.isEmpty()) Utils.config.add(new Utils.Config("Last Backup", LocalDate.now().toString()));

        if (option.isPresent()) {
            Utils.config.remove(option.get());
            Utils.config.add(new Utils.Config("Last Backup", LocalDate.now().toString()));
            Utils.saveConfig();
        }
    }

    public void write(byte[] input, String file) throws IOException {
        try (FileOutputStream IStream = new FileOutputStream(file, true)) {
            IStream.write(input);
        }
    }

    /**
     * Encryption & Decryption
     */
    public String encryptField(String token, Key key) throws GeneralSecurityException {
        byte[] iv = new byte[12];
        secRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherBytes = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + cipherBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherBytes, 0, combined, iv.length, cipherBytes.length);
        
        return Base64.getMimeEncoder().encodeToString(combined);
    }

    public String decryptField(String token, Key key) throws GeneralSecurityException {
        byte[] combined = Base64.getMimeDecoder().decode(token);

        byte[] iv = new byte[12];
        byte[] cipherBytes = new byte[combined.length - 12];
        System.arraycopy(combined, 0, iv, 0, 12);
        System.arraycopy(combined, 12, cipherBytes, 0, cipherBytes.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }
}
