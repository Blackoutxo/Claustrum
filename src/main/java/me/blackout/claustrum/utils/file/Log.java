package me.blackout.claustrum.utils.file;

import me.blackout.claustrum.ui.panels.SettingsPanel;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

public class Log {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final LocalTime now = LocalTime.now();

    public String fileName = LocalDate.now().atTime(LocalTime.parse(now.format(formatter))).toString();
    public List<String> logs = new ArrayList<>();

    public void writeLogs(String data) {
        if (SettingsPanel.saveLogs == 0) return;

        StringBuilder sb = new StringBuilder();

        // Append Time, Data and separate from original line
        sb.append("[").append(now).append("] ");
        sb.append(data);
        sb.append(System.lineSeparator());

        logs.add(sb.toString());
    }

    public void saveLogs() {
        File logs = new File(FileManager.CLAUSTRUM_LOG);

    }
}
