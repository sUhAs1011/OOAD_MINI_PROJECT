import java.sql.*;
import java.util.Scanner;

// Model - User
class User {
    private int id;
    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
}

// Model - Database Operations
class UserDAO {
    private Connection conn;

    public UserDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) {
        try {
            String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

// View - User Interface
class UserView {
    private Scanner scanner = new Scanner(System.in);

    public User getUserInputForRegistration() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        return new User(username, password, email);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public String[] getUserLoginInput() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        return new String[]{username, password};
    }
}

// Controller - Handles Business Logic
class UserController {
    private UserDAO userDAO;
    private UserView userView;

    public UserController(UserDAO userDAO, UserView userView) {
        this.userDAO = userDAO;
        this.userView = userView;
    }

    public void registerUser() {
        User user = userView.getUserInputForRegistration();
        if (userDAO.registerUser(user)) {
            userView.showMessage("Registration successful!");
        } else {
            userView.showMessage("Registration failed. Try again.");
        }
    }

    public void loginUser() {
        String[] credentials = userView.getUserLoginInput();
        User user = userDAO.loginUser(credentials[0], credentials[1]);
        if (user != null) {
            userView.showMessage("Login successful! Welcome " + user.getUsername());
        } else {
            userView.showMessage("Invalid username or password.");
        }
    }
}
