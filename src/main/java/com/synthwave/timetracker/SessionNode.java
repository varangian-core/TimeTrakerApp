package com.synthwave.timetracker;

import javax.swing.tree.DefaultMutableTreeNode;

public class SessionNode extends DefaultMutableTreeNode {
  private final RuntimeSession runtimeSession;

  public SessionNode(RuntimeSession runtimeSession) {
    this.runtimeSession = runtimeSession;
  }

  public RuntimeSession getRuntimeSession() {
    return runtimeSession;
  }

  @Override
  public String toString() {
    // Ensure runtimeSession has the necessary methods: getName(), getFormattedRemainingTime()
    return runtimeSession.getName() + " (" + runtimeSession.getFormattedRemainingTime() + ")";
  }
}
