package com.synthwave.timetracker;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class SessionTreeCellRenderer extends DefaultTreeCellRenderer {
    private final Icon toDoIcon = UIManager.getIcon("OptionPane.informationIcon");
    private final Icon inProgressIcon = UIManager.getIcon("OptionPane.warningIcon");
    private final Icon doneIcon = UIManager.getIcon("OptionPane.questionIcon");

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // Adjust to use RuntimeSession from SessionNode
        if (value instanceof SessionNode) {
            SessionNode sessionNode = (SessionNode) value;
            RuntimeSession runtimeSession = sessionNode.getRuntimeSession();
            setText(runtimeSession.getName() + " (" + runtimeSession.getFormattedRemainingTime() + ")");
            setIcon(null); // You can set a session-specific icon if you like

        } else if (value instanceof TaskNode) {
            TaskNode taskNode = (TaskNode) value;
            Task task = taskNode.getTask();
            setText(task.getName());

            switch (task.getState()) {
                case "To-Do":
                    setIcon(toDoIcon);
                    break;
                case "In-Progress":
                    setIcon(inProgressIcon);
                    break;
                case "Done":
                    setIcon(doneIcon);
                    break;
                default:
                    setIcon(null);
                    break;
            }
        }

        return this;
    }
}
