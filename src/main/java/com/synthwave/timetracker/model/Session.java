package com.synthwave.timetracker.model;

public class Session {
    private int id;
    private String name;
    private int assignedTime;  // Assigned time in seconds.
    private int completedTime; // Completed time in seconds.
    private int parentTaskId;  // Task ID that this session belongs to (can be null).

    // Constructors
    public Session() {}

    public Session(int id, String name, int assignedTime, int completedTime, int parentTaskId) {
        this.id = id;
        this.name = name;
        this.assignedTime = assignedTime;
        this.completedTime = completedTime;
        this.parentTaskId = parentTaskId;
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

    public int getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(int assignedTime) {
        this.assignedTime = assignedTime;
    }

    public int getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(int completedTime) {
        this.completedTime = completedTime;
    }

    public int getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(int parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
}
