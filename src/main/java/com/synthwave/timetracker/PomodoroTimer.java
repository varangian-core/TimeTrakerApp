package com.synthwave.timetracker;

import com.synthwave.timetracker.model.Session; // No longer needed if we remove references to persistent Session.
import com.synthwave.timetracker.state.PomodoroState;
import com.synthwave.timetracker.state.PomodoroStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PomodoroTimer {
    private static final Logger LOGGER = Logger.getLogger(PomodoroTimer.class.getName());

    private Timer timer;
    private int remainingTime; // in seconds
    private int totalTime;     // total in seconds
    private JLabel timerLabel;
    private SessionPanel sessionPanel;
    private TimerPanel timerPanel;
    private RuntimeSession currentSession;

    private PomodoroStateManager stateManager;

    public PomodoroTimer(JLabel timerLabel, SessionPanel sessionPanel, TimerPanel timerPanel, RuntimeSession currentSession) {
        if (currentSession == null) {
            throw new IllegalArgumentException("Current session cannot be null");
        }
        this.timerLabel = timerLabel;
        this.sessionPanel = sessionPanel;
        this.timerPanel = timerPanel;
        this.currentSession = currentSession;

        this.stateManager = new PomodoroStateManager();

        try {
            loadState();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load Pomodoro Timer state", e);
        }

        this.totalTime = currentSession.getDuration() * 60;
        this.remainingTime = currentSession.getRemainingTime();

        updateDisplay();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingTime > 0) {
                    remainingTime--;
                    currentSession.setRemainingTime(remainingTime);
                    updateDisplay();
                } else {
                    timer.stop();
                    handleCompletion();
                }
            }
        });
    }

    public void start() {
        try {
            timer.start();
            saveState();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start Pomodoro Timer", e);
        }
    }

    public void stop() {
        try {
            timer.stop();
            saveState();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to stop Pomodoro Timer", e);
        }
    }

    private void updateDisplay() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        float fraction = (float) (totalTime - remainingTime) / (float) totalTime;
        timerPanel.updateTimerProgress(fraction);

        sessionPanel.updateSessionDisplay();
    }

    private void handleCompletion() {
        LOGGER.info("Pomodoro session completed!");
        try {
            String sessionKey = getSessionKey();
            stateManager.deleteState(sessionKey);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to clear Pomodoro state on completion", e);
        }
    }

    private void saveState() throws Exception {
        String sessionKey = getSessionKey();
        if (sessionKey == null || sessionKey.isEmpty()) {
            LOGGER.warning("Session ID (UUID) is invalid. Cannot save state.");
            return;
        }

        PomodoroState state = new PomodoroState(sessionKey, remainingTime);
        stateManager.saveState(state);
        LOGGER.info("Pomodoro Timer state saved successfully.");
    }

    private void loadState() throws Exception {
        String sessionKey = getSessionKey();
        if (sessionKey == null || sessionKey.isEmpty()) {
            LOGGER.warning("Session ID (UUID) is invalid. Cannot load state.");
            return;
        }

        PomodoroState state = stateManager.loadState(sessionKey);
        if (state != null) {
            remainingTime = state.getRemainingTime();
            LOGGER.info("Pomodoro Timer state loaded successfully.");
        }
    }

    private String getSessionKey() {
        UUID id = currentSession.getId();
        return (id != null) ? id.toString() : null;
    }
}
