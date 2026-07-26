package me.blackout.claustrum.ui.elements;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LineBorder extends javax.swing.border.AbstractBorder {
    private final Color color;
    private final int radius;
    private final EmptyBorder padding;

    LineBorder(Color color, int radius, EmptyBorder padding) {
        this.color = color;
        this.radius = radius;
        this.padding = padding;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        Insets p = padding.getBorderInsets(c);
        return new Insets(p.top + 4, p.left + 4, p.bottom + 4, p.right + 4);
    }
}
