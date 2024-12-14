package com.synthwave.timetracker.state;

public class PomodoroState {
    private int sessionId;      // ID of the session being tracked.
    private int remainingTime;  // Remaining time in seconds.

    // Constructors
    public PomodoroState() {}

    public PomodoroState(int sessionId, int remainingTime) {
        this.sessionId = sessionId;
        this.remainingTime = remainingTime;
    }

    // Getters and Setters
    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
