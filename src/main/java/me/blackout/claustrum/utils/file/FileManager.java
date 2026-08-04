package me.blackout.claustrum.utils.file;

import me.blackout.claustrum.Claustrum;
import me.blackout.claustrum.utils.Utils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public class FileManager {
    public String KEY_PATH = "";
    public String SALT_PATH = "";

    public String CLAUSTRUM_CONFIG = "C:\\Claustrum\\config.txt";

    public String KEY_FILE = KEY_PATH + "clstDat.txt";
    public String SALT_FILE =  SALT_PATH + "clst.txt";

    public List<String> favourite = new ArrayList<>();
    public Key key;

    public SecureRandom secRandom = new SecureRandom();

    /**
     * Create File
     * */
    public void create() throws IOException{
        File file = new File(KEY_FILE);
        File saltyFile = new File(SALT_FILE);
        File concifg = new File(CLAUSTRUM_CONFIG);

        createDirectory();
        nullPath();

        // Check for existing file
        if (file.exists() && saltyFile.exists() && concifg.exists()) return;

        // Create file
        concifg.createNewFile();
        file.createNewFile();
        saltyFile.createNewFile();
    }

    public void nullPath() {
        KEY_PATH = KEY_PATH.isEmpty() ? "C:\\Claustrum" + File.separator : KEY_PATH;
        SALT_PATH = SALT_PATH.isEmpty() ? "C:\\Claustrum" + File.separator : SALT_PATH;
    }

    public void createDirectory() {
        File file = new File("C:\\" + File.separator + "Claustrum");
        if (!file.exists()) file.mkdir();
    }

    /**
     * Read file
     */
    public String read(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) return line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Load the file
     */
    public void load(String file) throws IOException, GeneralSecurityException {
        key = Utils.generateKey(Claustrum.masterKey);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) continue; // Skip malformed parts

                // Decrypt title & password
                String title = decryptField(parts[0], key);
                String password = decryptField(parts[1], key);

                // Add to entry
                Utils.allEntries.add(new Utils.Entry(title, password));
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

    public void saveEntries() throws IOException, GeneralSecurityException {
        StringBuilder line = new StringBuilder();

        // Set key
        key = Utils.generateKey(Claustrum.masterKey);

        // Normal entries
        for (Utils.Entry entry : Utils.allEntries) {

            String encryptedTitle = encryptField(entry.title(), key);
            String encryptedPassword = encryptField(entry.password(), key);

            line.append(encryptedTitle).append("|").append(encryptedPassword).append(System.lineSeparator());
        }

        // Favourite entries
        line.append(encryptField("Favourite:[", key));
        for (String title : favourite) {
            String encryptedTitle = encryptField(title, key);

            line.append(encryptedTitle).append(System.lineSeparator());
        }
        line.append(encryptField("]", key));

        try (FileWriter writer = new FileWriter(KEY_FILE, false)) {
            writer.write(line.toString());
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
    private String encryptField(String token, Key key) throws GeneralSecurityException {
        byte[] iv = new byte[12];
        secRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherBytes = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + cipherBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherBytes, 0, combined, iv.length, cipherBytes.length);
        
        return Base64.getEncoder().encodeToString(combined);
    }

    private String decryptField(String token, Key key) throws GeneralSecurityException {
        byte[] combined = Base64.getDecoder().decode(token);

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
