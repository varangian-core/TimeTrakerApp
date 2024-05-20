package com.synthwave.timetracker;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class SessionTaskTreeModel extends DefaultTreeModel {
    public SessionTaskTreeModel(DefaultMutableTreeNode root) {
        super(root);
    }

    public void addTaskToSession(Session session, Task task) {
        DefaultMutableTreeNode sessionNode = findSessionNode((DefaultMutableTreeNode) root, session);
        if (sessionNode != null) {
            sessionNode.add(new DefaultMutableTreeNode(task));
            reload(sessionNode);
        }
    }

    private DefaultMutableTreeNode findSessionNode(DefaultMutableTreeNode node, Session session) {
        if (node.getUserObject() instanceof Session && node.getUserObject().equals(session)) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode result = findSessionNode(childNode, session);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
