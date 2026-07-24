package me.blackout.Sentry.ui.elements;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ScrollBar extends BasicScrollBarUI {
    public static Color color;
    public static Color hover;

    private static final int THICKNESS = 12;
    private static final int PADDING = 3;

    public ScrollBar(Color background, Color hover) {
        this.thumbColor = background;
        ScrollBar.hover = hover;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return zeroButton();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return zeroButton();
    }

    private JButton zeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if(thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = isThumbRollover() ? hover : color;
        g2.setColor(fill);

        RoundRectangle2D thumb = new RoundRectangle2D.Float(
                thumbBounds.x + PADDING, thumbBounds.y,
                thumbBounds.width, thumbBounds.height,
                THICKNESS, THICKNESS);

        g2.fill(thumb);
        g2.dispose();
    }
}
