package com.synthwave.timetracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Task implements Serializable {
    private int id;
    private String name;
    private String state; // e.g., "To-Do", "In-Progress", "Done"
    private List<RuntimeSession> sessions; // Use RuntimeSession for runtime operations
    private List<Task> subtasks;

    // Constructor that matches the original usage (int, String, String)
    public Task(int id, String name, String state) {
        this(name, state);
        this.id = id;
    }

    // Existing constructor
    public Task(String name, String state) {
        this.name = name;
        this.state = state;
        this.sessions = new ArrayList<>();
        this.subtasks = new ArrayList<>();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void addSession(RuntimeSession session) {
        this.sessions.add(session);
    }

    public List<RuntimeSession> getSessions() {
        return sessions;
    }

    public List<Task> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Task subtask) {
        this.subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", name='" + name + "', state='" + state + "'}";
    }
}
