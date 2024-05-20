package com.synthwave.timetracker;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class SessionPanel extends GradientPanel {
    private DefaultListModel<Session> sessionListModel;
    private JTree sessionTree;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;

    public SessionPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));

        JLabel sessionLabel = new JLabel("Sessions");
        sessionLabel.setForeground(Color.BLACK);
        sessionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(sessionLabel, BorderLayout.NORTH);

        root = new DefaultMutableTreeNode("Sessions");
        treeModel = new DefaultTreeModel(root);
        sessionTree = new JTree(treeModel);
        sessionTree.setCellRenderer(new SessionTreeCellRenderer());
        sessionTree.setDragEnabled(true);
        sessionTree.setDropMode(DropMode.ON);
        sessionTree.setTransferHandler(new SessionTransferHandler());

        JScrollPane sessionScrollPane = new JScrollPane(sessionTree);
        add(sessionScrollPane, BorderLayout.CENTER);

        JPanel addSessionPanel = new JPanel(new BorderLayout());
        GradientLabel addSessionLabel = new GradientLabel("Add Session");
        addSessionPanel.add(addSessionLabel, BorderLayout.NORTH);

        JTextField sessionNameField = new JTextField();
        sessionNameField.setPreferredSize(new Dimension(0, 30)); // Set height for input fields
        addSessionPanel.add(sessionNameField, BorderLayout.CENTER);

        JTextField sessionDurationField = new JTextField();
        sessionDurationField.setPreferredSize(new Dimension(50, 30)); // Set height for input fields
        addSessionPanel.add(sessionDurationField, BorderLayout.EAST);

        addSessionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addSession(sessionNameField, sessionDurationField);
            }
        });

        sessionNameField.addActionListener(e -> addSession(sessionNameField, sessionDurationField));
        sessionDurationField.addActionListener(e -> addSession(sessionNameField, sessionDurationField));

        add(addSessionPanel, BorderLayout.SOUTH);
    }

    private void addSession(JTextField sessionNameField, JTextField sessionDurationField) {
        String sessionName = sessionNameField.getText().trim();
        String durationStr = sessionDurationField.getText().trim();
        if (!sessionName.isEmpty() && !durationStr.isEmpty()) {
            try {
                int duration = Integer.parseInt(durationStr);
                Session session = new Session(sessionName, duration);
                DefaultMutableTreeNode sessionNode = new DefaultMutableTreeNode(session);
                root.add(sessionNode);
                treeModel.reload();
                sessionNameField.setText("");
                sessionDurationField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid duration entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Session getSelectedSession() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
        if (node == null) {
            return null;
        }
        Object userObject = node.getUserObject();
        if (userObject instanceof Session) {
            return (Session) userObject;
        }
        return null;
    }

    public void updateTaskNode(Task task) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        updateTaskNodeRecursive(rootNode, task);
    }

    private void updateTaskNodeRecursive(DefaultMutableTreeNode node, Task task) {
        if (node.getUserObject() instanceof Task) {
            Task nodeTask = (Task) node.getUserObject();
            if (nodeTask.getName().equals(task.getName())) {
                treeModel.nodeChanged(node);
                return;
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            updateTaskNodeRecursive(childNode, task);
        }
    }

    public void updateSessionDisplay() {
        treeModel.reload();
        sessionTree.repaint();
    }

    // Refresh the tree to reflect the latest tasks and their statuses
    public void refreshTree() {
        List<DefaultMutableTreeNode> sessionNodes = getSessionNodes();
        for (DefaultMutableTreeNode sessionNode : sessionNodes) {
            Session session = (Session) sessionNode.getUserObject();
            sessionNode.removeAllChildren();
            for (Task task : session.getTasks()) {
                DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(task);
                sessionNode.add(taskNode);
            }
        }
        treeModel.reload();
    }

    private List<DefaultMutableTreeNode> getSessionNodes() {
        int childCount = root.getChildCount();
        List<DefaultMutableTreeNode> sessionNodes = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            sessionNodes.add((DefaultMutableTreeNode) root.getChildAt(i));
        }
        return sessionNodes;
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getForeground());
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = this.radius + 1;
            insets.right = this.radius + 1;
            insets.top = this.radius + 2;
            insets.bottom = this.radius;
            return insets;
        }
    }

    private static class SessionTreeCellRenderer extends DefaultTreeCellRenderer {
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
}
