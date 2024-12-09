package com.synthwave.timetracker;

import com.synthwave.timetracker.TimerPanel.RoundedBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TaskStateDialog extends JDialog {
    private Task task;
    private JComboBox<String> stateComboBox;
    private SessionPanel sessionPanel;

    public TaskStateDialog(Task task, SessionPanel sessionPanel) {
        this.task = task;
        this.sessionPanel = sessionPanel;
        setTitle("Edit Task State");
        setSize(300, 150);
        setModal(true);
        setLayout(new BorderLayout());

        // Center the dialog on the screen
        setLocationRelativeTo(null);

        JLabel stateLabel = new JLabel("State:");
        stateComboBox = new JComboBox<>(new String[]{"To-Do", "In-Progress", "Done"});
        stateComboBox.setSelectedItem(task.getState());

        JPanel panel = new JPanel();
        panel.add(stateLabel);
        panel.add(stateComboBox);
        add(panel, BorderLayout.CENTER);

        JButton saveButton = createRoundedButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                task.setState((String) stateComboBox.getSelectedItem());
                sessionPanel.updateTaskNode(task); // Update the specific task node
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(200, 200, 200));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            button.getBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFont(new Font("Serif", Font.BOLD, 14));
        button.setBorder(new RoundedBorder(15)); // Use the standalone RoundedBorder class
        return button;
    }
}