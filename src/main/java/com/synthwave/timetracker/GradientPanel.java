package com.synthwave.timetracker;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GradientPanel extends JPanel {
    private Timer timer;
    private float hue = 0;

    public GradientPanel() {
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hue += 0.001f;
                if (hue > 1) {
                    hue = 0;
                }
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        Color color1 = Color.getHSBColor(hue, 0.3f, 1.0f);
        Color color2 = Color.getHSBColor((hue + 0.5f) % 1, 0.3f, 1.0f);

        GradientPaint gradientPaint = new GradientPaint(0, 0, color1, width, height, color2);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, width, height);
    }
}
