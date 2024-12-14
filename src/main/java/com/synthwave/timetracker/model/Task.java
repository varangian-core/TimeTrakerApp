package com.synthwave.timetracker.model;

public class Task {
    //TODO: change to enums
    private int id;
    private String name;
    private String state;  // e.g., "Pending", "InProgress", "Completed"
    private String notes;

    // Constructors
    public Task() {}

    public Task(int id, String name, String state, String notes) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.notes = notes;
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
