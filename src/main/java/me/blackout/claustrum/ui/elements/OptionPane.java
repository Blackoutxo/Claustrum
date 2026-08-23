package me.blackout.claustrum.ui.elements;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

public class OptionPane {
    public OptionPane() {}

    /**
     * Message Pane disclosing only information to user with button 'OK'
     * */
    public static void showMessage(Component parent, String title, String message) {
        JDialog dialog = buildBaseDialog(parent, title);

        JPanel contents = buildPanel();
        contents.add(bodyLabel(message));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonBar.setOpaque(false);
        RoundedButton ok = new RoundedButton("OK", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        ok.addActionListener(e -> dialog.dispose());
        buttonBar.add(ok);

        assemble(dialog, contents, buttonBar);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.setVisible(true);
    }

    /**
     * Confirm pane with 2 choice buttons
     * */
    public static boolean showConfirm(Component parent, String title, String message) {
        JDialog dialog = buildBaseDialog(parent, title);
        boolean[] result = {false};

        JPanel contents = buildPanel();
        contents.add(bodyLabel(message));

        RoundedButton confirm = new RoundedButton("Confirm", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        confirm.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        RoundedButton cancel = new RoundedButton("Cancel", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        cancel.addActionListener(e -> dialog.dispose());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonBar.setBackground(Panel.PANEL_BG);
        buttonBar.add(confirm);
        buttonBar.add(cancel);

        assemble(dialog, contents, buttonBar);
        dialog.getRootPane().setDefaultButton(confirm);
        dialog.setVisible(true);

        return result[0];
    }



    /**
     * Builds & Assemblies
     * */
    private static JLabel bodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Utils.spaceGrotesk.deriveFont(14f));
        label.setForeground(Panel.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JDialog buildBaseDialog(Component parent, String title) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner instanceof Frame ? (Frame) owner : null, title, true);
        dialog.setUndecorated(true);
        dialog.setResizable(false);
        return dialog;
    }

    private static JPanel buildPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(4, 0, 16, 0));
        return panel;
    }

    private static void assemble(JDialog dialog, JPanel panel, JPanel buttonBar) {
        RoundedPanel card = new RoundedPanel(20);
        card.setOpaque(false);
        card.setBackground(Panel.PANEL_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 20, 24));

        // Header of the pane
        JLabel header = new JLabel(dialog.getTitle());
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 20f));
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        header.setForeground(Panel.TEXT);

        card.add(header, BorderLayout.NORTH);
        card.add(panel, BorderLayout.CENTER);
        card.add(buttonBar, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Panel.PANEL_BG);
        wrapper.setBorder(new EmptyBorder(2, 2, 2, 2));
        wrapper.add(card, BorderLayout.CENTER);

        dialog.setContentPane(wrapper);
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 360), dialog.getHeight());
        dialog.setLocationRelativeTo(dialog.getOwner());

        // Close dialog on 'Esc' keyboard action
        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
}
