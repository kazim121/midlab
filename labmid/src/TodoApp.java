import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

class Task {
    private String description;
    private LocalDateTime dueDate;
    private String recurrence;

    public Task(String description) {
        this.description = description;
        this.dueDate = null;
        this.recurrence = null;
    }

    public Task(String description, LocalDateTime dueDate, String recurrence) {
        this.description = description;
        this.dueDate = dueDate;
        this.recurrence = recurrence;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dueDateStr = (dueDate != null) ? dueDate.format(formatter) : "No Due Date";
        String recurrenceStr = (recurrence != null && !recurrence.isEmpty()) ? " (" + recurrence + ")" : "";
        return description + " | Due: " + dueDateStr + recurrenceStr;
    }
}

public class TodoApp extends JFrame {
    private DefaultListModel<String> taskModel;
    private JList<String> taskList;
    private JTextField taskField;
    private JButton addButton, removeButton, clearButton, scheduleButton;
    private ArrayList<Task> allTasks;

    public TodoApp() {
        setTitle("TODO Application");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize components
        taskModel = new DefaultListModel<>();
        taskList = new JList<>(taskModel);
        taskField = new JTextField();
        addButton = new JButton("Add Task");
        removeButton = new JButton("Remove Selected");
        clearButton = new JButton("Clear All");
        scheduleButton = new JButton("Schedule Task");

        allTasks = new ArrayList<>();

        // Set layout
        setLayout(new BorderLayout());

        // Top panel for input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(taskField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(removeButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(scheduleButton);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(e -> addTask());
        removeButton.addActionListener(e -> removeSelectedTask());
        clearButton.addActionListener(e -> clearAllTasks());
        scheduleButton.addActionListener(e -> scheduleTask());

        // Optional: Press Enter to add task
        taskField.addActionListener(e -> addTask());
    }

    private void addTask() {
        String taskDescription = taskField.getText().trim();
        if (!taskDescription.isEmpty()) {
            Task task = new Task(taskDescription);
            allTasks.add(task);
            taskModel.addElement(task.toString());
            taskField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a task.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removeSelectedTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            allTasks.remove(selectedIndex);
            taskModel.remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAllTasks() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all tasks?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            taskModel.clear();
            allTasks.clear();
        }
    }

    private void scheduleTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task selectedTask = allTasks.get(selectedIndex);

            // Get due date
            String dueDateStr = JOptionPane.showInputDialog(this, "Enter due date (yyyy-MM-dd HH:mm):");
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime dueDate = LocalDateTime.parse(dueDateStr, formatter);
                    selectedTask.setDueDate(dueDate);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd HH:mm.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Get recurrence option
            String recurrence = JOptionPane.showInputDialog(this, "Enter recurrence (DAILY, WEEKLY, MONTHLY) or leave blank for no recurrence:");
            if (recurrence != null && !recurrence.trim().isEmpty()) {
                selectedTask.setRecurrence(recurrence.toUpperCase());
            }

            // Update the task in the list
            taskModel.set(selectedIndex, selectedTask.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to schedule.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TodoApp app = new TodoApp();
            app.setVisible(true);
        });
    }
}
