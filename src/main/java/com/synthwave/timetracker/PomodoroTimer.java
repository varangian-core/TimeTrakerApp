package com.synthwave.timetracker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PomodoroTimer {
    private Timer timer;
    private int remainingTime; // in seconds
    private int totalTime;     // total in seconds
    private JLabel timerLabel;
    private SessionPanel sessionPanel;
    private TimerPanel timerPanel;
    private Session currentSession;

    public PomodoroTimer(JLabel timerLabel, SessionPanel sessionPanel, TimerPanel timerPanel, Session currentSession) {
        this.timerLabel = timerLabel;
        this.sessionPanel = sessionPanel;
        this.timerPanel = timerPanel;
        this.currentSession = currentSession;

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
                    // handle completion
                }
            }
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void updateDisplay() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        float fraction = (float)(totalTime - remainingTime) / (float)totalTime;
        timerPanel.updateTimerProgress(fraction);

        sessionPanel.updateSessionDisplay();
    }
}
