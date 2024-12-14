package com.synthwave.timetracker.dao;

import com.synthwave.timetracker.model.Session;
import com.synthwave.timetracker.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDao {

    // Create the `sessions` table
    public void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS sessions (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255),
                    assigned_time INT,
                    completed_time INT,
                    parent_task_id INT
                );
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    // Insert a new session
    public int insert(Session session) throws SQLException {
        String sql = "INSERT INTO sessions (name, assigned_time, completed_time, parent_task_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, session.getName());
            stmt.setInt(2, session.getAssignedTime());
            stmt.setInt(3, session.getCompletedTime());
            stmt.setInt(4, session.getParentTaskId());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1); // Return the generated ID
                }
            }
        }
        return -1; // Return -1 if no ID was generated
    }

    // Retrieve all sessions
    public List<Session> getAll() throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sessions.add(new Session(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("assigned_time"),
                        rs.getInt("completed_time"),
                        rs.getInt("parent_task_id")
                ));
            }
        }
        return sessions;
    }

    // Retrieve a session by ID
    public Session getById(int id) throws SQLException {
        String sql = "SELECT * FROM sessions WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Session(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("assigned_time"),
                            rs.getInt("completed_time"),
                            rs.getInt("parent_task_id")
                    );
                }
            }
        }
        return null;
    }

    // Update a session
    public void update(Session session) throws SQLException {
        String sql = "UPDATE sessions SET name = ?, assigned_time = ?, completed_time = ?, parent_task_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, session.getName());
            stmt.setInt(2, session.getAssignedTime());
            stmt.setInt(3, session.getCompletedTime());
            stmt.setInt(4, session.getParentTaskId());
            stmt.setInt(5, session.getId());
            stmt.executeUpdate();
        }
    }

    // Delete a session by ID
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM sessions WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
