package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.RoundedPanel;
import me.blackout.claustrum.ui.elements.ScrollBar;
import me.blackout.claustrum.ui.elements.TextFieldUI;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ScrollBarUI;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public JPanel settings() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Panel.PANEL_BG);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel header = new JLabel("Settings");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 30f));
        header.setForeground(Panel.TEXT);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);

        // Settings list
        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
        settings.setOpaque(false);

        // Set custom scroll UI
        JScrollPane scroll = new JScrollPane(settings);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Panel.PANEL_BG);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUI(new ScrollBar(Panel.ScrollThumb, Panel.ScrollThumb.darker()));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // Settings Item
    private JPanel fieldSetting(String title, TextFieldUI pathField, Runnable onBrowse) {
        RoundedPanel item = new RoundedPanel(0); // Panel of item
        item.setLayout(new BorderLayout(0, 8));
        item.setBackground(Panel.PANEL_BG);
        item.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Set title
        JLabel label = new JLabel(title);
        label.setFont(Utils.spaceGrotesk.deriveFont(15f));
        label.setForeground(Panel.TEXT);
        item.add(label, BorderLayout.NORTH);

        // Config text field
        pathField.setEditable(false);
        pathField.setForeground(Panel.FIELDTEXT);
        pathField.setBackground(Panel.FIELD);
        pathField.setBorder(new EmptyBorder(6, 10, 6, 10));



        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 0, 10, 0));
        outer.add(item, BorderLayout.CENTER);
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, outer.getPreferredSize().height));

        return outer;
    }


}
