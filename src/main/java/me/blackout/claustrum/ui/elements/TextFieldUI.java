package me.blackout.claustrum.ui.elements;

import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TextFieldUI extends JTextField {
    private final String placeholder;

    private final Color background;

    public TextFieldUI(String placeholder, Color background, Color textColor) {
        this.placeholder = placeholder;
        this.background = background;

        setOpaque(false);
        setBorder(new EmptyBorder(8, 14, 8, 14));
        setFont(Utils.spaceGrotesk.deriveFont(13f));
        setForeground(textColor);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Box
        g2.setColor(background);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));

        g2.dispose();
        super.paintComponent(g);

        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D pg = (Graphics2D) g.create();
            pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //pg.setColor(TEXT);
            pg.setFont(Utils.spaceGrotesk.deriveFont(13f));

            FontMetrics fm = pg.getFontMetrics();
            pg.drawString(placeholder, getInsets().left + 10, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            pg.dispose();
        }
    }
}
