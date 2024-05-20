package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class PomodoroTimer {
    private Timer timer;
    private int timeRemaining; // in seconds
    private boolean isRunning;
    private JLabel timerLabel;
    private SessionPanel sessionPanel;

    public PomodoroTimer(JLabel timerLabel, SessionPanel sessionPanel) {
        this.timerLabel = timerLabel;
        this.sessionPanel = sessionPanel;
        this.isRunning = false;
        this.timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    updateDisplay();
                } else {
                    timer.stop();
                    isRunning = false;
                    JOptionPane.showMessageDialog(null, "Time's up!");
                    updateSessionTime();
                }
            }
        });
    }

    public void start() {
        if (!isRunning) {
            Session selectedSession = sessionPanel.getSelectedSession();
            if (selectedSession != null) {
                timeRemaining = selectedSession.getRemainingTime();
                updateDisplay();
                timer.start();
                isRunning = true;
            } else {
                JOptionPane.showMessageDialog(null, "No session selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void pause() {
        if (isRunning) {
            timer.stop();
            isRunning = false;
        }
    }

    public void stop() {
        if (isRunning) {
            timer.stop();
            isRunning = false;
            updateSessionTime();
        }
    }

    public void reset() {
        timer.stop();
        isRunning = false;
        timeRemaining = 0;
        updateDisplay();
    }

    private void updateDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateSessionTime() {
        Session selectedSession = sessionPanel.getSelectedSession();
        if (selectedSession != null) {
            selectedSession.setRemainingTime(timeRemaining);
            sessionPanel.updateSessionDisplay();
        }
    }
}
