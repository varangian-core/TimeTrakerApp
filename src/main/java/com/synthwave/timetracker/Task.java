package com.synthwave.timetracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Task implements Serializable {
    private String name;
    private String state; // e.g., "To-Do", "In-Progress", "Done"
    private List<Session> sessions;
    private List<Task> subtasks; // New field for subtasks

    public Task(String name, String state) {
        this.name = name;
        this.state = state;
        this.sessions = new ArrayList<>();
        this.subtasks = new ArrayList<>(); // Initialize subtasks list
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addSession(Session session) {
        this.sessions.add(session);
    }

    public List<Session> getSessions() {
        return sessions;
    }

    // New methods for subtasks
    public List<Task> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Task subtask) {
        this.subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return name;
    }
}