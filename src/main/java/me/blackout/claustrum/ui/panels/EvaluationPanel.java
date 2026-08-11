package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.RoundedPanel;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationPanel extends JPanel {
    private String warningTxt = "";
    private List<Utils.Entry> reused = new ArrayList<>();

    public EvaluationPanel() {
        setLayout(new BorderLayout());
        setBackground(Panel.PANEL_BG);

        checkReused(); // Check for re-used passkeys

        JLabel header = new JLabel("Favourites");
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 32f));
        header.setForeground(Panel.TEXT);
        header.setBackground(Panel.PANEL_BG);
        header.setAlignmentX(CENTER_ALIGNMENT);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        add(header, BorderLayout.CENTER);
    }

    private void checkReused() {
        for (Utils.Entry entry : Utils.allEntries) {
            for (Utils.Entry entry1 : Utils.allEntries) {
                if (entry.password().equals(entry1.password())) {
                    reused.add(new Utils.Entry(entry.title(), entry.password()));
                    reused.add(new Utils.Entry(entry1.title(), entry.password()));
                }
            }
        }

        if (reused.size() > 2) warningTxt = reused.size() + " entries have re-used passwords, please change them ASAP!";
    }
}
