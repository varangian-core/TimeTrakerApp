package com.synthwave.timetracker;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.function.Consumer;
import com.synthwave.timetracker.model.Task;


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

    public SessionPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));

        sessionLabel = new JLabel("Sessions");
        sessionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(sessionLabel, BorderLayout.NORTH);

        root = new DefaultMutableTreeNode("Sessions");
        treeModel = new DefaultTreeModel(root);
        sessionTree = new JTree(treeModel);
        sessionTree.setRowHeight(24);

        SessionTreeCellRenderer renderer = new SessionTreeCellRenderer();
        sessionTree.setCellRenderer(renderer);

        sessionTree.setDragEnabled(true);
        sessionTree.setDropMode(DropMode.ON);
        sessionTree.setTransferHandler(new SessionTransferHandler());

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

        addSessionPanel.setToolTipText("<html><b>Instructions:</b><br>"
                + "1. Enter a session name and duration, then click 'Add Session' or press Enter.<br>"
                + "2. Once sessions are created, you can select them and the TimerPanel will update accordingly.<br>"
                + "3. You can drag tasks onto a session to assign them.</html>");

        sessionNameField = new JTextField();
        sessionNameField.setPreferredSize(new Dimension(0, 30));
        addSessionPanel.add(sessionNameField, BorderLayout.CENTER);

        sessionDurationField = new JTextField();
        sessionDurationField.setPreferredSize(new Dimension(50, 30));
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

        ThemeManager.register(this);
        applyTheme(currentTheme);
    }

    public void setSelectedSessionListener(Consumer<RuntimeSession> listener) {
        this.selectedSessionListener = listener;
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
                sessionNameField.setText("");
                sessionDurationField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid duration entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter both session name and duration.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public void updateSessionDisplay() {
        treeModel.reload();
        sessionTree.repaint();
    }

    public void updateTaskNode(Task updatedTask) {
        // Reload the tree model to reflect changes in tasks (icons, state, etc.)
        treeModel.reload();
        sessionTree.repaint();
    }

    @Override
    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        Color background;
        Color foreground;
        Color panelBackground;

        switch (theme) {
            case LIGHT:
                background = Color.WHITE;
                foreground = Color.BLACK;
                panelBackground = Color.LIGHT_GRAY;
                break;
            case DARK:
                background = new Color(45,45,45);
                foreground = Color.WHITE;
                panelBackground = new Color(60,60,60);
                break;
            case SYNTHWAVE:
                background = new Color(40,0,40);
                foreground = Color.MAGENTA;
                panelBackground = new Color(20,0,20);
                break;
            default:
                background = Color.WHITE;
                foreground = Color.BLACK;
                panelBackground = Color.LIGHT_GRAY;
        }

        setBackground(panelBackground);
        sessionLabel.setForeground(foreground);

        sessionTree.setBackground(background);
        sessionTree.setForeground(foreground);

        sessionNameField.setBackground(background);
        sessionNameField.setForeground(foreground);

        sessionDurationField.setBackground(background);
        sessionDurationField.setForeground(foreground);

        addSessionLabel.setForeground(foreground);
        addSessionLabel.setBackground(panelBackground);

        sessionTree.repaint();
        repaint();
    }
}
