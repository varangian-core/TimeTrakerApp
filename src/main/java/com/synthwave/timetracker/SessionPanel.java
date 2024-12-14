package com.synthwave.timetracker;

import com.synthwave.timetracker.model.Session;
import com.synthwave.timetracker.model.Task;
import com.synthwave.timetracker.dao.SessionDao;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

class SessionPanel extends GradientPanel implements ThemedComponent {
    private JTree sessionTree;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;
    private JLabel sessionLabel;
    private JTextField sessionNameField;
    private JTextField sessionDurationField;
    private GradientLabel addSessionLabel;

    private Theme currentTheme = ThemeManager.getTheme();
    private Consumer<RuntimeSession> selectedSessionListener;

    private SessionDao sessionDao; // DAO for database operations

    public SessionPanel(SessionDao sessionDao) {
        this.sessionDao = sessionDao;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));

        sessionLabel = new JLabel("Sessions");
        sessionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(sessionLabel, BorderLayout.NORTH);

        root = new DefaultMutableTreeNode("Sessions");
        treeModel = new DefaultTreeModel(root);
        sessionTree = new JTree(treeModel);
        sessionTree.setRowHeight(24);

        sessionTree.addTreeSelectionListener(e -> {
            RuntimeSession selected = getSelectedSession();
            if (selectedSessionListener != null) {
                selectedSessionListener.accept(selected);
            }
        });

        JScrollPane sessionScrollPane = new JScrollPane(sessionTree);
        add(sessionScrollPane, BorderLayout.CENTER);

        JPanel addSessionPanel = new JPanel(new BorderLayout());
        addSessionLabel = new GradientLabel("Add Session");
        addSessionPanel.add(addSessionLabel, BorderLayout.NORTH);

        sessionNameField = new JTextField();
        sessionDurationField = new JTextField();

        addSessionPanel.add(sessionNameField, BorderLayout.CENTER);
        addSessionPanel.add(sessionDurationField, BorderLayout.EAST);

        addSessionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addSession(sessionNameField, sessionDurationField);
            }
        });

        sessionNameField.addActionListener(e -> addSession(sessionNameField, sessionDurationField));
        sessionDurationField.addActionListener(e -> addSession(sessionNameField, sessionDurationField));

        add(addSessionPanel, BorderLayout.SOUTH);

        // Load sessions from the database
        loadSessionsFromDatabase();

        ThemeManager.register(this);
        applyTheme(currentTheme);
    }

    private void addSession(JTextField sessionNameField, JTextField sessionDurationField) {
        String sessionName = sessionNameField.getText().trim();
        String durationStr = sessionDurationField.getText().trim();

        if (!sessionName.isEmpty() && !durationStr.isEmpty()) {
            try {
                int duration = Integer.parseInt(durationStr);
                RuntimeSession runtimeSession = new RuntimeSession(sessionName, duration);
                DefaultMutableTreeNode sessionNode = new DefaultMutableTreeNode(runtimeSession);
                root.add(sessionNode);
                treeModel.reload();

                // Save to database
                Session session = new Session(0, sessionName, duration * 60, 0, 0);
                sessionDao.insert(session);

                sessionNameField.setText("");
                sessionDurationField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid duration entered.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to save session.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter both session name and duration.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSessionsFromDatabase() {
        try {
            List<Session> sessions = sessionDao.getAll();
            for (Session session : sessions) {
                RuntimeSession runtimeSession = new RuntimeSession(session.getName(), session.getAssignedTime() / 60);
                runtimeSession.setRemainingTime(session.getAssignedTime() - session.getCompletedTime());
                // If you have logic to load tasks for each session, do it here and call runtimeSession.addTask(t) for each Task
                DefaultMutableTreeNode sessionNode = new DefaultMutableTreeNode(runtimeSession);

                // Add tasks as children if you have them:
                for (Task t : runtimeSession.getTasks()) {
                    sessionNode.add(new DefaultMutableTreeNode(t));
                }

                root.add(sessionNode);
            }
            treeModel.reload();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to load sessions from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public RuntimeSession getSelectedSession() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
        if (node == null) {
            return null;
        }
        Object userObject = node.getUserObject();
        if (userObject instanceof RuntimeSession) {
            return (RuntimeSession) userObject;
        }
        return null;
    }

    public void setSelectedSessionListener(Consumer<RuntimeSession> listener) {
        this.selectedSessionListener = listener;
    }

    /**
     * Refresh the entire session display tree. This could be as simple as reloading the model.
     * Call this if you need to reflect global changes.
     */
    public void updateSessionDisplay() {
        treeModel.reload();
    }

    /**
     * Update the tree to reflect changes to a specific task.
     * This finds the node that contains a RuntimeSession with the given task, then rebuilds its children.
     */
    public void updateTaskNode(Task updatedTask) {
        DefaultMutableTreeNode sessionNode = findSessionNodeForTask((DefaultMutableTreeNode) root, updatedTask);
        if (sessionNode != null) {
            sessionNode.removeAllChildren();
            RuntimeSession rs = (RuntimeSession) sessionNode.getUserObject();
            for (Task t : rs.getTasks()) {
                sessionNode.add(new DefaultMutableTreeNode(t));
            }
            treeModel.reload(sessionNode);
        }
    }

    private DefaultMutableTreeNode findSessionNodeForTask(DefaultMutableTreeNode node, Task task) {
        Object userObject = node.getUserObject();
        if (userObject instanceof RuntimeSession) {
            RuntimeSession rs = (RuntimeSession) userObject;
            if (rs.getTasks().contains(task)) {
                return node;
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode result = findSessionNodeForTask(childNode, task);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        setBackground(Color.LIGHT_GRAY); // Example of applying the theme
    }
}
