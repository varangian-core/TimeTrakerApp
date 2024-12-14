package com.synthwave.timetracker;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel implements ThemedComponent {
    private PomodoroTimer pomodoroTimer;
    private boolean isMinimized = false;
    private JButton minimizeButton;
    private JButton maximizeButton;
    private JButton themeToggleButton;
    private ProgressCircle progressCircle;
    private JLabel timerLabel;

    private SessionPanel sessionPanel; // store reference for use in updateSelectedSession()

    public TimerPanel(SessionPanel sessionPanel, JFrame frame) {
        this.sessionPanel = sessionPanel;

        setLayout(null);
        setPreferredSize(new Dimension(600, 200));

        timerLabel = new JLabel("--:--");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setFont(new Font("Serif", Font.BOLD, 48));
        timerLabel.setBounds(200, 40, 200, 60);
        add(timerLabel);

        minimizeButton = createMinimizeButton(frame);
        minimizeButton.setBounds(10, 10, 40, 40);
        add(minimizeButton);

        maximizeButton = createMaximizeButton(frame);
        maximizeButton.setBounds(60, 10, 40, 40);
        maximizeButton.setVisible(false);
        add(maximizeButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        // Start and Stop buttons only
        JButton startButton = createRoundedButton("Start", e -> {
            if (pomodoroTimer != null) {
                pomodoroTimer.start();
            }
        });
        JButton stopButton = createRoundedButton("Stop", e -> {
            if (pomodoroTimer != null) {
                pomodoroTimer.stop();
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.setBounds(150, 120, 300, 50);
        add(buttonPanel);

        themeToggleButton = new JButton("☀");
        themeToggleButton.setToolTipText("Change Theme");
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setBounds(550, 150, 40, 40);
        themeToggleButton.addActionListener(e -> cycleTheme());
        add(themeToggleButton);

        // Add a ProgressCircle near the timer
        progressCircle = new ProgressCircle();
        progressCircle.setBounds(420, 40, 60, 60);
        add(progressCircle);

        // Initially show --:-- and empty circle, no PomodoroTimer created yet
        ThemeManager.register(this);
        applyTheme(ThemeManager.getTheme());
    }

    /**
     * Called by SessionPanel (via a listener) when a new RuntimeSession is selected.
     * If session is null, show --:-- and empty circle.
     * Otherwise, create a new PomodoroTimer for the selected runtime session.
     */
    public void updateSelectedSession(RuntimeSession newSession) {
        // Stop old timer if any
        if (pomodoroTimer != null) {
            pomodoroTimer.stop();
            pomodoroTimer = null;
        }

        if (newSession == null) {
            // No session selected
            timerLabel.setText("--:--");
            progressCircle.setProgress(0.0f);
        } else {
            // Create a new pomodoroTimer for the selected runtime session
            pomodoroTimer = new PomodoroTimer(timerLabel, sessionPanel, this, newSession);
            // Update display without starting the timer
            updateTimerDisplay(newSession);
        }
    }

    /**
     * Called by PomodoroTimer to update the circular progress fraction.
     */
    public void updateTimerProgress(float fraction) {
        progressCircle.setProgress(fraction);
    }

    /**
     * Update timer label and circle from the given runtime session without starting timer.
     */
    private void updateTimerDisplay(RuntimeSession session) {
        int totalTime = session.getDuration() * 60;
        int remainingTime = session.getRemainingTime();
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        float fraction = (totalTime > 0) ? (float)(totalTime - remainingTime) / totalTime : 0.0f;
        updateTimerProgress(fraction);
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

        switch (theme) {
            case LIGHT:
                background = Color.WHITE;
                foreground = Color.BLACK;
                themeToggleButton.setText("☀");
                break;
            case DARK:
                background = new Color(45,45,45);
                foreground = Color.WHITE;
                themeToggleButton.setText("🌑");
                break;
            case SYNTHWAVE:
                background = new Color(40,0,40);
                foreground = Color.MAGENTA;
                themeToggleButton.setText("🎵");
                break;
            default:
                background = Color.WHITE;
                foreground = Color.BLACK;
                themeToggleButton.setText("☀");
        }

        setBackground(background);
        minimizeButton.setBackground(background);
        minimizeButton.setForeground(foreground);
        maximizeButton.setBackground(background);
        maximizeButton.setForeground(foreground);
        themeToggleButton.setBackground(background);
        themeToggleButton.setForeground(foreground);

        for (Component c : getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(background);
                c.setForeground(foreground);
            }
            c.setForeground(foreground);
        }

        repaint();
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
