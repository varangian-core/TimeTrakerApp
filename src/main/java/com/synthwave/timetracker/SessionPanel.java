package com.synthwave.timetracker;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class SessionPanel extends GradientPanel implements ThemedComponent {
    private JTree sessionTree;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;

    private JLabel sessionLabel;
    private JTextField sessionNameField;
    private JTextField sessionDurationField;
    private GradientLabel addSessionLabel;

    private Theme currentTheme = ThemeManager.getTheme();
    private Consumer<Session> selectedSessionListener; // Listener for session selection

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

        // Add selection listener to notify others when a session is selected
        sessionTree.addTreeSelectionListener(e -> {
            Session selected = getSelectedSession();
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
            + "2. Once sessions are created, you can select them and the TimerPanel will update accordingly.</html>");

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

    /**
     * Set a listener to be notified whenever a session is selected.
     */
    public void setSelectedSessionListener(Consumer<Session> listener) {
        this.selectedSessionListener = listener;
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
        } else {
            JOptionPane.showMessageDialog(null, "Please enter both session name and duration.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public void updateSessionDisplay() {
        treeModel.reload();
        sessionTree.repaint();
    }

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

    public void updateTaskNode(Task task) {
        SessionTreeCellRenderer renderer = (SessionTreeCellRenderer) sessionTree.getCellRenderer();
        renderer.clearCachedIconForTask(task);

        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        updateTaskNodeRecursive(rootNode, task);
    }

    private void updateTaskNodeRecursive(DefaultMutableTreeNode node, Task task) {
        Object userObject = node.getUserObject();
        if (userObject instanceof Task) {
            Task nodeTask = (Task) userObject;
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

    @Override
    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        Color background;
        Color foreground;
        Color panelBackground;
        Color borderColor;

        switch (theme) {
            case LIGHT:
                background = Color.WHITE;
                foreground = Color.BLACK;
                panelBackground = Color.LIGHT_GRAY;
                borderColor = foreground;
                break;
            case DARK:
                background = new Color(45, 45, 45);
                foreground = Color.WHITE;
                panelBackground = new Color(60, 60, 60);
                borderColor = foreground;
                break;
            case SYNTHWAVE:
                background = new Color(40,0,40);
                foreground = Color.MAGENTA;
                panelBackground = new Color(20,0,20);
                borderColor = new Color(255, 105, 180);
                break;
            default:
                background = Color.WHITE;
                foreground = Color.BLACK;
                panelBackground = Color.LIGHT_GRAY;
                borderColor = foreground;
        }

        setBackground(panelBackground);
        sessionLabel.setForeground(foreground);

        sessionTree.setBackground(background);
        sessionTree.setForeground(foreground);

        sessionNameField.setBackground(background);
        sessionNameField.setForeground(borderColor);

        sessionDurationField.setBackground(background);
        sessionDurationField.setForeground(borderColor);

        addSessionLabel.setForeground(foreground);
        addSessionLabel.setBackground(panelBackground);

        sessionTree.repaint();
        repaint();
    }

    static class SessionCellPanel extends JPanel {
        JLabel label;
        SessionProgressBar progressBar;

        public SessionCellPanel(Theme theme) {
            super(new FlowLayout(FlowLayout.LEFT, 5, 0));
            setOpaque(false);
            label = new JLabel();
            progressBar = new SessionProgressBar();
            progressBar.setPreferredSize(new Dimension(200, 20));
            add(label);
            add(progressBar);
        }

        public void applyTheme(Theme theme, Color background, Color foreground, boolean selected) {
            setBackground(selected ? UIManager.getColor("Tree.selectionBackground") : background);
            if (theme == Theme.LIGHT && selected) {
                label.setForeground(Color.BLACK);
            } else {
                label.setForeground(foreground);
            }
        }
    }

    private class SessionTreeCellRenderer extends DefaultTreeCellRenderer {
        private Icon[] todoIcons;
        private Icon[] inProgressIcons;
        private Icon[] doneIcons;
        private final Map<Task, Integer> chosenIconsForTasks = new HashMap<>();

        public SessionTreeCellRenderer() {
            loadIcons();
        }

        private void loadIcons() {
            todoIcons = new Icon[] {
                loadAndScaleIcon("todo-1.png", 20, 20),
                loadAndScaleIcon("todo-2.png", 20, 20)
            };

            inProgressIcons = new Icon[] {
                loadAndScaleIcon("inprogress-1.png", 20, 20),
                loadAndScaleIcon("inprogress-2.png", 20, 20)
            };

            doneIcons = new Icon[] {
                loadAndScaleIcon("done-1.png", 20, 20),
                loadAndScaleIcon("done-2.png", 20, 20)
            };
        }

        private Icon loadAndScaleIcon(String resourceName, int w, int h) {
            String fullPath = "/com/synthwave/timetracker/" + resourceName;
            try (InputStream is = getClass().getResourceAsStream(fullPath)) {
                if (is == null) {
                    return new ImageIcon(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
                }
                BufferedImage original = ImageIO.read(is);
                Image scaled = original.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            } catch (IOException e) {
                return new ImageIcon(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
            }
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();

            Theme theme = ThemeManager.getTheme();
            Color background;
            Color foreground;
            switch (theme) {
                case LIGHT:
                    background = Color.WHITE;
                    foreground = Color.BLACK;
                    break;
                case DARK:
                    background = new Color(45,45,45);
                    foreground = Color.WHITE;
                    break;
                case SYNTHWAVE:
                    background = new Color(40,0,40);
                    foreground = Color.MAGENTA;
                    break;
                default:
                    background = Color.WHITE;
                    foreground = Color.BLACK;
            }

            if (userObject instanceof Session) {
                Session session = (Session) userObject;
                SessionCellPanel panel = new SessionCellPanel(theme);

                int total = session.getDuration() * 60;
                int remaining = session.getRemainingTime();
                int elapsed = total - remaining;
                float progress = (total > 0) ? (float)elapsed / total : 0.0f;

                panel.label.setText(session.getName() + " (" + session.getFormattedRemainingTime() + ")");
                panel.progressBar.setProgress(progress);
                panel.applyTheme(theme, background, foreground, sel);

                if (sel && theme == Theme.LIGHT) {
                    panel.label.setForeground(Color.BLACK);
                }

                return panel;
            } else if (userObject instanceof Task) {
                Task task = (Task) userObject;
                setText(task.getName());
                setIconForTask(task);
                if (sel && theme == Theme.LIGHT) {
                    setForeground(Color.BLACK);
                    setBackgroundSelectionColor(Color.LIGHT_GRAY);
                }
                return this;
            } else {
                return this;
            }
        }

        private void setIconForTask(Task task) {
            Icon chosenIcon = null;
            switch (task.getState()) {
                case "To-Do":
                    chosenIcon = chooseConsistentIcon(task, todoIcons);
                    break;
                case "In-Progress":
                    chosenIcon = chooseConsistentIcon(task, inProgressIcons);
                    break;
                case "Done":
                    chosenIcon = chooseConsistentIcon(task, doneIcons);
                    break;
                default:
                    chosenIcon = null;
            }
            setIcon(chosenIcon);
        }

        private Icon chooseConsistentIcon(Task task, Icon[] icons) {
            if (!chosenIconsForTasks.containsKey(task)) {
                int chosenIndex = new Random().nextInt(icons.length);
                chosenIconsForTasks.put(task, chosenIndex);
            }
            return icons[chosenIconsForTasks.get(task)];
        }

        public void clearCachedIconForTask(Task task) {
            chosenIconsForTasks.remove(task);
        }
    }
}
