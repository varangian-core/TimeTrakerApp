package com.synthwave.timetracker;

import com.synthwave.timetracker.dao.SessionDao;
import com.synthwave.timetracker.dao.TaskDao;
import com.synthwave.timetracker.model.Session;
import com.synthwave.timetracker.model.Task;

import javax.swing.*;
import java.awt.*;

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

                // Example: Preload some sample data
                preloadData(sessionDao, taskDao);

                // Create the main application window
                JFrame frame = new JFrame("\uD83D\uDD52 Akari's Time Tracker \uD83E\uDD17");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);

                JPanel mainPanel = new GradientPanel();
                mainPanel.setLayout(new BorderLayout());

                // Panels for the UI
                SessionPanel sessionPanel = new SessionPanel(sessionDao);
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

    private static void preloadData(SessionDao sessionDao, TaskDao taskDao) throws Exception {
        // Add some sample tasks (Now we use Task(int, String, String))
        Task task1 = new Task(0, "Plan the day", "To-Do");
        Task task2 = new Task(0, "Code review", "In-Progress");

        int task1Id = taskDao.insert(task1, null); // No parent task
        int task2Id = taskDao.insert(task2, null); // No parent task

        // Add some sample sessions
        Session session1 = new Session(0, "Morning Routine", 1200, 600, task1Id); // 20 mins session
        Session session2 = new Session(0, "Afternoon Coding", 1800, 0, task2Id); // 30 mins session

        sessionDao.insert(session1);
        sessionDao.insert(session2);

        System.out.println("Preloaded sample data into the database.");
    }
}
