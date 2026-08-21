package me.blackout.claustrum.ui;

import me.blackout.claustrum.Main;
import me.blackout.claustrum.ui.elements.*;
import me.blackout.claustrum.ui.panels.EvaluationPanel;
import me.blackout.claustrum.ui.panels.FavouritePanel;
import me.blackout.claustrum.ui.panels.SettingsPanel;
import me.blackout.claustrum.utils.file.FileManager;
import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.generator.Generator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.blackout.claustrum.utils.Utils.allEntries;

public class Panel extends JFrame {
    // ---------------------------------------------------------------
    //                          Color palette
    // ---------------------------------------------------------------
    public static Color PRIMARY = new Color(208, 188, 255);
    public static Color ON_PRIMARY = new Color(56, 30, 114);

    public static Color BUTTON = PRIMARY;
    public static Color BUTTON_TEXT = ON_PRIMARY;

    public static Color TEXT = new Color(230, 225, 229);
    public static Color FIELD = new Color(202, 196, 208);
    public static Color FIELDTEXT = ON_PRIMARY;

    public static Color PANEL_BG = new Color(28, 27, 31);
    public static Color SIDEBAR_BG = new Color(35, 33, 39);

    public static Color CARD_BG = new Color(35, 33, 39);
    public static Color CARD_HOVER = new Color(48, 46, 54);
    public static Color CARD_BORDER = new Color(255, 255, 255);

    public static Color ScrollThumb = PRIMARY;
    public static Color ThumbHover = PRIMARY.darker();

    // Field vars
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelContainer = new JPanel(cardLayout);

    private static final FileManager file = new FileManager();
    private final CardRenderer cardRenderer = new CardRenderer(TEXT, PANEL_BG, CARD_BG, CARD_HOVER, CARD_BORDER , entry -> openDetailDialog());
    private final CardRenderer favouriteCardRenderer = new CardRenderer(TEXT, PANEL_BG, CARD_BG, CARD_HOVER, CARD_BORDER, entry -> openDetailDialog());

    // Panels
    private final SettingsPanel settingsPanel = new SettingsPanel();
    private final FavouritePanel favouritePanel = new FavouritePanel(favouriteCardRenderer);
    private final EvaluationPanel evalPanel = new EvaluationPanel();

    // Panel
    public Panel() throws IOException, FontFormatException {
        super("Claustrum");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 600);
        setMinimumSize(new Dimension(980, 600));
        setPreferredSize(new Dimension(980, 600));

        Utils.registerFont();

        getContentPane().setBackground(PANEL_BG);

        panelContainer.add(mainPanel(), "home");
        panelContainer.add(favouritePanel, "favourite");
        panelContainer.add(evalPanel, "evaluation");
        panelContainer.add(settingsPanel, "settings");

        add(sideBar(), BorderLayout.WEST);
        add(panelContainer, BorderLayout.CENTER);

        cardLayout.show(panelContainer, "home");
    }

    // ---------------------------------------------------------------
    // panels
    // ---------------------------------------------------------------
    private JPanel mainPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(PANEL_BG);
        center.setBorder(new EmptyBorder(24, 24, 24, 16));

        // Search bar
        TextFieldUI search = new TextFieldUI("Search for items.....", FIELD, FIELDTEXT);
        search.setPreferredSize(new Dimension(0, 38));
        search.setEditable(true);
        search.setFocusable(true);

        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { cardRenderer.refresh(search.getText(), false, "All"); }
            @Override public void removeUpdate(DocumentEvent e)  { cardRenderer.refresh(search.getText(), false, "All"); }
            @Override public void changedUpdate(DocumentEvent e) { cardRenderer.refresh(search.getText(), false, "All"); }
        });

        // Top panel
        JPanel top = new JPanel(new BorderLayout());

        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 16, 0));
        top.add(search, BorderLayout.CENTER);
        center.add(top, BorderLayout.NORTH);

        // Entry card & Scroll Panel
        JScrollPane scroll = new JScrollPane(cardRenderer.getContainer());
        scroll.setBorder(null);
        cardRenderer.refresh();
        scroll.getViewport().setBackground(PANEL_BG);
        scroll.setOpaque(false);

        scroll.getVerticalScrollBar().setUI(new ScrollBar(ScrollThumb, ThumbHover));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setPreferredSize(new Dimension(12, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setOpaque(false);
        center.add(scroll);

        // Button
        Button addButton = new Button("+  ADD KEYS", BUTTON_TEXT);

        // Button action
        addButton.addActionListener(e ->
            openAddDialog()
        );

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(16, 0, 0, 0)); // space above the button
        bottomBar.add(addButton);

        center.add(bottomBar, BorderLayout.SOUTH);
        cardRenderer.refresh("", false, "All");
        return center;
    }

    public JPanel sideBar() {
        JPanel sideBar = new JPanel(new BorderLayout());

        // Set layout
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(SIDEBAR_BG);
        sideBar.setPreferredSize(new Dimension(220, 0));
        sideBar.setBorder(new EmptyBorder(24, 0, 0, 0));

        // Logo
        JLabel logoLabel = new JLabel("CLAUSTRUM");
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(new EmptyBorder(0,  20,10 , 0));

        logoLabel.setForeground(TEXT);
        logoLabel.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 30f));

        // Home
        JLabel home = navItem("Home", "home.png", SettingsPanel.darkTheme);
        home.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardRenderer.refresh("", false, "All");
                cardLayout.show(panelContainer, "home");
            }
        });

        // Favourite
        JLabel favourite = navItem("Favourite", "favourite.png", SettingsPanel.darkTheme);
        favourite.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                favouriteCardRenderer.refresh("", true, "All");
                cardLayout.show(panelContainer, "favourite");
            }
        });

        // Evaluation
        JLabel eval = navItem("Evaluation", "eval_board.png", SettingsPanel.darkTheme);
        eval.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                evalPanel.init();
                cardLayout.show(panelContainer, "evaluation");
            }
        });

        // Settings
        JLabel settings = navItem("Settings", "settings.png", SettingsPanel.darkTheme);
        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(panelContainer, "settings");
            }
        });

        sideBar.add(logoLabel);
        sideBar.add(home);
        sideBar.add(favourite);
        sideBar.add(eval);
        sideBar.add(settings);
        sideBar.add(Box.createVerticalStrut(24));
        return sideBar;
    }

    private JLabel navItem(String title, String path, boolean dark) {
        JLabel nav = new JLabel(title);
        nav.setBorder(new EmptyBorder(10, 20, 10, 0));

        nav.setForeground(TEXT);
        nav.setFont(Utils.spaceGrotesk.deriveFont(20f));
        nav.setIcon(new ImageIcon(icon((dark ? "light" : "dark") + "/" + path, 25, 25)));

        return nav;
    }

    private Image icon(String path, int w, int h) {
        URL icon = Main.class.getResource("/icons/" + path);
        if (icon != null) {
            BufferedImage original = null;
            try {
                original = ImageIO.read(icon);
            } catch (IOException ignored) {}
            return original.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        }

        return null;
    }

    public static void applyDarkTheme() {
        PRIMARY = new Color(208, 188, 255);
        ON_PRIMARY = new Color(56, 30, 114);

        BUTTON = PRIMARY;
        BUTTON_TEXT = ON_PRIMARY;

        TEXT = new Color(230, 225, 229);
        FIELD = new Color(202, 196, 208);
        FIELDTEXT = ON_PRIMARY;

        PANEL_BG = new Color(28, 27, 31);
        SIDEBAR_BG = new Color(35, 33, 39);

        CARD_BG = new Color(35, 33, 39);
        CARD_HOVER = new Color(48, 46, 54);
        CARD_BORDER = new Color(255, 255, 255);

        ScrollThumb = PRIMARY;
        ThumbHover = PRIMARY.darker();
    }

    public static void applyLightTheme() {
        PRIMARY = new Color(103, 80, 164);
        ON_PRIMARY = new Color(255, 255, 255);

        BUTTON = PRIMARY;
        BUTTON_TEXT = ON_PRIMARY;

        TEXT = new Color(29, 27, 32);
        FIELD = new Color(231, 224, 236);
        FIELDTEXT = TEXT;

        PANEL_BG = new Color(254, 247, 255);
        SIDEBAR_BG = new Color(243, 237, 247);

        CARD_BG = new Color(243, 237, 247);
        CARD_HOVER = new Color(230, 224, 233);
        CARD_BORDER = new Color(121, 116, 126);

        ScrollThumb = PRIMARY;
        ThumbHover = PRIMARY.darker();
    }

    // Dialog panel
    public void openAddDialog() {
        JDialog dialog = new JDialog(this, "Add new key", true);
        JPanel form = new JPanel(new GridBagLayout());

        dialog.setBackground(PANEL_BG);
        form.setBackground(PANEL_BG);

        // Layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleL = new JLabel("TITLE");
        JTextField title = new TextFieldUI("", FIELD, FIELDTEXT);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; // For title label
        titleL.setFont(Utils.spaceGrotesk.deriveFont(14f));
        titleL.setForeground(TEXT);
        form.add(titleL, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; // For title text field
        form.add(title, gbc);

        // Passkey
        JLabel passL = new JLabel("PASSWORD");
        JPasswordField password = new PasswordField(ON_PRIMARY, FIELD, FIELDTEXT);
        RoundedButton generate = new RoundedButton("", BUTTON_TEXT, BUTTON, ON_PRIMARY);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        passL.setFont(Utils.spaceGrotesk.deriveFont(14f));
        passL.setForeground(TEXT);
        form.add(passL, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0;
        form.add(password, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        generate.setIcon(new ImageIcon(icon("dark/key.png", 18, 18)));
        form.add(generate, gbc);

        generate.addActionListener(e -> {
            password.setText(Generator.generate());
        });

        dialog.add(form, BorderLayout.CENTER);

        // Tag
        JLabel tagsLabel = new JLabel("Tags");
        JTextField tagsField = new TextFieldUI("", FIELD, FIELDTEXT);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        tagsLabel.setFont(Utils.spaceGrotesk.deriveFont(14f));
        tagsLabel.setForeground(TEXT);
        form.add(tagsLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1;
        form.add(tagsField, gbc);

        dialog.add(form, BorderLayout.CENTER);

        // Button
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        buttonBar.setBackground(PANEL_BG);
        buttonBar.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Cancel button
        Button cancel = new Button("CANCEL", ON_PRIMARY);

        cancel.addActionListener(e ->
            dialog.dispose()
        );

        // Save button
        Button save = new Button("SAVE", ON_PRIMARY);

        save.addActionListener(e -> {
            String strTitle = title.getText().trim();
            String passKey = new String(password.getPassword());
            List<String> tags = Arrays.stream(tagsField.getText().split(","))
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .distinct()
                    .toList();

            if (strTitle.isEmpty() || passKey.isEmpty()) return;

            Optional<Utils.Entry> option = Utils.findByTitle(strTitle);

            // Check if title is re-used
            if (option.isPresent()) {
                int choice = JOptionPane.showConfirmDialog(
                        dialog, "Entry named " + strTitle + " is already in use, do you want to overwrite it?",
                        "Duplicate Entry", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
                );

                // Check choice made
                if (choice != JOptionPane.OK_OPTION) return;

                // Remove former entry
                allEntries.remove(option.get());

                cardRenderer.getContainer().remove(cardRenderer.buildCard(option.get())); // Remove from list container
                cardRenderer.refresh();
            }

            try {
                allEntries.add(new Utils.Entry(strTitle, passKey, tags)); // Add to entries

                cardRenderer.getContainer().add(cardRenderer.buildCard(new Utils.Entry(strTitle, passKey, tags))); // Add to list container
                cardRenderer.refresh();

                file.saveEntries(); // Save file
                dialog.dispose();

                CardRenderer.selectedEntry = null; // Set as null
            } catch (IOException | GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonBar.add(cancel);
        buttonBar.add(save);
        dialog.add(buttonBar, BorderLayout.SOUTH);

        // Pack dialog box
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 380), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void openDetailDialog() {
        JDialog dialog = new JDialog(this, "Detail", true);
        JPanel form = new JPanel(new GridBagLayout());

        Utils.Entry entry = CardRenderer.selectedEntry;

        dialog.setBackground(PANEL_BG);
        form.setBackground(PANEL_BG);

        // Layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleL = new JLabel("Title");
        JTextField title = new TextFieldUI(entry.title(), FIELD, FIELDTEXT);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; // For title label
        titleL.setFont(Utils.spaceGrotesk.deriveFont(14f));
        titleL.setForeground(TEXT);
        form.add(titleL, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; // For title text field
        title.setEditable(false);
        form.add(title, gbc);

        // Passkey
        JLabel passL = new JLabel("Password");
        JPasswordField password = new PasswordField(FIELDTEXT, FIELD, FIELDTEXT);
        RoundedButton copyButton = new RoundedButton("", BUTTON_TEXT, BUTTON, FIELDTEXT);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; // For passkey label
        passL.setFont(Utils.spaceGrotesk.deriveFont(14f));
        passL.setForeground(TEXT);
        form.add(passL, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0; // For passkey text field
        password.setText(entry.password());
        form.add(password, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        copyButton.setIcon(new ImageIcon(icon("dark/copy.png", 15, 15)));
        form.add(copyButton, gbc);

        // Copy Button action
        copyButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(entry.password());
            clipboard.setContents(selection, selection);
        });

        dialog.add(form, BorderLayout.CENTER);

        // Tag
        JLabel tagsLabel = new JLabel("Tags");
        JTextField tagsField = new TextFieldUI(entry.tag().toString(), FIELD, FIELDTEXT);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        tagsLabel.setFont(Utils.spaceGrotesk.deriveFont(14f));
        tagsLabel.setForeground(TEXT);
        form.add(tagsLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1;
        form.add(tagsField, gbc);

        dialog.add(form, BorderLayout.CENTER);

        // Button
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        buttonBar.setBackground(PANEL_BG);
        buttonBar.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Cancel button
        Button delete = new Button("DELETE", ON_PRIMARY);

        delete.addActionListener(e -> {
            try {
                deleteEntry(entry);
                cardRenderer.refresh();
                dialog.dispose();

                CardRenderer.selectedEntry = null; // Set as null
            } catch (GeneralSecurityException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Save button
        Button show = new Button("SHOW", ON_PRIMARY);

        final int[] ps = {0};
        show.addActionListener(e -> {
            if (ps[0] == 0) {
                password.setEchoChar((char) 0);
                ps[0] += 1;
            } else {
                password.setEchoChar('\u2022');
                ps[0] = 0;
            }
        });

        // Add to button bar
        buttonBar.add(delete);
        buttonBar.add(show);
        dialog.add(buttonBar, BorderLayout.SOUTH);

        // Pack dialog box
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 380), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Delete selected
    private void deleteEntry(Utils.Entry entry) throws GeneralSecurityException, IOException {
        // Confirm dialog panel
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + entry.title() + "\"?", "Confirm delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            allEntries.remove(entry);
            file.saveEntries();
        }
    }

    // ---------------------------------------------------------------
    //  J-Elements modification
    // ---------------------------------------------------------------

    static class Button extends JButton {
        public final Color textColor;

        Button(String text, Color textColor) {
            super(text);
            this.textColor = textColor;

            setForeground(textColor);
            setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 13f));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(9, 18, 9, 18));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float arc = getHeight(); // radius = height/2 -> full pill/stadium shape

            g2.setColor(BUTTON);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));

            if (getModel().isPressed()) {
                g2.setColor(withAlpha(ON_PRIMARY, 32));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
            } else if (getModel().isRollover()) {
                g2.setColor(withAlpha(ON_PRIMARY, 20));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
            }

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {/*  No borders */ }
    }

    public static Color withAlpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
}
