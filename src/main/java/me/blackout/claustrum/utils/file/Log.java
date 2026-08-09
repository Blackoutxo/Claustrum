package me.blackout.claustrum.utils.file;

import me.blackout.claustrum.ui.panels.SettingsPanel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Log {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final LocalTime now = LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

    public String fileName = LocalDate.now().atTime(LocalTime.parse(now.format(formatter))) + ".txt";
    public static List<String> logs = new ArrayList<>();

    public static void writeLogs(String data) {
        if (SettingsPanel.saveLogs == 0) return;

        LocalTime lt = LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // Add Time & Data to logs
        logs.add("[" + lt + "] " + data);
    }

    public void saveLogs() {
        if (SettingsPanel.saveLogs == 0) return; // Stop if save logs is off

        // File & Folders
        File logFolder = new File(FileManager.CLAUSTRUM_LOG);
        File logFile = new File(FileManager.CLAUSTRUM_LOG + File.separator + fileName);
        if (!logFolder.exists()) logFolder.mkdir();
        try {if (!logFile.exists()) logFile.createNewFile(); } catch (IOException ignored) {}

        StringBuilder strB = new StringBuilder();
        for (String log : logs) {
            strB.append(log);
            strB.append(System.lineSeparator());
        }

        // Write in file
        try (FileWriter writer = new FileWriter(logFile, false)) {
            writer.write(strB.toString());
        } catch (IOException ignored) {}
    }
}
