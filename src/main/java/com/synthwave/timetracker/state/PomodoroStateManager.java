package com.synthwave.timetracker.state;

import com.synthwave.timetracker.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PomodoroStateManager {

    public void saveState(PomodoroState state) throws Exception {
        // Use MERGE or INSERT depending on your DB. Assuming H2 with MERGE:
        // session_key is now VARCHAR, not int
        String sql = "MERGE INTO pomodoro_state (session_key, remaining_time) KEY(session_key) VALUES (?, ?)\n";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, state.getSessionKey());
            stmt.setInt(2, state.getRemainingTime());
            stmt.executeUpdate();
        }
    }

    public PomodoroState loadState(String sessionKey) throws Exception {
        String sql = "SELECT remaining_time FROM pomodoro_state WHERE session_key = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionKey);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int remainingTime = rs.getInt("remaining_time");
                    return new PomodoroState(sessionKey, remainingTime);
                }
            }
        }
        return null;
    }

    public void deleteState(String sessionKey) throws Exception {
        String sql = "DELETE FROM pomodoro_state WHERE session_key = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionKey);
            stmt.executeUpdate();
        }
    }
}
