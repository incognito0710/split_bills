import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// JFreeChart imports
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;

public class splitbillsApp extends JFrame {

    // ---- Fields ----
    private JTextField userField, descField, amountField;
    private JComboBox<user> paidByBox;
    private JComboBox<String> splitTypeBox;
    private JTable balanceTable;
    private DefaultTableModel balanceModel;

    private List<user> users = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();

    private boolean darkMode = false;

    public splitbillsApp() {
        setTitle("💰 SplitBills - Smart Expense Sharing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        // App Icon
        try {
            setIconImage(new ImageIcon(getClass().getResource("/resources/app.png")).getImage());
        } catch (Exception e) {
            System.out.println("⚠ App icon missing.");
        }

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.add("👥 Users", createUserPanel());
        tabs.add("💸 Expenses", createExpensePanel());
        tabs.add("📊 Balances", createBalancePanel());
        tabs.add("📈 Dashboard", createDashboardPanel());

        add(tabs, BorderLayout.CENTER);

        // Dark Mode Toggle
        JCheckBox darkModeToggle = new JCheckBox("🌙 Dark Mode");
        darkModeToggle.addActionListener(e -> toggleDarkMode(darkModeToggle.isSelected()));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(darkModeToggle);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ==================== USERS PANEL ====================
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Enter User Name:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        userField = new JTextField(15);
        JButton addUserBtn =UIutils.createStyledButton("Add User", new Color(50, 150, 90), "/resources/user.png");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(label, gbc);
        gbc.gridx = 1; panel.add(userField, gbc);
        gbc.gridx = 2; panel.add(addUserBtn, gbc);

        addUserBtn.addActionListener(e -> addUser());

        return panel;
    }

    // ==================== EXPENSE PANEL ====================
    private JPanel createExpensePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel descLabel = new JLabel("Description:");
        JLabel amtLabel = new JLabel("Amount:");
        JLabel paidLabel = new JLabel("Paid By:");
        JLabel splitLabel = new JLabel("Split Type:");

        descField = new JTextField(15);
        amountField = new JTextField(10);
        paidByBox = new JComboBox<>();
        splitTypeBox = new JComboBox<>(new String[]{"Equal", "Custom"});

        JButton addExpenseBtn = UIutils.createStyledButton("Add Expense", new Color(60, 120, 200), "/resources/expense.png");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(descLabel, gbc);
        gbc.gridx = 1; panel.add(descField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(amtLabel, gbc);
        gbc.gridx = 1; panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(paidLabel, gbc);
        gbc.gridx = 1; panel.add(paidByBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(splitLabel, gbc);
        gbc.gridx = 1; panel.add(splitTypeBox, gbc);

        gbc.gridx = 1; gbc.gridy = 4; panel.add(addExpenseBtn, gbc);

        addExpenseBtn.addActionListener(e -> addExpense());

        return panel;
    }

    // ==================== BALANCE PANEL ====================
    private JPanel createBalancePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        balanceModel = new DefaultTableModel(new String[]{"User", "Balance"}, 0);
        balanceTable = new JTable(balanceModel);
        JScrollPane scrollPane = new JScrollPane(balanceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton showBtn = UIutils.createStyledButton("Show Balances", new Color(70, 130, 180), "/resources/balance.png");
        JButton simplifyBtn = UIutils.createStyledButton("Simplify Debts", new Color(200, 90, 70), null);

        btnPanel.add(showBtn);
        btnPanel.add(simplifyBtn);

        panel.add(btnPanel, BorderLayout.SOUTH);

        showBtn.addActionListener(e -> showBalances());
        simplifyBtn.addActionListener(e -> simplifyDebts());

        return panel;
    }

    // ==================== DASHBOARD PANEL ====================
    private JPanel dashboardPanel;
    private JLabel totalUsersLbl, totalExpLbl;
    private DefaultPieDataset pieDataset;

    private JPanel createDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top summary
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        totalUsersLbl = new JLabel("👥 Total Users: " + users.size(), SwingConstants.CENTER);
        totalUsersLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        totalExpLbl = new JLabel("💸 Total Expenses: ₹" + getTotalExpenses(), SwingConstants.CENTER);
        totalExpLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        summaryPanel.add(totalUsersLbl);
        summaryPanel.add(totalExpLbl);
        dashboardPanel.add(summaryPanel, BorderLayout.NORTH);

        // Pie Chart
        pieDataset = new DefaultPieDataset();
        var chart = org.jfree.chart.ChartFactory.createPieChart(
                "Who Spent the Most?",
                pieDataset,
                true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        dashboardPanel.add(chartPanel, BorderLayout.CENTER);

        return dashboardPanel;
    }

    // Call this whenever expenses/users change
    private void updateDashboard() {
        totalUsersLbl.setText("👥 Total Users: " + users.size());
        totalExpLbl.setText("💸 Total Expenses: ₹" + getTotalExpenses());

        pieDataset.clear();
        for (user u : users) {
            double total = expenses.stream()
                    .filter(e -> e.getPaidById() == u.getId())
                    .mapToDouble(Expense::getAmount)
                    .sum();
            if (total > 0) pieDataset.setValue(u.getName(), total);
        }
    }



    // ==================== ACTIONS ====================
    private void addUser() {
        String name = userField.getText().trim();
        if (!name.isEmpty()) {
            user newUser = new user(users.size() + 1, name);
            users.add(newUser);
            paidByBox.addItem(newUser);
            JOptionPane.showMessageDialog(this, "User added: " + name);
            userField.setText("");
        }
    }

    private void addExpense() {
        String desc = descField.getText().trim();
        String amtText = amountField.getText().trim();
        user paidByUser = (user) paidByBox.getSelectedItem();

        if (desc.isEmpty() || amtText.isEmpty() || paidByUser == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try {
            double amt = Double.parseDouble(amtText);
            // For now, splitMap is null or empty (you can implement splitting logic later)
            Expense e = new Expense(desc, amt, paidByUser.getId(), null);
            expenses.add(e);
            JOptionPane.showMessageDialog(this, "Expense added: " + desc + " ₹" + amt);
            descField.setText("");
            amountField.setText("");
            updateDashboard(); // update pie chart and totals
            showBalances();    // update balances table
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount");
        }
    }

    private void showBalances() {
            balanceModel.setRowCount(0);
            for (user u : users) {
                double total = expenses.stream()
                        .filter(e -> e.getPaidById() == u.getId())
                        .mapToDouble(Expense::getAmount)
                        .sum();
                balanceModel.addRow(new Object[]{u, "₹" + total});
            }


    }

    private void simplifyDebts() {
        JOptionPane.showMessageDialog(this, "Debts simplified (dummy)");
    }

    // ==================== HELPERS ====================
    private void toggleDarkMode(boolean enable) {
        darkMode = enable;
        Color bg = enable ? new Color(45, 45, 45) : new Color(245, 245, 245);
        Color fg = enable ? Color.WHITE : Color.BLACK;

        SwingUtilities.invokeLater(() -> {
            getContentPane().setBackground(bg);
            for (Component c : getContentPane().getComponents()) {
                c.setBackground(bg);
                c.setForeground(fg);
            }
        });
    }

    private double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }


    // ==================== MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(splitbillsApp::new);
    }
}
