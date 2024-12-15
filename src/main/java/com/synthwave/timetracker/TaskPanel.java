package com.synthwave.timetracker;

import com.synthwave.timetracker.dao.TaskDao;
import com.synthwave.timetracker.model.Task; // Import the model Task class

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class TaskPanel extends GradientPanel implements ThemedComponent {
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField taskNameField;
    private SessionPanel sessionPanel;
    private JLabel taskLabel;
    private TaskDao taskDao; // If provided, can insert tasks into DB

    // Original constructor
    public TaskPanel(SessionPanel sessionPanel) {
        this(null, sessionPanel);
    }

    // Constructor called by TimeTrackerApp
    public TaskPanel(TaskDao taskDao, SessionPanel sessionPanel) {
        this.taskDao = taskDao;
        this.sessionPanel = sessionPanel;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 600));

        taskLabel = new JLabel("Tasks");
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

        ThemeManager.register(this);
        applyTheme(ThemeManager.getTheme());
    }

    private void addTask() {
        String taskName = taskNameField.getText().trim();
        if (!taskName.isEmpty()) {
            // Create a new Task object
            Task newTask = new Task(taskName, "To-Do");

            // If we have a taskDao, persist the task in the database
            if (taskDao != null) {
                try {
                    int newTaskId = taskDao.insert(newTask, null);
                    newTask.setId(newTaskId);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to save task to the database: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Add the new task to the UI list
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

    @Override
    public void applyTheme(Theme theme) {
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
        taskLabel.setForeground(foreground);

        taskList.setBackground(background);
        taskList.setForeground(foreground);
        taskNameField.setBackground(background);
        taskNameField.setForeground(foreground);

        repaint();
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
