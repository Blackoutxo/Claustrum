package me.blackout.claustrum.ui.elements;

import me.blackout.claustrum.Main;
import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.file.FileManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.function.Consumer;

import static me.blackout.claustrum.utils.Utils.allEntries;

public class CardRenderer extends JPanel {
    public final Color background, hover, textColor, border;
    private final Consumer<Utils.Entry> consumer;
    private boolean favouritesOnly = false;
    private boolean dark;

    private final FileManager file = new FileManager();
    public final JPanel listContainer = new JPanel();
    public static Utils.Entry selectedEntry = null;
    private String currentFilter = "";
    private String tagFilter = "All";

    public CardRenderer(Color textColor, Color panelBg, Color background, Color hover, Color border, Consumer<Utils.Entry> consumer) {
        this.background = background;
        this.hover = hover;
        this.textColor = textColor;
        this.border = border;
        this.dark = Utils.darkTheme;

        this.consumer = consumer;

        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(panelBg);
        listContainer.setOpaque(true);
    }

    public JPanel getContainer() {
        return listContainer;
    }

    // Build card
    public JPanel buildCard(Utils.Entry entry) {
        boolean isSelected = entry.equals(selectedEntry);

        JLabel title = new JLabel(entry.title());
        title.setForeground(textColor);
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 16f));
        title.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel avatar = new JLabel(entry.title().substring(0, 1).toUpperCase());
        avatar.setBorder(new EmptyBorder(10, 10, 10, 0));
        avatar.setOpaque(true);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setForeground(textColor);
        avatar.setBackground(color(entry.title()));
        avatar.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 30f));
        avatar.setPreferredSize(new Dimension(100, 0));

        JLabel favouriteIcon = setFavouriteIcon(entry);
        favouriteIcon.setBorder(new EmptyBorder(0, 0, 0, 14));

        RoundedPanel card = new RoundedPanel(20);
        card.setLayout(new BorderLayout());
        card.setBackground(isSelected ? hover : background);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(avatar, BorderLayout.WEST);
        card.add(title, BorderLayout.CENTER);
        card.add(favouriteIcon, BorderLayout.EAST);
        card.setPreferredSize(new Dimension(0, 60));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedEntry = entry;
                refresh(currentFilter, favouritesOnly, "All");
                if (consumer != null) consumer.accept(entry);
            }
        });

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(10, 0, 10, 0));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        outer.add(card, BorderLayout.CENTER);

        return outer;
    }

    // Refresh list container
    public void refresh(String filter, boolean favouritesOnly, String tagFilter) {
        currentFilter = filter == null ? "" : filter;
        this.tagFilter = tagFilter;
        this.favouritesOnly = favouritesOnly;
        listContainer.removeAll();

        String t = currentFilter.trim().toLowerCase();
        for (Utils.Entry entry : allEntries) {
            boolean titleMatch = entry.title().toLowerCase().contains(t);
            boolean tagMatch = entry.tag().stream()
                    .anyMatch(tag -> tag.toLowerCase().contains(t));

            boolean matchesFilter = t.isEmpty() || titleMatch || tagMatch;
            boolean favourite = !favouritesOnly || Utils.favourites.contains(entry.title());
            boolean matchesTagFilter = tagFilter.equals("All") || entry.tag().contains(tagFilter);

            if (matchesFilter && favourite && matchesTagFilter) listContainer.add(buildCard(entry));
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    public void refresh() {
        refresh(currentFilter, this.favouritesOnly, "All");
    }

    // Icon
    private JLabel setFavouriteIcon(Utils.Entry entry) {
        JLabel icon = new JLabel();
        icon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boolean isFavourite = Utils.favourites.contains(entry.title());
        loadIcon(icon, isFavourite);

        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean favourite = !Utils.favourites.contains(entry.title());

                if (favourite) Utils.favourites.add(entry.title());
                  else Utils.favourites.remove(entry.title());

                loadIcon(icon, favourite);
                try {  file.saveFavourite();   } catch (GeneralSecurityException | IOException ignored) {}
                e.consume();
            }
        });

        return icon;
    }

    private void loadIcon(JLabel label, boolean filled) {
        String path = filled ? "/icons/" + (dark ? "light" : "dark") + "/filled/favourite.png" : "/icons/" + (dark ? "light" : "dark") + "/favourite.png";

        URL url = Main.class.getResource(path);
        if (url == null) return;
        try {
            BufferedImage original = ImageIO.read(url);
            Image scaled = original.getScaledInstance(20, 20, Image.SCALE_SMOOTH); // smaller than 28 — this is a secondary/inline indicator, not the main avatar
            label.setIcon(new ImageIcon(scaled));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Color color(String seed) {
        int hash = Math.abs(seed.hashCode());
        Color[] palette = {
                new Color(39, 134, 192), new Color(162, 37, 37),
                new Color(67, 178, 36), new Color(192, 167, 39),
                new Color(89, 32, 234), new Color(37, 47, 162)
        };
        return palette[hash % palette.length];
    }
}