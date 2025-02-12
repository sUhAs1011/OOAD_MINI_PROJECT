import java.sql.*;
import java.util.*;

// Model - Budget
class Budget {
    private int id;
    private double amount;
    private String category;
    private String startDate;
    private String endDate;
    private boolean recurring;

    public Budget(double amount, String category, String startDate, String endDate, boolean recurring) {
        this.amount = amount;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recurring = recurring;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isRecurring() { return recurring; }
}

// Model - Database Operations
class BudgetDAO {
    private Connection conn;

    public BudgetDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addBudget(Budget budget) {
        try {
            String query = "INSERT INTO budgets (amount, category, start_date, end_date, recurring) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, budget.getAmount());
            stmt.setString(2, budget.getCategory());
            stmt.setString(3, budget.getStartDate());
            stmt.setString(4, budget.getEndDate());
            stmt.setBoolean(5, budget.isRecurring());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Budget> getBudgets() {
        List<Budget> budgets = new ArrayList<>();
        try {
            String query = "SELECT * FROM budgets";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                budgets.add(new Budget(
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getBoolean("recurring")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }
}

// View - User Interface
class BudgetView {
    private Scanner scanner = new Scanner(System.in);

    public Budget getBudgetInput() {
        System.out.print("Enter budget amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();
        System.out.print("Is this a recurring budget? (true/false): ");
        boolean recurring = scanner.nextBoolean();
        return new Budget(amount, category, startDate, endDate, recurring);
    }

    public void showBudgetList(List<Budget> budgets) {
        System.out.println("\nBudget List:");
        for (Budget b : budgets) {
            System.out.println("Category: " + b.getCategory() + " | Amount: $" + b.getAmount() + " | Duration: " + b.getStartDate() + " to " + b.getEndDate() + " | Recurring: " + b.isRecurring());
        }
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}

// Controller - Handles Business Logic
class BudgetController {
    private BudgetDAO budgetDAO;
    private BudgetView budgetView;

    public BudgetController(BudgetDAO budgetDAO, BudgetView budgetView) {
        this.budgetDAO = budgetDAO;
        this.budgetView = budgetView;
    }

    public void addBudget() {
        Budget budget = budgetView.getBudgetInput();
        if (budgetDAO.addBudget(budget)) {
            budgetView.showMessage("Budget set successfully!");
        } else {
            budgetView.showMessage("Failed to set budget. Try again.");
        }
    }

    public void viewBudgets() {
        List<Budget> budgets = budgetDAO.getBudgets();
        budgetView.showBudgetList(budgets);
    }
}
