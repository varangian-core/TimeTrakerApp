package com.synthwave.timetracker.dao;

import com.synthwave.timetracker.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PomodoroStateDao {
    public void createTable() throws SQLException {
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
