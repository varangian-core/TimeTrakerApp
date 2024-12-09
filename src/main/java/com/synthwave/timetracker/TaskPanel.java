package com.synthwave.timetracker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

class TaskPanel extends GradientPanel {
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField taskNameField;
    private SessionPanel sessionPanel;

    public TaskPanel(SessionPanel sessionPanel) {
        this.sessionPanel = sessionPanel;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 600));

        JLabel taskLabel = new JLabel("Tasks");
        taskLabel.setForeground(Color.BLACK);
        taskLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(taskLabel, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);

        taskList.setFixedCellHeight(24);
        taskList.setCellRenderer(new TaskListCellRenderer(20, 20));
        taskList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        taskList.setDragEnabled(true);
        taskList.setTransferHandler(new SessionTransferHandler());
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = taskList.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        editTaskState(index);
                    }
                }
            }
        });

        JScrollPane taskScrollPane = new JScrollPane(taskList);
        add(taskScrollPane, BorderLayout.CENTER);

        JPanel addTaskPanel = new JPanel(new BorderLayout());
        GradientLabel addTaskLabel = new GradientLabel("Add Task");
        addTaskPanel.add(addTaskLabel, BorderLayout.NORTH);

        taskNameField = new JTextField();
        taskNameField.setPreferredSize(new Dimension(0, 30));
        addTaskPanel.add(taskNameField, BorderLayout.CENTER);

        addTaskLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                addTask();
            }
        });

        taskNameField.addActionListener(e -> addTask());

        add(addTaskPanel, BorderLayout.SOUTH);
    }

    private void addTask() {
        String taskName = taskNameField.getText().trim();
        if (!taskName.isEmpty()) {
            Task newTask = new Task(taskName, "To-Do");
            taskListModel.addElement(newTask);
            taskNameField.setText("");
            sessionPanel.updateSessionDisplay();
        }
    }

    private void editTaskState(int index) {
        Task task = taskListModel.get(index);
        TaskStateDialog dialog = new TaskStateDialog(task, sessionPanel);
        dialog.setVisible(true);
        taskList.repaint();
        sessionPanel.updateTaskNode(task);
    }

    public static class RoundedBorder extends AbstractBorder {
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

    private static class TaskListCellRenderer extends DefaultListCellRenderer {
        private Icon[] todoIcons;
        private Icon[] inProgressIcons;
        private Icon[] doneIcons;

        private final int iconWidth;
        private final int iconHeight;
        private final Random random = new Random();
        private final Map<Task, Integer> chosenIconsForTasks = new HashMap<>();

        public TaskListCellRenderer(int iconWidth, int iconHeight) {
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
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Task task = (Task) value;
            label.setText(task.getName());

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
            }

            label.setIcon(chosenIcon);
            label.setForeground(Color.BLACK);
            label.setBackground(isSelected ? new Color(200, 200, 255) : new Color(240, 240, 240));
            return label;
        }

        private Icon chooseConsistentIcon(Task task, Icon[] icons) {
            if (!chosenIconsForTasks.containsKey(task)) {
                int chosenIndex = random.nextInt(icons.length);
                chosenIconsForTasks.put(task, chosenIndex);
            }
            return icons[chosenIconsForTasks.get(task)];
        }
    }
}