package com.synthwave.timetracker.state;

public class PomodoroState {
    private String sessionKey;  // String instead of int
    private int remainingTime;

    public PomodoroState(String sessionKey, int remainingTime) {
        this.sessionKey = sessionKey;
        this.remainingTime = remainingTime;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
