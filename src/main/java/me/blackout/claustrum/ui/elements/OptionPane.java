package me.blackout.claustrum.ui.elements;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OptionPane extends JPanel {
    private final String title;

    public OptionPane(String title) {
        this.title = title;
    }

    public void showDialog() {
        JDialog dialog = new JDialog(new Frame(), title, true);
        JPanel form = new JPanel(new GridBagLayout());

        dialog.setTitle(title);
        dialog.setBackground(Panel.PANEL_BG);
        form.setBackground(Panel.PANEL_BG);

        // Layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        dialog.add(form, BorderLayout.CENTER);

        // Button
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        buttonBar.setBackground(Panel.PANEL_BG);
        buttonBar.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Cancel button
       RoundedButton button = new RoundedButton("", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);


        // Add to button bar

        dialog.add(buttonBar, BorderLayout.SOUTH);

        // Pack dialog box
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 380), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}
