package com.synthwave.timetracker;

import java.util.List;
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
import java.util.*;

class SessionPanel extends GradientPanel implements ThemedComponent {
    private JTree sessionTree;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;

    private JLabel sessionLabel;
    private JTextField sessionNameField;
    private JTextField sessionDurationField;
    private GradientLabel addSessionLabel;

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

        SessionTreeCellRenderer renderer = new SessionTreeCellRenderer(20, 20);
        sessionTree.setCellRenderer(renderer);

        sessionTree.setDragEnabled(true);
        sessionTree.setDropMode(DropMode.ON);
        sessionTree.setTransferHandler(new SessionTransferHandler());

        JScrollPane sessionScrollPane = new JScrollPane(sessionTree);
        add(sessionScrollPane, BorderLayout.CENTER);

        JPanel addSessionPanel = new JPanel(new BorderLayout());
        addSessionLabel = new GradientLabel("Add Session");
        addSessionPanel.add(addSessionLabel, BorderLayout.NORTH);

        addSessionPanel.setToolTipText("<html><b>Instructions:</b><br>"
            + "1. Enter a session name and duration, then click 'Add Session' or press Enter.<br>"
            + "2. Once sessions are created, you can drag and drop tasks into them from the other panel.</html>");

        sessionNameField = new JTextField();
        sessionNameField.setPreferredSize(new Dimension(0, 30));
        addSessionPanel.add(sessionNameField, BorderLayout.CENTER);

        sessionDurationField = new JTextField();
        sessionDurationField.setPreferredSize(new Dimension(50, 30));
        addSessionPanel.add(sessionDurationField, BorderLayout.EAST);

        addSessionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addSession(sessionNameField, sessionDurationField);
            }
        });

        sessionNameField.addActionListener(e -> addSession(sessionNameField, sessionDurationField));
        sessionDurationField.addActionListener(e -> addSession(sessionNameField, sessionDurationField));

        add(addSessionPanel, BorderLayout.SOUTH);

        // Register with ThemeManager and apply current theme
        ThemeManager.register(this);
        applyTheme(ThemeManager.getTheme());
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
        Color background;
        Color foreground;
        Color panelBackground;
        Color borderColor; // Color for RoundedBorder

        switch (theme) {
            case LIGHT:
                background = Color.WHITE;
                foreground = Color.BLACK;
                panelBackground = Color.LIGHT_GRAY;
                borderColor = foreground; // black borders
                break;
            case DARK:
                background = new Color(45, 45, 45);
                foreground = Color.WHITE;
                panelBackground = new Color(60, 60, 60);
                borderColor = foreground; // white borders
                break;
            case SYNTHWAVE:
                background = new Color(40,0,40);
                foreground = Color.MAGENTA;
                panelBackground = new Color(20,0,20);
                borderColor = new Color(255, 105, 180); // pink borders
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

        // Ensure fields and label use borderColor if they have RoundedBorder
        sessionNameField.setBackground(background);
        sessionNameField.setForeground(borderColor);

        sessionDurationField.setBackground(background);
        sessionDurationField.setForeground(borderColor);

        addSessionLabel.setForeground(foreground);
        addSessionLabel.setBackground(panelBackground);
        // If addSessionLabel or any button has RoundedBorder, also set its foreground to borderColor:
        // addSessionLabel.setForeground(borderColor); // if needed

        repaint();
    }

    private static class SessionTreeCellRenderer extends DefaultTreeCellRenderer {
        private Icon[] todoIcons;
        private Icon[] inProgressIcons;
        private Icon[] doneIcons;

        private final int iconWidth;
        private final int iconHeight;
        private final Random random = new Random();

        private final Map<Task, Integer> chosenIconsForTasks = new HashMap<>();

        public SessionTreeCellRenderer(int iconWidth, int iconHeight) {
            this.iconWidth = iconWidth;
            this.iconHeight = iconHeight;
            loadIcons();
        }

        private void loadIcons() {
            todoIcons = new Icon[] {
                loadAndScaleIcon("todo-1.png"),
                loadAndScaleIcon("todo-2.png")
            };

            inProgressIcons = new Icon[] {
                loadAndScaleIcon("inprogress-1.png"),
                loadAndScaleIcon("inprogress-2.png")
            };

            doneIcons = new Icon[] {
                loadAndScaleIcon("done-1.png"),
                loadAndScaleIcon("done-2.png")
            };
        }

        private Icon loadAndScaleIcon(String resourceName) {
            String fullPath = "/com/synthwave/timetracker/" + resourceName;
            try (InputStream is = getClass().getResourceAsStream(fullPath)) {
                if (is == null) {
                    return new ImageIcon(new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB));
                }
                BufferedImage original = ImageIO.read(is);
                Image scaled = original.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            } catch (IOException e) {
                return new ImageIcon(new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB));
            }
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();

            if (userObject instanceof Session) {
                Session session = (Session) userObject;
                String displayText = session.getName() + " (" + session.getFormattedRemainingTime() + ")";
                setText(displayText);
                setIcon(null);
            } else if (userObject instanceof Task) {
                Task task = (Task) userObject;
                setText(task.getName());
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
            return this;
        }

        private Icon chooseConsistentIcon(Task task, Icon[] icons) {
            if (!chosenIconsForTasks.containsKey(task)) {
                int chosenIndex = random.nextInt(icons.length);
                chosenIconsForTasks.put(task, chosenIndex);
            }
            return icons[chosenIconsForTasks.get(task)];
        }

        public void clearCachedIconForTask(Task task) {
            chosenIconsForTasks.remove(task);
        }
    }
}