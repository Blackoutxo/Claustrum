package me.blackout.claustrum.ui.elements;

import me.blackout.claustrum.ui.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class RadioButton extends JRadioButton {

    private static final int CIRCLE_SIZE = 18;
    private static final int GAP = 8;

    public RadioButton(String text) {
        super(text);
        setForeground(Panel.TEXT);
        setFont(getFont().deriveFont(13f));
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setIcon(new BlankIcon());
        setIconTextGap(GAP);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = (getHeight() - CIRCLE_SIZE) / 2;
        Ellipse2D outerRing = new Ellipse2D.Float(0, y, CIRCLE_SIZE, CIRCLE_SIZE);

        if (isSelected()) {
            g2.setColor(Panel.PRIMARY); // Outer Ring
            g2.fill(outerRing);

            int innerInset = 4; // Inner Cutout
            Ellipse2D innerCutout = new Ellipse2D.Float(innerInset, y + innerInset, CIRCLE_SIZE - innerInset * 2, CIRCLE_SIZE - innerInset * 2);
            g2.setColor(Panel.CARD_BG);
            g2.fill(innerCutout);

            int dotInset = 6;
            Ellipse2D dot = new Ellipse2D.Float(dotInset, y + dotInset, CIRCLE_SIZE - dotInset * 2, CIRCLE_SIZE - dotInset * 2);
            g2.setColor(Panel.PRIMARY);
            g2.fill(dot);
        } else {
            g2.setColor(getModel().isRollover() ? Panel.PRIMARY.darker() : Panel.PRIMARY);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new Ellipse2D.Float(1, y + 1, CIRCLE_SIZE - 2, CIRCLE_SIZE - 2));
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private static class BlankIcon implements Icon {
        public void paintIcon(Component c, Graphics g, int x, int y) { }
        public int getIconWidth() { return CIRCLE_SIZE; }
        public int getIconHeight() { return CIRCLE_SIZE; }
    }
}
