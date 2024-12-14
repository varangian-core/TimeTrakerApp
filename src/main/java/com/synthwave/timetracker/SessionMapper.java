package com.synthwave.timetracker;

import com.synthwave.timetracker.model.Session;

public class SessionMapper {

    // Convert RuntimeSession to Persistent Session
    public static Session toPersistentSession(RuntimeSession runtimeSession) {
        if (runtimeSession == null) return null;

        Session persistentSession = new Session();
        persistentSession.setName(runtimeSession.getName());
        persistentSession.setAssignedTime(runtimeSession.getDuration() * 60); // Convert minutes to seconds
        persistentSession.setCompletedTime(runtimeSession.getDuration() * 60 - runtimeSession.getRemainingTime());
        // If taskId is relevant, set it here: persistentSession.setTaskId(someTaskId);
        return persistentSession;
    }

    // Convert Persistent Session to RuntimeSession
    public static RuntimeSession toRuntimeSession(Session persistentSession) {
        if (persistentSession == null) return null;

        RuntimeSession runtimeSession = new RuntimeSession(
                persistentSession.getName(),
                persistentSession.getAssignedTime() / 60 // Convert seconds to minutes
        );
        runtimeSession.setRemainingTime(
                persistentSession.getAssignedTime() - persistentSession.getCompletedTime()
        );
        return runtimeSession;
    }
}
