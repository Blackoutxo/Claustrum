package me.blackout.claustrum.ui.elements;

import me.blackout.claustrum.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    public final Color textColor, button, hs;

    public RoundedButton(String text, Color textColor, Color button, Color hs) {
        super(text);
        this.textColor = textColor;
        this.button = button;
        this.hs = hs;

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

        float arc = getHeight(); // (radius = height/2) -> full pill/stadium shape

        g2.setColor(button);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));

        if (getModel().isPressed()) {
            g2.setColor(withAlpha(hs, 32));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
        } else if (getModel().isRollover()) {
            g2.setColor(withAlpha(hs, 20));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {/*  No borders */ }

    public static Color withAlpha(Color c, int a) {
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
}
}
