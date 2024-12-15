package com.synthwave.timetracker;

import com.synthwave.timetracker.model.Task;

import javax.swing.tree.DefaultMutableTreeNode;

public class TaskNode extends DefaultMutableTreeNode {
  private final Task task;

  public TaskNode(Task task) {
    this.task = task;

    // Recursively add nodes for subtasks
    for (Task subtask : task.getSubtasks()) {
      add(new TaskNode(subtask));
    }
  }

  public Task getTask() {
    return task;
  }

  @Override
  public String toString() {
    return task.getName();
  }
}
