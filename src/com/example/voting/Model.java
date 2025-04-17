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
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
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
            if (totalVotes >= 20) {
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
}