package me.blackout.claustrum.ui.elements;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
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

        // Button bar and buttons
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

        // Button bar & Buttons
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonBar.setBackground(Panel.PANEL_BG);

        RoundedButton confirm = new RoundedButton("Confirm", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        confirm.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        RoundedButton cancel = new RoundedButton("Cancel", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        cancel.addActionListener(e -> dialog.dispose());

        // Add buttons
        buttonBar.add(confirm);
        buttonBar.add(cancel);

        // Finishing
        assemble(dialog, contents, buttonBar);
        dialog.getRootPane().setDefaultButton(confirm);
        dialog.setVisible(true);

        return result[0];
    }


    /**
     *
     * */
    public static String showInput(Component parent, String title, String message, String placeholder) {
        JDialog dialog = buildBaseDialog(parent, title);
        String[] result = {""};

        JPanel contents = buildPanel();
        contents.add(bodyLabel(message));

        TextFieldUI textField = new TextFieldUI(placeholder, Panel.FIELD, Panel.FIELDTEXT);
        textField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        contents.add(Box.createVerticalStrut(10));
        contents.add(textField);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonBar.setBackground(Panel.PANEL_BG);

        RoundedButton confirm = new RoundedButton("Ok", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        Runnable confirmAction = () -> {
          result[0] = textField.getText();
          dialog.dispose();
        };
        confirm.addActionListener(e -> confirmAction.run());

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed (KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_ENTER) confirmAction.run();
            }
        });

        RoundedButton cancel = new RoundedButton("Cancel", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        cancel.addActionListener(e -> dialog.dispose());

        buttonBar.add(confirm);
        buttonBar.add(cancel);

        assemble(dialog, contents, buttonBar);
        dialog.getRootPane().setDefaultButton(confirm);
        SwingUtilities.invokeLater(textField::requestFocusInWindow);
        dialog.setVisible(true);

        return result[0];
    }

    public static String showMaskedInput(Component parent, String title, String message) {
        JDialog dialog = buildBaseDialog(parent, title);
        String[] result = {""};

        JPanel contents = buildPanel();
        contents.add(bodyLabel(message));

        PasswordField textField = new PasswordField(Panel.FIELDTEXT, Panel.FIELD, Panel.FIELDTEXT);
        textField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        contents.add(Box.createVerticalStrut(10));
        contents.add(textField);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonBar.setBackground(Panel.PANEL_BG);

        RoundedButton confirm = new RoundedButton("Ok", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        Runnable confirmAction = () -> {
            result[0] = new String(textField.getPassword());
            dialog.dispose();
        };
        confirm.addActionListener(e -> confirmAction.run());

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed (KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_ENTER) confirmAction.run();
            }
        });

        RoundedButton cancel = new RoundedButton("Cancel", Panel.BUTTON_TEXT, Panel.BUTTON, Panel.ON_PRIMARY);
        cancel.addActionListener(e -> dialog.dispose());

        buttonBar.add(confirm);
        buttonBar.add(cancel);

        assemble(dialog, contents, buttonBar);
        dialog.getRootPane().setDefaultButton(confirm);
        SwingUtilities.invokeLater(textField::requestFocusInWindow);
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
        Frame owner = null;
        if (parent != null) {
            Window ancestor = SwingUtilities.getWindowAncestor(parent);
            if (ancestor instanceof Frame) owner = (Frame) ancestor;
        }

        JDialog dialog = new JDialog(owner, title, true);
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
