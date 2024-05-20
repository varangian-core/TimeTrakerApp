package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.*;

public class TimeTrackerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("\uD83D\uDD52 Akari's Time Tracker \uD83E\uDD17");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JPanel mainPanel = new GradientPanel(); // Use GradientPanel for main panel
            mainPanel.setLayout(new BorderLayout());

            SessionPanel sessionPanel = new SessionPanel();
            TaskPanel taskPanel = new TaskPanel(sessionPanel); // Pass sessionPanel to taskPanel
            TimerPanel timerPanel = new TimerPanel(sessionPanel, frame); // Pass frame to TimerPanel

            mainPanel.add(taskPanel, BorderLayout.WEST);
            mainPanel.add(sessionPanel, BorderLayout.CENTER);
            mainPanel.add(timerPanel, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}
