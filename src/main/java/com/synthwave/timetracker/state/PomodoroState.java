package com.synthwave.timetracker.state;

public class PomodoroState {
    private int sessionId;
    private int remainingTime;

    public PomodoroState() {}

    public PomodoroState(int sessionId, int remainingTime) {
        this.sessionId = sessionId;
        this.remainingTime = remainingTime;
    }

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
