package com.synthwave.timetracker;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        taskList.setCellRenderer(new TaskListCellRenderer());
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
        taskNameField.setPreferredSize(new Dimension(0, 30)); // Set height to match session input field
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
        TaskStateDialog dialog = new TaskStateDialog(task, sessionPanel); // Pass sessionPanel to dialog
        dialog.setVisible(true);
        taskList.repaint();
        sessionPanel.updateTaskNode(task); // Update specific task node
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
        private final Icon toDoIcon = UIManager.getIcon("OptionPane.informationIcon");
        private final Icon inProgressIcon = UIManager.getIcon("OptionPane.warningIcon");
        private final Icon doneIcon = UIManager.getIcon("OptionPane.questionIcon");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Task task = (Task) value;
            label.setText(task.getName());

            switch (task.getState()) {
                case "To-Do":
                    label.setIcon(toDoIcon);
                    break;
                case "In-Progress":
                    label.setIcon(inProgressIcon);
                    break;
                case "Done":
                    label.setIcon(doneIcon);
                    break;
            }

            label.setForeground(Color.BLACK);
            label.setBackground(isSelected ? new Color(200, 200, 255) : new Color(240, 240, 240));
            return label;
        }
    }
}
