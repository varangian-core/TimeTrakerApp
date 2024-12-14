package com.synthwave.timetracker.state;

import com.synthwave.timetracker.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PomodoroStateManager {

    public void saveState(PomodoroState state) throws Exception {
        String sql = "MERGE INTO pomodoro_state (id, session_id, remaining_time) VALUES (1, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, state.getSessionId());
            stmt.setInt(2, state.getRemainingTime());
            stmt.executeUpdate();
        }
    }

    public PomodoroState loadState(int sessionId) throws Exception {
        String sql = "SELECT remaining_time FROM pomodoro_state WHERE session_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int remainingTime = rs.getInt("remaining_time");
                    return new PomodoroState(sessionId, remainingTime);
                }
            }
        }
        return null;
    }

    public void deleteState(int sessionId) throws Exception {
        String sql = "DELETE FROM pomodoro_state WHERE session_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.executeUpdate();
        }
    }
}
