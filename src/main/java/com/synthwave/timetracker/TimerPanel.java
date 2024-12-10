package com.synthwave.timetracker;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel implements ThemedComponent {
    public PomodoroTimer pomodoroTimer;
    private boolean isMinimized = false;
    private JButton minimizeButton;
    private JButton maximizeButton;
    private JButton themeToggleButton;

    private JLabel timerLabel;
    private JButton startButton;
    private JButton stopButton;

    public TimerPanel(SessionPanel sessionPanel, JFrame frame) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 200));

        // Top panel for minimize/maximize
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        minimizeButton = createMinimizeButton(frame);
        maximizeButton = createMaximizeButton(frame);
        maximizeButton.setVisible(false);
        topPanel.add(minimizeButton);
        topPanel.add(maximizeButton);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for timer label
        timerLabel = new JLabel("00:00");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setFont(new Font("Serif", Font.BOLD, 48));
        add(timerLabel, BorderLayout.CENTER);

        // Bottom panel for start/stop and theme toggle
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // Left part of bottom for start/stop
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomLeftPanel.setOpaque(false);
        startButton = createRoundedButton("Start", e -> pomodoroTimer.start());
        stopButton = createRoundedButton("Stop", e -> pomodoroTimer.stop());
        bottomLeftPanel.add(startButton);
        bottomLeftPanel.add(stopButton);
        bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);

        // Right part of bottom for theme toggle
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomRightPanel.setOpaque(false);
        themeToggleButton = new JButton("☀");
        themeToggleButton.setToolTipText("Change Theme");
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.addActionListener(e -> cycleTheme());
        bottomRightPanel.add(themeToggleButton);
        bottomPanel.add(bottomRightPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        pomodoroTimer = new PomodoroTimer(timerLabel, sessionPanel);

        // Register and apply theme
        ThemeManager.register(this);
        applyTheme(ThemeManager.getTheme());
    }

    private void cycleTheme() {
        Theme current = ThemeManager.getTheme();
        switch (current) {
            case LIGHT:
                ThemeManager.setTheme(Theme.DARK);
                break;
            case DARK:
                ThemeManager.setTheme(Theme.SYNTHWAVE);
                break;
            case SYNTHWAVE:
                ThemeManager.setTheme(Theme.LIGHT);
                break;
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        Color background;
        Color foreground;
        Color borderColor; // For potential RoundedBorder usage

        switch (theme) {
            case LIGHT:
                background = Color.WHITE;
                foreground = Color.BLACK;
                borderColor = foreground;
                themeToggleButton.setText("☀");
                break;
            case DARK:
                background = new Color(45,45,45);
                foreground = Color.WHITE;
                borderColor = foreground;
                themeToggleButton.setText("🌑");
                break;
            case SYNTHWAVE:
                background = new Color(40,0,40);
                foreground = Color.MAGENTA;
                borderColor = new Color(255,105,180);
                themeToggleButton.setText("🎵");
                break;
            default:
                background = Color.WHITE;
                foreground = Color.BLACK;
                borderColor = foreground;
                themeToggleButton.setText("☀");
        }

        setBackground(background);
        // Update all components with new colors
        for (Component c : getComponentsInHierarchy(this)) {
            if (c instanceof JPanel) {
                c.setBackground(background);
            }
            c.setForeground(foreground);
        }

        themeToggleButton.setBackground(background);
        themeToggleButton.setForeground(foreground);

        // If start/stop buttons have RoundedBorder, set their foreground to borderColor:
        startButton.setForeground(borderColor);
        startButton.setBackground(background);
        stopButton.setForeground(borderColor);
        stopButton.setBackground(background);

        minimizeButton.setBackground(background);
        minimizeButton.setForeground(foreground);
        maximizeButton.setBackground(background);
        maximizeButton.setForeground(foreground);

        repaint();
    }

    // Helper to get all components recursively
    private java.util.List<Component> getComponentsInHierarchy(Container container) {
        java.util.List<Component> compList = new java.util.ArrayList<>();
        for (Component comp : container.getComponents()) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getComponentsInHierarchy((Container) comp));
            }
        }
        return compList;
    }

    private JButton createRoundedButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
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