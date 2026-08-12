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
    private final List<Utils.Entry> reused = new ArrayList<>();

    public EvaluationPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Panel.PANEL_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        checkReused(); // Check for re-used passkeys

        // Header
        JLabel header = new JLabel("Evaluation");
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 32f));
        header.setForeground(Panel.TEXT);
        header.setAlignmentX(CENTER_ALIGNMENT);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Status card
        RoundedPanel statusCard = new RoundedPanel(10);
        statusCard.setLayout(new BoxLayout(statusCard, BoxLayout.Y_AXIS));
        statusCard.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusCard.setBackground(Panel.PANEL_BG.brighter());

        JLabel cardH = new JLabel("Key status");
        cardH.setFont(Utils.spaceGrotesk.deriveFont(24f));
        cardH.setForeground(Panel.TEXT);
        cardH.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusCard.add(cardH);

        JLabel status = new JLabel(warningTxt);
        status.setBorder(new EmptyBorder(10, 0, 0, 0));
        status.setFont(Utils.spaceGrotesk.deriveFont(18f));
        status.setForeground(Panel.TEXT);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusCard.add(status);

        statusCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, statusCard.getPreferredSize().height));

        // List card
        RoundedPanel listPanel = new RoundedPanel(10);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        listPanel.setBackground(Panel.PANEL_BG.brighter());

        for (Utils.Entry entry : reused) {
             JLabel title = new JLabel(entry.title());
             title.setFont(Utils.spaceGrotesk.deriveFont(18f));
             title.setForeground(Panel.TEXT);
             listPanel.add(title);
        }

        listPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, listPanel.getPreferredSize().height));

        add(header);
        add(statusCard);
        add(Box.createVerticalStrut(10));
        if (reused.size() > 2) add(listPanel);
        else remove(listPanel);
    }

    private void checkReused() {
        for (Utils.Entry entry : Utils.allEntries) {
            for (Utils.Entry entry1 : Utils.allEntries) {
                if (entry.password().equals(entry1.password()) && !entry.title().equals(entry1.title()))
                    if (!reused.contains(entry)) reused.add(new Utils.Entry(entry.title(), entry.password()));
            }
        }

        if (reused.size() > 2) warningTxt = reused.size() + " entries have re-used the same passwords!";
        else warningTxt = "Fine";
    }
}
