package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.*;

public class TimeTrackerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("\uD83D\uDD52 Akari's Time Tracker \uD83E\uDD17");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JPanel mainPanel = new GradientPanel();
            mainPanel.setLayout(new BorderLayout());

            SessionPanel sessionPanel = new SessionPanel();
            TaskPanel taskPanel = new TaskPanel(sessionPanel);
            TimerPanel timerPanel = new TimerPanel(sessionPanel, frame);

            // Link SessionPanel to TimerPanel using the selection listener
            sessionPanel.setSelectedSessionListener(selectedSession -> {
                // When a session is selected, update the TimerPanel
                timerPanel.updateSelectedSession(selectedSession);
            });

            // Register mainPanel if themed
            if (mainPanel instanceof ThemedComponent) {
                ThemeManager.register((ThemedComponent) mainPanel);
            }

            mainPanel.add(taskPanel, BorderLayout.WEST);
            mainPanel.add(sessionPanel, BorderLayout.CENTER);
            mainPanel.add(timerPanel, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}
