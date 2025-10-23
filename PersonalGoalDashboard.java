import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class PersonalGoalDashboard extends JFrame {
    private DefaultListModel<String> goalListModel = new DefaultListModel<>();
    private JList<String> goalList = new JList<>(goalListModel);
    private JProgressBar progressBar = new JProgressBar(0, 100);
    private java.util.List<Integer> progressValues = new ArrayList<>();
    private static final String FILE_NAME = "goals.txt";

    public PersonalGoalDashboard() {
        setTitle("🎯 Personal Goal Dashboard");
        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("My Goals", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        goalList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(goalList), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField goalField = new JTextField(18);
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update Progress");
        JButton deleteBtn = new JButton("Delete");

        inputPanel.add(goalField);
        inputPanel.add(addBtn);
        inputPanel.add(updateBtn);
        inputPanel.add(deleteBtn);
        add(inputPanel, BorderLayout.SOUTH);

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(new JLabel("Overall Progress:", SwingConstants.CENTER), BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        add(progressPanel, BorderLayout.EAST);

        // Load previous goals
        loadGoals();

        addBtn.addActionListener(e -> {
            String goal = goalField.getText().trim();
            if (!goal.isEmpty()) {
                goalListModel.addElement(goal + " (0%)");
                progressValues.add(0);
                goalField.setText("");
                saveGoals();
            }
        });

        updateBtn.addActionListener(e -> {
            int index = goalList.getSelectedIndex();
            if (index != -1) {
                String current = goalListModel.get(index);
                int newProgress = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter progress (0–100):"));
                progressValues.set(index, newProgress);
                goalListModel.set(index, current.split("\\(")[0].trim() + " (" + newProgress + "%)");
                updateOverallProgress();
                saveGoals();
            }
        });

        deleteBtn.addActionListener(e -> {
            int index = goalList.getSelectedIndex();
            if (index != -1) {
                goalListModel.remove(index);
                progressValues.remove(index);
                updateOverallProgress();
                saveGoals();
            }
        });

        setVisible(true);
    }

    private void updateOverallProgress() {
        int total = progressValues.stream().mapToInt(i -> i).sum();
        int avg = progressValues.isEmpty() ? 0 : total / progressValues.size();
        progressBar.setValue(avg);
    }

    private void saveGoals() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < goalListModel.size(); i++) {
                pw.println(goalListModel.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGoals() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                goalListModel.addElement(line);
                try {
                    int val = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                    progressValues.add(val);
                } catch (Exception e) {
                    progressValues.add(0);
                }
            }
            updateOverallProgress();
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PersonalGoalDashboard::new);
    }
}
