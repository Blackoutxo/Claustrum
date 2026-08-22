package me.blackout.claustrum.ui.elements;

import me.blackout.claustrum.utils.Utils;
import me.blackout.claustrum.utils.generator.Generator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StrengthMeter extends JPanel {
    private final double  BAR_MAX = 128; // 128 = Very strong, bar max limit
    private final int BAR_HEIGHT = 6;

    private double fillFraction = 0;
    private Color barColor = Color.GRAY;
    private final JLabel label = new JLabel(" ");

    public StrengthMeter() {
        setLayout(new BorderLayout(0, 4));
        setOpaque(false);
        setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel bar = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(barColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT));

                int fillWidth = (int) (getWidth() * fillFraction);
                if (fillWidth > 0) {
                    g2.setColor(barColor);
                    g2.fill(new RoundRectangle2D.Float(0, 0, fillWidth, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT));
                }

                g2.dispose();
            }
        };

        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, BAR_HEIGHT));

        bar.setFont(Utils.spaceGrotesk.deriveFont(11f));

        add(bar, BorderLayout.NORTH);
        add(label, BorderLayout.CENTER);
    }

    public void update(String password) {
        if (password == null || password.isEmpty()) {
            barColor = Color.GRAY;
            fillFraction = 0;
            label.setText(" ");
            repaint();
            return;
        }

        double bits = Generator.estimateEntropy(password);
        String strength = Generator.strengthLabel(bits);

        fillFraction = Math.min(1.0, bits / BAR_MAX);
        barColor = barColor(strength);

        label.setText(strength + " (" + Math.round(bits) + " bits)");
        label.setForeground(barColor);

        repaint();
    }

    private Color barColor(String strength) {
        return switch (strength) {
            case "Very Weak" -> new Color(220, 26, 26);
            case "Weak" -> new Color(189, 116, 28);
            case "Reasonable" -> new Color(182, 169, 37);
            case "Strong" -> new Color(118, 185, 34);
            default -> new Color(48, 236, 19);
        };
    }
}
