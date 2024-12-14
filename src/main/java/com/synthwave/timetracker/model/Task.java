package com.synthwave.timetracker.model;

public class Task {
    //TODO: change to enums
    private int id;
    private String name;
    private String state;  // e.g., "Pending", "InProgress", "Completed"
    private String notes;

    // No-arg constructor
    public Task() {}

    // Full constructor
    public Task(int id, String name, String state, String notes) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.notes = notes;
    }

    // Convenience constructor for Task(int, String, String)
    public Task(int id, String name, String state) {
        this(id, name, state, null);
    }

    // Convenience constructor for Task(String, String)
    public Task(String name, String state) {
        this(0, name, state, null);
    }

    // Getters and Setters
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
}
