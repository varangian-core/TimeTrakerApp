package com.synthwave.timetracker;

import com.synthwave.timetracker.model.Task;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SessionTreeCellRenderer extends DefaultTreeCellRenderer {
    private final Map<Task, Integer> chosenIconsForTasks = new HashMap<>();
    private Icon[] todoIcons;
    private Icon[] inProgressIcons;
    private Icon[] doneIcons;

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

        if (userObject instanceof RuntimeSession) {
            RuntimeSession session = (RuntimeSession) userObject;
            return new SessionCellPanel(session, sel, ThemeManager.getTheme());
        } else if (userObject instanceof Task) {
            Task task = (Task) userObject;
            setText(task.getName());
            setIconForTask(task);
            return this;
        }

        return this; // For non-session, non-task nodes (e.g., root)
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

    /**
     * A panel that displays the session name, remaining time, and a progress bar.
     */
    static class SessionCellPanel extends JPanel {
        private JLabel label;
        private SessionProgressBar progressBar;

        public SessionCellPanel(RuntimeSession session, boolean selected, Theme theme) {
            super(new FlowLayout(FlowLayout.LEFT, 5, 0));
            setOpaque(false);

            label = new JLabel();
            progressBar = new SessionProgressBar();
            progressBar.setPreferredSize(new Dimension(200, 20));

            int total = session.getDuration() * 60;
            int remaining = session.getRemainingTime();
            int elapsed = total - remaining;
            float progress = (total > 0) ? (float) elapsed / (float) total : 0.0f;

            label.setText(session.getName() + " (" + session.getFormattedRemainingTime() + ")");
            progressBar.setProgress(progress);

            add(label);
            add(progressBar);

            applyTheme(theme, selected);
        }

        private void applyTheme(Theme theme, boolean selected) {
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

            setBackground(selected ? UIManager.getColor("Tree.selectionBackground") : background);

            if (selected && theme == Theme.LIGHT) {
                label.setForeground(Color.BLACK);
            } else {
                label.setForeground(foreground);
            }

            // Progress bar can use background as well
            progressBar.setBackground(background);
        }
    }
}
