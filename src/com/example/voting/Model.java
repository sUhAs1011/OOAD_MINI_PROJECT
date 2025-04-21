package com.example.voting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Model {
    private Connection connection;
    private Map<String, String> otpStorage = new HashMap<>();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading MySQL driver: " + e.getMessage());
        }
    }

    public Model() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "Helloworld@2025");
            
            // Check and add the role column if it doesn't exist
            ensureRoleColumnExists();
            
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    /**
     * Ensures the role column exists in the users table
     */
    private void ensureRoleColumnExists() {
        try {
            // Check if the role column exists
            Statement checkStatement = connection.createStatement();
            try {
                checkStatement.executeQuery("SELECT role FROM users LIMIT 1");
                // Column exists, no need to add it
            } catch (SQLException e) {
                // Column doesn't exist, add it
                if (e.getMessage().contains("Unknown column")) {
                    Statement alterStatement = connection.createStatement();
                    alterStatement.executeUpdate("ALTER TABLE users ADD COLUMN role VARCHAR(10) DEFAULT 'voter'");
                    System.out.println("Added 'role' column to users table");
                    
                    // Also add an admin user if one doesn't exist
                    PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT IGNORE INTO users (username, password, role) VALUES ('admin', 'admin123', 'admin')");
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking or adding role column: " + e.getMessage());
        }
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
            return false;
        }
    }

    public boolean hasVoted(String username) {
        String query = "SELECT has_voted FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("has_voted");
            }
        } catch (SQLException e) {
            System.err.println("Error checking voting status: " + e.getMessage());
        }
        return false;
    }

    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void storeOTP(String username, String otp) {
        otpStorage.put(username, otp);
    }

    public boolean verifyOTP(String username, String enteredOTP) {
        String storedOTP = otpStorage.get(username);
        return storedOTP != null && storedOTP.equals(enteredOTP);
    }

    public void sendOTP(String username, String otp) {
        System.out.println("Sending OTP to user: " + username + ". Your OTP is: " + otp);
    }

    public boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error during registration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Register a user with a specific role
     * @param username Username to register
     * @param password Password for the account
     * @param role Role for the user (admin or voter)
     * @return true if registration was successful
     */
    public boolean registerUserWithRole(String username, String password, String role) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error during registration: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if a user has admin role
     * @param username Username to check
     * @return true if user is an admin
     */
    public boolean isAdmin(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return "admin".equals(resultSet.getString("role"));
            }
        } catch (SQLException e) {
            System.err.println("Error checking admin status: " + e.getMessage());
        }
        return false;
    }

    public boolean registerCandidate(String candidateName, String constituency) {
        String query = "INSERT INTO candidates (name, constituency, votes) VALUES (?, ?, 0)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, candidateName);
            statement.setString(2, constituency);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error during candidate registration: " + e.getMessage());
            return false;
        }
    }

    public List<String> getConstituencies() {
        List<String> constituencies = new ArrayList<>();
        String query = "SELECT DISTINCT constituency FROM candidates";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                constituencies.add(resultSet.getString("constituency"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching constituencies: " + e.getMessage());
        }
        return constituencies;
    }

    public List<String> getCandidatesByConstituency(String constituency) {
        List<String> candidates = new ArrayList<>();
        String query = "SELECT name FROM candidates WHERE constituency = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, constituency);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                candidates.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching candidates: " + e.getMessage());
        }
        return candidates;
    }

    public int getTotalVotes() {
        String query = "SELECT SUM(votes) AS total_votes FROM candidates";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("total_votes");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total votes: " + e.getMessage());
        }
        return 0;
    }

    public boolean castVote(String username, String candidate) {
        try {
            int totalVotes = getTotalVotes();
            if (totalVotes >= 100) {
                return false;
            }
            String updateVotesQuery = "UPDATE candidates SET votes = votes + 1 WHERE name = ?";
            if (candidate.equals("None of the Above (NOTA)")) {
                updateVotesQuery = "UPDATE candidates SET votes = votes + 1 WHERE name = 'None of the Above (NOTA)'";
            }
            try (PreparedStatement voteStatement = connection.prepareStatement(updateVotesQuery)) {
                voteStatement.setString(1, candidate);
                voteStatement.executeUpdate();
            }
            String updateUserQuery = "UPDATE users SET has_voted = TRUE WHERE username = ?";
            try (PreparedStatement userStatement = connection.prepareStatement(updateUserQuery)) {
                userStatement.setString(1, username);
                userStatement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error during voting: " + e.getMessage());
            return false;
        }
    }

    public String getWinner() {
        String query = "SELECT name FROM candidates ORDER BY votes DESC LIMIT 1";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching winner: " + e.getMessage());
        }
        return null;
    }

    public boolean saveFeedback(String username, String feedback) {
        String query = "UPDATE users SET feedback = ? WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, feedback);
            statement.setString(2, username);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving feedback: " + e.getMessage());
            return false;
        }
    }

    public List<String> getFeedbackReport() {
        List<String> feedbackList = new ArrayList<>();
        String query = "SELECT username, feedback FROM users WHERE feedback IS NOT NULL";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String entry = "User: " + resultSet.getString("username") + " | Feedback: " + resultSet.getString("feedback");
                feedbackList.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching feedback report: " + e.getMessage());
        }
        return feedbackList;
    }

    public List<String> getCandidatesWithVotes() {
        List<String> candidatesWithVotes = new ArrayList<>();
        String query = "SELECT name, votes FROM candidates";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String candidate = resultSet.getString("name") + " - Votes: " + resultSet.getInt("votes");
                candidatesWithVotes.add(candidate);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching candidates with votes: " + e.getMessage());
        }
        return candidatesWithVotes;
    }

    public List<String> getCandidatesWithVotesByConstituency(String constituency) {
        List<String> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "Helloworld@2025");
             PreparedStatement stmt = conn.prepareStatement("SELECT name, votes FROM candidates WHERE constituency = ?")) {
            stmt.setString(1, constituency);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("name") + " - Votes: " + rs.getInt("votes"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Admin operations
    
    /**
     * Get total number of users in the system
     * @return total number of users
     */
    public int getTotalUsers() {
        // Placeholder - in a real system this would query the database
        return 25; // Example value
    }
    
    /**
     * Get number of users who have voted
     * @return number of users who have voted
     */
    public int getVotedUsers() {
        // Placeholder - in a real system this would query the database
        return 18; // Example value
    }
    
    /**
     * Get total number of candidates across all constituencies
     * @return total number of candidates
     */
    public int getTotalCandidates() {
        int count = 0;
        for (String constituency : getConstituencies()) {
            List<String> candidates = getCandidatesByConstituency(constituency);
            if (candidates != null) {
                count += candidates.size();
            }
        }
        return count;
    }
    
    /**
     * Get total number of constituencies
     * @return total number of constituencies
     */
    public int getTotalConstituencies() {
        return getConstituencies().size();
    }
    
    /**
     * Get all candidates with their vote counts for display in the admin panel
     * @return list of CandidateItem objects
     */
    public List<AdminView.CandidateItem> getAllCandidates() {
        List<AdminView.CandidateItem> result = new ArrayList<>();
        
        String query = "SELECT name, constituency, votes FROM candidates";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String constituency = resultSet.getString("constituency");
                int votes = resultSet.getInt("votes");
                result.add(new AdminView.CandidateItem(name, constituency, votes));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all candidates: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get candidate votes for chart display
     * @return list of CandidateVote objects
     */
    public List<AdminView.CandidateVote> getCandidateVotesForChart() {
        List<AdminView.CandidateVote> result = new ArrayList<>();
        
        String query = "SELECT name, votes FROM candidates";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int votes = resultSet.getInt("votes");
                result.add(new AdminView.CandidateVote(name, votes));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching candidate votes for chart: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get candidate votes grouped by constituency for pie charts
     * @return Map of constituency name to list of candidate votes
     */
    public Map<String, List<JavaFXView.CandidateVote>> getCandidateVotesByConstituencyForCharts() {
        Map<String, List<JavaFXView.CandidateVote>> result = new HashMap<>();
        
        List<String> constituencies = getConstituencies();
        for (String constituency : constituencies) {
            List<JavaFXView.CandidateVote> constituencyVotes = new ArrayList<>();
            
            String query = "SELECT name, votes FROM candidates WHERE constituency = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, constituency);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String candidateName = resultSet.getString("name");
                    int votes = resultSet.getInt("votes");
                    constituencyVotes.add(new JavaFXView.CandidateVote(candidateName, String.valueOf(votes)));
                }
            } catch (SQLException e) {
                System.err.println("Error fetching candidate votes by constituency: " + e.getMessage());
            }
            
            // Only add constituencies that have candidates
            if (!constituencyVotes.isEmpty()) {
                result.put(constituency, constituencyVotes);
            }
        }
        
        return result;
    }
    
    /**
     * Get votes for a specific candidate
     * @param candidate the candidate name
     * @return number of votes
     */
    private int getVotesForCandidate(String candidate) {
        // Placeholder - in a real system this would query the database
        // Return a random number between 0 and 50 for demonstration
        return new java.util.Random().nextInt(51);
    }
    
    /**
     * Delete a candidate from the system
     * @param name the candidate name
     * @param constituency the constituency
     * @return true if successful
     */
    public boolean deleteCandidate(String name, String constituency) {
        String query = "DELETE FROM candidates WHERE name = ? AND constituency = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, constituency);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting candidate: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reset all vote counts
     * @return true if successful
     */
    public boolean resetAllVotes() {
        // Placeholder - in a real system this would set all vote counts to zero
        return true;
    }
    
    /**
     * Reset all user voting status
     * @return true if successful
     */
    public boolean resetUserVoteStatus() {
        // Placeholder - in a real system this would reset the has_voted flag for all users
        return true;
    }
    
    /**
     * Delete all candidates
     * @return true if successful
     */
    public boolean deleteAllCandidates() {
        // Placeholder - in a real system this would delete all candidates from the database
        return true;
    }
    
    /**
     * Perform a full system reset
     * @return true if successful
     */
    public boolean fullSystemReset() {
        // Placeholder - in a real system this would reset votes, user voting status, and delete all candidates
        boolean resetVotes = resetAllVotes();
        boolean resetUsers = resetUserVoteStatus();
        boolean deleteAllCandidates = deleteAllCandidates();
        
        return resetVotes && resetUsers && deleteAllCandidates;
    }
    
    /**
     * Verify admin password
     * @param password the password to verify
     * @return true if the password is correct
     */
    public boolean verifyAdminPassword(String password) {
        // Simple admin password for demonstration
        return "admin123".equals(password);
    }
}