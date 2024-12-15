package com.synthwave.timetracker.model;

import com.synthwave.timetracker.RuntimeSession; // Import if you still need this reference and RuntimeSession is accessible
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A unified Task class that incorporates all features:
 * - Serializable for drag-and-drop.
 * - id, name, state, notes (from model.Task).
 * - subtasks and sessions (from old Task).
 * Make sure RuntimeSession is also serializable or remove sessions if not needed.
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String state;  // e.g., "To-Do", "In-Progress", "Done"
    private String notes;

    // From old Task:
    private List<RuntimeSession> sessions;
    private List<Task> subtasks;

    // No-arg constructor needed for frameworks and serialization
    public Task() {
        this.sessions = new ArrayList<>();
        this.subtasks = new ArrayList<>();
    }

    // Full constructor (from model)
    public Task(int id, String name, String state, String notes) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.notes = notes;
        this.sessions = new ArrayList<>();
        this.subtasks = new ArrayList<>();
    }

    // Convenience constructor for (int, String, String) from model
    public Task(int id, String name, String state) {
        this(id, name, state, null);
    }

    // Convenience constructor for (String, String) from model
    public Task(String name, String state) {
        this(0, name, state, null);
    }

    // Constructor matching old usage (int, String, String) is covered by above constructors

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<RuntimeSession> getSessions() {
        return sessions;
    }

    public void addSession(RuntimeSession session) {
        this.sessions.add(session);
    }

    public List<Task> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Task subtask) {
        this.subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", name='" + name + "', state='" + state + "', notes='" + notes + "'}";
    }
}
