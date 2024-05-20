package com.synthwave.timetracker;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class SessionTreeCellRenderer extends DefaultTreeCellRenderer {
    private final Icon toDoIcon = UIManager.getIcon("OptionPane.informationIcon");
    private final Icon inProgressIcon = UIManager.getIcon("OptionPane.warningIcon");
    private final Icon doneIcon = UIManager.getIcon("OptionPane.questionIcon");

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (userObject instanceof Session) {
            Session session = (Session) userObject;
            String displayText = session.getName() + " (" + session.getFormattedRemainingTime() + ")";
            setText(displayText);
            setIcon(null);  // clear any previous icon
        } else if (userObject instanceof Task) {
            Task task = (Task) userObject;
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
            }
        }
        return c;
    }
}
