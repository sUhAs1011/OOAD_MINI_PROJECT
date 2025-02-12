import java.sql.*;
import java.util.*;

// Model - Report
class Report {
    private double totalIncome;
    private double totalExpense;
    private String period;

    public Report(double totalIncome, double totalExpense, String period) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.period = period;
    }

    public double getTotalIncome() { return totalIncome; }
    public double getTotalExpense() { return totalExpense; }
    public String getPeriod() { return period; }
}

// Model - Database Operations
class ReportDAO {
    private Connection conn;

    public ReportDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Report generateReport(String period) {
        double totalIncome = 0;
        double totalExpense = 0;
        try {
            String incomeQuery = "SELECT SUM(amount) AS total FROM income WHERE period = ?";
            PreparedStatement incomeStmt = conn.prepareStatement(incomeQuery);
            incomeStmt.setString(1, period);
            ResultSet incomeRs = incomeStmt.executeQuery();
            if (incomeRs.next()) {
                totalIncome = incomeRs.getDouble("total");
            }

            String expenseQuery = "SELECT SUM(amount) AS total FROM expenses WHERE period = ?";
            PreparedStatement expenseStmt = conn.prepareStatement(expenseQuery);
            expenseStmt.setString(1, period);
            ResultSet expenseRs = expenseStmt.executeQuery();
            if (expenseRs.next()) {
                totalExpense = expenseRs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Report(totalIncome, totalExpense, period);
    }
}

// View - User Interface
class ReportView {
    public void displayReport(Report report) {
        System.out.println("\nFinancial Report for: " + report.getPeriod());
        System.out.println("Total Income: $" + report.getTotalIncome());
        System.out.println("Total Expense: $" + report.getTotalExpense());
        System.out.println("Net Savings: $" + (report.getTotalIncome() - report.getTotalExpense()));
    }
}

// Controller - Handles Business Logic
class ReportController {
    private ReportDAO reportDAO;
    private ReportView reportView;

    public ReportController(ReportDAO reportDAO, ReportView reportView) {
        this.reportDAO = reportDAO;
        this.reportView = reportView;
    }

    public void generateAndDisplayReport(String period) {
        Report report = reportDAO.generateReport(period);
        reportView.displayReport(report);
    }
}
