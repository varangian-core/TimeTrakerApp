package com.synthwave.timetracker;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class SessionTaskTreeModel extends DefaultTreeModel {

    public SessionTaskTreeModel(DefaultMutableTreeNode root) {
        super(root);
    }

    // Adjust the method signature to accept a RuntimeSession instead of Session
    public void addTaskToSession(RuntimeSession runtimeSession, Task task) {
        DefaultMutableTreeNode sessionNode = findSessionNode((DefaultMutableTreeNode) getRoot(), runtimeSession);
        if (sessionNode != null) {
            sessionNode.add(new DefaultMutableTreeNode(task));
            reload(sessionNode);
        }
    }

    private DefaultMutableTreeNode findSessionNode(DefaultMutableTreeNode node, RuntimeSession runtimeSession) {
        Object userObject = node.getUserObject();
        if (userObject instanceof RuntimeSession && userObject.equals(runtimeSession)) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode result = findSessionNode(childNode, runtimeSession);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
