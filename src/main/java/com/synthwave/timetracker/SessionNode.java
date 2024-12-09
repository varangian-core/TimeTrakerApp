package com.synthwave.timetracker;

import javax.swing.tree.DefaultMutableTreeNode;

public class SessionNode extends DefaultMutableTreeNode {
  private final Session session;

  public SessionNode(Session session) {
    this.session = session;
  }

  public Session getSession() {
    return session;
  }

  @Override
  public String toString() {
    return session.getName() + " (" + session.getFormattedRemainingTime() + ")";
  }
}

