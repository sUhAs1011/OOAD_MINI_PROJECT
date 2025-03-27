package com.example.voting;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private Connection connection;

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

    public boolean registerCandidate(String candidateName) {
        String query = "INSERT INTO candidates (name, votes) VALUES (?, 0)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, candidateName);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error during candidate registration: " + e.getMessage());
            return false;
        }
    }

    public List<String> getCandidates() {
        List<String> candidates = new ArrayList<>();
        String query = "SELECT name FROM candidates";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                candidates.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching candidates: " + e.getMessage());
        }
        return candidates;
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

    public boolean castVote(String username, String candidate) {
        try {
            String updateVotesQuery = "UPDATE candidates SET votes = votes + 1 WHERE name = ?";
            try (PreparedStatement voteStatement = connection.prepareStatement(updateVotesQuery)) {
                voteStatement.setString(1, candidate);
                voteStatement.executeUpdate();
            }

            String updateUserQuery = "UPDATE users SET has_voted = TRUE WHERE username = ?";
            try (PreparedStatement userStatement = connection.prepareStatement(updateUserQuery)) {
                userStatement.setString(1, username);
                userStatement.executeUpdate();
            }

            String auditLogQuery = "INSERT INTO audit_log (action) VALUES (?)";
            try (PreparedStatement auditStatement = connection.prepareStatement(auditLogQuery)) {
                auditStatement.setString(1, "User " + username + " voted for " + candidate);
                auditStatement.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error during voting: " + e.getMessage());
            return false;
        }
    }
}