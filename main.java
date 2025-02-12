import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        UserView userView = new UserView();
        UserController userController = new UserController(userDAO, userView);
        
        IncomeDAO incomeDAO = new IncomeDAO();
        IncomeView incomeView = new IncomeView();
        IncomeController incomeController = new IncomeController(incomeDAO, incomeView);
        
        ExpenseDAO expenseDAO = new ExpenseDAO();
        ExpenseView expenseView = new ExpenseView();
        ExpenseController expenseController = new ExpenseController(expenseDAO, expenseView);
        
        BudgetDAO budgetDAO = new BudgetDAO();
        BudgetView budgetView = new BudgetView();
        BudgetController budgetController = new BudgetController(budgetDAO, budgetView);
        
        ReportDAO reportDAO = new ReportDAO();
        ReportView reportView = new ReportView();
        ReportController reportController = new ReportController(reportDAO, reportView);

        while (true) {
            System.out.println("\nFinance Management System");
            System.out.println("1. User Registration");
            System.out.println("2. User Login");
            System.out.println("3. Income Management");
            System.out.println("4. Expense Tracking");
            System.out.println("5. Budget Management");
            System.out.println("6. Reports & Analytics");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    userController.registerUser();
                    break;
                case 2:
                    userController.loginUser();
                    break;
                case 3:
                    incomeController.manageIncome();
                    break;
                case 4:
                    expenseController.manageExpenses();
                    break;
                case 5:
                    budgetController.manageBudgets();
                    break;
                case 6:
                    reportController.generateReports();
                    break;
                case 7:
                    System.out.println("Exiting Finance Management System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
