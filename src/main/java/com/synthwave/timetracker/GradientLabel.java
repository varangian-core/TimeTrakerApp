package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GradientLabel extends JLabel implements ActionListener {
    private Timer timer;
    private float hue;

    public GradientLabel(String text) {
        super(text, SwingConstants.CENTER);
        setOpaque(false);
        setForeground(Color.BLACK);
        setFont(new Font("Serif", Font.BOLD, 14));
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Initialize the hue for dynamic gradient
        hue = 0.0f;
        timer = new Timer(50, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        // Define pastel colors for the gradient
        Color startColor = Color.getHSBColor(hue, 0.3f, 1.0f);
        Color endColor = Color.getHSBColor((hue + 0.5f) % 1, 0.3f, 1.0f);

        GradientPaint gradientPaint = new GradientPaint(0, 0, startColor, width, height, endColor);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, width, height);
        super.paintComponent(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        hue += 0.001f;
        if (hue > 1.0f) {
            hue = 0.0f;
        }
        repaint();
    }
}
