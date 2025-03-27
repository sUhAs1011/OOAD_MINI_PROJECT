import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class test {

    private static Connection connectToDatabase() {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/voting_system";
        String user = "root"; // Replace with your MySQL username
        String password = "Helloworld@2025"; // Replace with your MySQL password

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // Connect to the database
        Connection connection = connectToDatabase();
        if (connection == null) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the database. Exiting...");
            return;
        }

        // Create the GUI
        JFrame frame = new JFrame("Electronic Voting System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to the Electronic Voting System", SwingConstants.CENTER);
        frame.add(welcomeLabel);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> loginScreen(connection));
        frame.add(loginButton);

        // Sign-Up Button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(e -> signUpScreen(connection));
        frame.add(signUpButton);

        frame.setVisible(true);
    }

    private static void loginScreen(Connection connection) {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    boolean hasVoted = resultSet.getBoolean("has_voted");
                    if (!hasVoted) {
                        JOptionPane.showMessageDialog(loginFrame, "Login successful! Proceed to voting.");
                        votingScreen(connection, username);
                        loginFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(loginFrame, "You have already voted.");
                    }
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(loginFrame, "Error during login.");
            }
        });

        loginFrame.add(usernameLabel);
        loginFrame.add(usernameField);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);

        loginFrame.setVisible(true);
    }

    private static void signUpScreen(Connection connection) {
        JFrame signUpFrame = new JFrame("Sign Up");
        signUpFrame.setSize(300, 200);
        signUpFrame.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton signUpButton = new JButton("Sign Up");

        signUpButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(signUpFrame, "Sign-up successful! You can now log in.");
                    signUpFrame.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(signUpFrame, "Error during sign-up.");
            }
        });

        signUpFrame.add(usernameLabel);
        signUpFrame.add(usernameField);
        signUpFrame.add(passwordLabel);
        signUpFrame.add(passwordField);
        signUpFrame.add(signUpButton);

        signUpFrame.setVisible(true);
    }

    private static void votingScreen(Connection connection, String username) {
        JFrame votingFrame = new JFrame("Voting");
        votingFrame.setSize(400, 300);
        votingFrame.setLayout(new GridLayout(0, 1));

        JLabel instructions = new JLabel("Select a candidate to cast your vote:", SwingConstants.CENTER);
        votingFrame.add(instructions);

        try {
            String query = "SELECT * FROM candidates";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String candidateName = resultSet.getString("name");
                JButton voteButton = new JButton(candidateName);
                voteButton.addActionListener(e -> {
                    try {
                        // Update votes for the selected candidate
                        String updateQuery = "UPDATE candidates SET votes = votes + 1 WHERE name = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, candidateName);
                        updateStatement.executeUpdate();

                        // Mark user as having voted
                        String userUpdateQuery = "UPDATE users SET has_voted = TRUE WHERE username = ?";
                        PreparedStatement userUpdateStatement = connection.prepareStatement(userUpdateQuery);
                        userUpdateStatement.setString(1, username);
                        userUpdateStatement.executeUpdate();

                        // Add to audit log
                        String auditQuery = "INSERT INTO audit_log (action) VALUES (?)";
                        PreparedStatement auditStatement = connection.prepareStatement(auditQuery);
                        auditStatement.setString(1, "User " + username + " voted for " + candidateName);
                        auditStatement.executeUpdate();

                        JOptionPane.showMessageDialog(votingFrame, "Vote successfully cast!");
                        votingFrame.dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(votingFrame, "Error during voting.");
                    }
                });
                votingFrame.add(voteButton);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(votingFrame, "Error loading candidates.");
        }

        votingFrame.setVisible(true);
    }
}
