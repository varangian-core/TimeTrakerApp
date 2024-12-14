package com.synthwave.timetracker.dao;

import com.synthwave.timetracker.config.DatabaseConfig;
import com.synthwave.timetracker.model.Task; // Correct import

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {

    public void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255),
                    state VARCHAR(50),
                    parent_task_id INT DEFAULT NULL,
                    notes TEXT DEFAULT NULL
                );
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public int insert(Task task, Integer parentTaskId) throws SQLException {
        String sql = "INSERT INTO tasks (name, state, parent_task_id, notes) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getState());
            stmt.setObject(3, parentTaskId, Types.INTEGER);
            stmt.setString(4, task.getNotes()); // or setNull if you prefer
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1); // Return the generated ID
                }
            }
        }
        return -1; // Return -1 if no ID was generated
    }

    public List<Task> getAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("state"),
                        rs.getString("notes")
                );
                tasks.add(task);
            }
        }
        return tasks;
    }

    public Task getById(int id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Task(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("state"),
                            rs.getString("notes")
                    );
                }
            }
        }
        return null;
    }

    public void update(int id, String name, String state) throws SQLException {
        String sql = "UPDATE tasks SET name = ?, state = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, state);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
