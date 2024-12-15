package com.synthwave.timetracker;

import com.synthwave.timetracker.dao.SessionDao;
import com.synthwave.timetracker.dao.TaskDao;
import com.synthwave.timetracker.model.Session;
import com.synthwave.timetracker.config.DatabaseConfig;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class TimeTrackerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize DAOs
                SessionDao sessionDao = new SessionDao();
                TaskDao taskDao = new TaskDao();

                // Create database tables
                sessionDao.createTable();
                taskDao.createTable();
                createPomodoroStateTable(); // Create pomodoro_state table

                // Create the main application window
                JFrame frame = new JFrame("\uD83D\uDD52 Akari's Time Tracker \uD83E\uDD17");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);

                JPanel mainPanel = new GradientPanel();
                mainPanel.setLayout(new BorderLayout());

                // Panels for the UI
                // SessionPanel no longer takes sessionDao as a parameter
                SessionPanel sessionPanel = new SessionPanel();
                TaskPanel taskPanel = new TaskPanel(taskDao, sessionPanel);
                TimerPanel timerPanel = new TimerPanel(sessionPanel, frame);

                // Link SessionPanel to TimerPanel using the selection listener
                sessionPanel.setSelectedSessionListener(selectedSession -> {
                    timerPanel.updateSelectedSession(selectedSession);
                });

                // Register mainPanel if themed
                if (mainPanel instanceof ThemedComponent) {
                    ThemeManager.register((ThemedComponent) mainPanel);
                }

                // Add components to the main panel
                mainPanel.add(taskPanel, BorderLayout.WEST);
                mainPanel.add(sessionPanel, BorderLayout.CENTER);
                mainPanel.add(timerPanel, BorderLayout.SOUTH);

                frame.add(mainPanel);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to initialize the application: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void createPomodoroStateTable() throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS pomodoro_state (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    session_key VARCHAR(255) UNIQUE,
                    remaining_time INT
                );
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
