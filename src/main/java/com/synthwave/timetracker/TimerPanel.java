package com.synthwave.timetracker;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    public PomodoroTimer pomodoroTimer;
    private boolean isMinimized = false;
    private JButton minimizeButton;
    private JButton maximizeButton;

    public TimerPanel(SessionPanel sessionPanel, JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));
        setPreferredSize(new Dimension(600, 200));

        JLabel timerLabel = new JLabel("00:00");
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setFont(new Font("Serif", Font.BOLD, 48));

        JPanel timerContainer = new JPanel(new BorderLayout());
        timerContainer.setOpaque(false);
        timerContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        timerContainer.add(timerLabel, BorderLayout.CENTER);

        minimizeButton = createMinimizeButton(frame);
        maximizeButton = createMaximizeButton(frame);
        maximizeButton.setVisible(false);

        JPanel controlButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlButtonPanel.setOpaque(false);
        controlButtonPanel.add(minimizeButton);
        controlButtonPanel.add(maximizeButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(createRoundedButton("Start", e -> pomodoroTimer.start()));
        buttonPanel.add(createRoundedButton("Pause", e -> pomodoroTimer.pause()));
        buttonPanel.add(createRoundedButton("Stop", e -> pomodoroTimer.stop()));
        buttonPanel.add(createRoundedButton("Reset", e -> pomodoroTimer.reset()));

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);
        buttonContainer.add(controlButtonPanel, BorderLayout.WEST);
        buttonContainer.add(buttonPanel, BorderLayout.CENTER);

        GradientPanel buttonGradientPanel = new GradientPanel();
        buttonGradientPanel.setPreferredSize(new Dimension(600, 50));
        buttonGradientPanel.setOpaque(false);
        buttonGradientPanel.add(buttonContainer);

        add(timerContainer, BorderLayout.CENTER);
        add(buttonGradientPanel, BorderLayout.NORTH);

        pomodoroTimer = new PomodoroTimer(timerLabel, sessionPanel);
    }

    private JButton createRoundedButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(173, 216, 230)); // Pastel blue
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            button.getBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFont(new Font("Serif", Font.BOLD, 14));
        button.setBorder(new RoundedBorder(15));
        button.addActionListener(actionListener);
        return button;
    }

    private JButton createMinimizeButton(JFrame frame) {
        JButton button = new JButton("\u25F4");
        button.setFont(new Font("Serif", Font.BOLD, 24));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(e -> minimize(frame));
        return button;
    }

    private JButton createMaximizeButton(JFrame frame) {
        JButton button = new JButton("\u25F5");
        button.setFont(new Font("Serif", Font.BOLD, 24));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(e -> maximize(frame));
        return button;
    }

    private void minimize(JFrame frame) {
        if (!isMinimized) {
            frame.setSize(450, 220);
            isMinimized = true;
            minimizeButton.setVisible(false);
            maximizeButton.setVisible(true);
        }
    }

    private void maximize(JFrame frame) {
        if (isMinimized) {
            frame.setSize(800, 600);
            isMinimized = false;
            minimizeButton.setVisible(true);
            maximizeButton.setVisible(false);
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getForeground());
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = this.radius + 1;
            insets.right = this.radius + 1;
            insets.top = this.radius + 2;
            insets.bottom = this.radius;
            return insets;
        }
    }
}