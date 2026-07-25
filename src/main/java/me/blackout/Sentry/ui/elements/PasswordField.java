package me.blackout.Sentry.ui.elements;

import me.blackout.Sentry.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PasswordField extends JPasswordField {
    private final Color textColor;
    private final Color background;
    private final Color caretColor;

    public PasswordField(Color textColor, Color background, Color caretColor) {
        this.textColor = textColor;
        this.background = background;
        this.caretColor = caretColor;

        setOpaque(false);
        setBorder(new EmptyBorder(8, 14, 8, 14));
        setFont(Utils.spaceGrotesk.deriveFont(13f));
        setForeground(textColor);
        setCaretColor(caretColor);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Box
        g2.setColor(background);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));

        // Box border
        //g2.setColor(CARD_BORDER);
        //g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 12, 12));

        g2.dispose();
        super.paintComponent(g);
    }
}
