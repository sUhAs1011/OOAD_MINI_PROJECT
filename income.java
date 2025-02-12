import java.sql.*;
import java.util.*;

// Model - Transaction
class Transaction {
    private int id;
    private String type; // Income or Expense
    private double amount;
    private String category;
    private String date;

    public Transaction(String type, double amount, String category, String date) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
}

// Model - Database Operations
class TransactionDAO {
    private Connection conn;

    public TransactionDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addTransaction(Transaction transaction) {
        try {
            String query = "INSERT INTO transactions (type, amount, category, date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, transaction.getType());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getCategory());
            stmt.setString(4, transaction.getDate());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getTransactionHistory() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            String query = "SELECT * FROM transactions ORDER BY date DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}

// View - User Interface
class TransactionView {
    private Scanner scanner = new Scanner(System.in);

    public Transaction getTransactionInput() {
        System.out.print("Enter transaction type (Income/Expense): ");
        String type = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        return new Transaction(type, amount, category, date);
    }

    public void showTransactionHistory(List<Transaction> transactions) {
        System.out.println("\nTransaction History:");
        for (Transaction t : transactions) {
            System.out.println(t.getDate() + " - " + t.getType() + " - " + t.getCategory() + " - $" + t.getAmount());
        }
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}

// Controller - Handles Business Logic
class TransactionController {
    private TransactionDAO transactionDAO;
    private TransactionView transactionView;

    public TransactionController(TransactionDAO transactionDAO, TransactionView transactionView) {
        this.transactionDAO = transactionDAO;
        this.transactionView = transactionView;
    }

    public void addTransaction() {
        Transaction transaction = transactionView.getTransactionInput();
        if (transactionDAO.addTransaction(transaction)) {
            transactionView.showMessage("Transaction recorded successfully!");
        } else {
            transactionView.showMessage("Failed to record transaction. Try again.");
        }
    }

    public void viewTransactionHistory() {
        List<Transaction> transactions = transactionDAO.getTransactionHistory();
        transactionView.showTransactionHistory(transactions);
    }
}
