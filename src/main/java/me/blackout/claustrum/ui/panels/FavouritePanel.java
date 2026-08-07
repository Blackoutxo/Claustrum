package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.CardRenderer;
import me.blackout.claustrum.ui.elements.ScrollBar;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class FavouritePanel extends JPanel {
    private CardRenderer cardRenderer;

    public FavouritePanel(Consumer<Utils.Entry> consume) {
        setLayout(new BorderLayout());
        setBackground(me.blackout.claustrum.ui.Panel.PANEL_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel header = new JLabel("Favourites");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 30f));
        header.setForeground(me.blackout.claustrum.ui.Panel.TEXT);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Init card renderer
        this.cardRenderer = new CardRenderer(
                Panel.TEXT,
                Panel.PANEL_BG,
                Panel.CARD_BG,
                Panel.CARD_HOVER,
                Panel.CARD_BORDER,
                true,
                consume
        );

        this.cardRenderer.refresh();

        // Set custom scroll UI
        JScrollPane scroll = new JScrollPane(cardRenderer.getContainer());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Panel.PANEL_BG);
        scroll.setOpaque(false);

        scroll.getVerticalScrollBar().setUI(new ScrollBar(Panel.ScrollThumb, Panel.ThumbHover));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setPreferredSize(new Dimension(12, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setOpaque(false);

        add(scroll, BorderLayout.CENTER);
    }
}
