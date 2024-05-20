package com.synthwave.timetracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Task implements Serializable {
    private String name;
    private String state; // e.g., "To-Do", "In-Progress", "Done"
    private List<Session> sessions;

    public Task(String name, String state) {
        this.name = name;
        this.state = state;
        this.sessions = new ArrayList<>();
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

    @Override
    public String toString() {
        return name;
    }
}
