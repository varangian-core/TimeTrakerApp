package com.synthwave.timetracker;

import com.synthwave.timetracker.model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RuntimeSession implements Serializable {
    private final UUID sessionId; // Unique ID for this session
    private String name;
    private int duration; // in minutes
    private int remainingTime; // in seconds
    private List<Task> tasks;

    public RuntimeSession(String name, int duration) {
        this.sessionId = UUID.randomUUID(); // Generate a unique ID at creation
        this.name = name;
        this.duration = duration;
        this.remainingTime = duration * 60; // Convert duration to seconds
        this.tasks = new ArrayList<>();
    }

    public UUID getId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public String getFormattedRemainingTime() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return name;
    }
}
