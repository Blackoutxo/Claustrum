package me.blackout.claustrum.ui.panels;

import me.blackout.claustrum.ui.Panel;
import me.blackout.claustrum.ui.elements.CardRenderer;
import me.blackout.claustrum.ui.elements.ScrollBar;
import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class FavouritePanel extends JPanel {
    private CardRenderer cardRenderer;

    public FavouritePanel(CardRenderer renderer) {
        super(new BorderLayout());
        setBackground(Panel.PANEL_BG);
        setBorder(new EmptyBorder(24, 24, 24, 16));

        this.cardRenderer = renderer;
        cardRenderer.refresh("", true);

        JLabel header = new JLabel("Favourites");
        header.setFont(Utils.spaceGrotesk.deriveFont(Font.BOLD, 32f));
        header.setForeground(Panel.TEXT);
        header.setBackground(Panel.PANEL_BG);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(header, BorderLayout.NORTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cardRenderer.refresh("", true);
            }
        });

        JScrollPane scroll = new JScrollPane(cardRenderer.getContainer());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Panel.PANEL_BG);
        scroll.getVerticalScrollBar().setUI(new ScrollBar(Panel.ScrollThumb, Panel.ThumbHover));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }
}
