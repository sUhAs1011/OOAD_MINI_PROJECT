// Controller.java
package com.example.voting;

import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.util.Pair;

public class Controller {
    private Model model;
    private ViewInterface view;

    public Controller(Model model, ViewInterface view) {
        this.model = model;
        this.view = view;
        
        // Set the model reference in the view
        view.setModel(model);
        
        run();
    }

    public void run() {
        view.showMainMenu();
        view.setMainMenuListener(this::handleMainMenuSelection);
    }

    public void handleMainMenuSelection(String selectedOption) {
        switch (selectedOption) {
            case "Dashboard":
                // Refresh dashboard data
                updateDashboardData();
                view.showMainMenu();
                break;
            case "Login":
                handleLogin();
                break;
            case "Sign Up":
                handleSignUp();
                break;
            case "View Results":
                handleViewResults();
                break;
            case "Register Candidate":
                handleCandidateRegistration();
                break;
            case "View Feedback Report":
                handleViewFeedbackReport();
                break;
            case "Admin Login":
                handleAdminLogin();
                break;
            case "Exit":
                view.showMessage("Thank you for using the Electronic Voting System!");
                System.exit(0);
                break;
            default:
                view.showMessage("Invalid option. Please select again.");
        }
    }

    private void updateDashboardData() {
        // Get the number of constituencies and update the dashboard
        List<String> constituencies = model.getConstituencies();
        if (constituencies != null) {
            view.setPositionsCount(constituencies.size());
        }

        // Count total candidates
        int candidateCount = 0;
        for (String constituency : constituencies) {
            List<String> candidates = model.getCandidatesByConstituency(constituency);
            if (candidates != null) {
                candidateCount += candidates.size();
            }
        }
        view.setCandidatesCount(candidateCount);
        
        // Force refresh of the entire dashboard to update charts
        view.showMainMenu();
    }

    private void handleLogin() {
        view.showLoginScreen(credentials -> {
            String username = credentials[0];
            String password = credentials[1];
            
            if (model.authenticateUser(username, password)) {
                if (!model.hasVoted(username)) {
                    String otp = model.generateOTP();
                    model.storeOTP(username, otp);
                    model.sendOTP(username, otp);
                    String enteredOTP = view.showOTPPrompt();
                    if (enteredOTP != null && model.verifyOTP(username, enteredOTP)) {
                        view.showMessage("Login successful!");
                        handleConstituencySelection(username);
                    } else {
                        view.showMessage("Invalid OTP. Login failed.");
                    }
                } else {
                    view.showMessage("You have already voted!");
                }
            } else {
                view.showMessage("Invalid credentials. Try again.");
            }
        });
    }

    private void handleSignUp() {
        view.showRegistrationWithRoleScreen(credentials -> {
            String username = credentials[0];
            String password = credentials[1];
            String role = credentials[2]; // "admin" or "voter"
            
            if (model.registerUserWithRole(username, password, role)) {
                view.showMessage("Sign-up successful! You can now log in.");
                view.showMainMenu();
            } else {
                view.showMessage("Sign-up failed. Username might already exist.");
            }
        });
    }

    private void handleConstituencySelection(String username) {
        List<String> constituencies = model.getConstituencies();
        view.showConstituencyScreen(constituencies, selectedConstituency -> {
            handleVote(username, selectedConstituency);
        });
    }

    private void handleVote(String username, String constituency) {
        List<String> candidates = model.getCandidatesByConstituency(constituency);
        view.showVoteScreen(candidates, selectedCandidate -> {
            if (model.castVote(username, selectedCandidate)) {
                view.showMessage("Vote successfully cast for " + selectedCandidate + "!");
                int totalVotes = model.getTotalVotes();
                if (totalVotes >= 20) {
                    String winner = model.getWinner();
                    view.showMessage("Maximum votes reached! Winner: " + winner);
                }
                view.showFeedbackScreen(feedback -> {
                    if (model.saveFeedback(username, feedback)) {
                        view.showMessage("Thank you for your feedback!");
                        view.showMainMenu();
                    } else {
                        view.showMessage("Error saving feedback. Please try again.");
                    }
                });
            } else {
                view.showMessage("Error casting vote or maximum votes reached.");
                view.showMainMenu();
            }
        });
    }

    private void handleViewResults() {
        List<String> constituencies = model.getConstituencies();
        view.showConstituencyResultsScreen(constituencies, selectedConstituency -> {
            List<String> results = model.getCandidatesWithVotesByConstituency(selectedConstituency);
            view.showResultsByConstituencyScreen(results);
        });
    }

    private void handleViewFeedbackReport() {
        List<String> feedbackReport = model.getFeedbackReport();
        if (feedbackReport.isEmpty()) {
            view.showMessage("No feedback available.");
            view.showMainMenu();
        } else {
            view.showFeedbackReport(feedbackReport);
        }
    }

    private void handleCandidateRegistration() {
        view.showCandidateRegistrationScreen(candidateInfo -> {
            String candidateName = candidateInfo[0];
            String constituency = candidateInfo[1];
            
            if (model.registerCandidate(candidateName, constituency)) {
                view.showMessage("Candidate " + candidateName + " registered successfully!");
                view.showMainMenu();
            } else {
                view.showMessage("Error registering candidate. It may already exist.");
            }
        });
    }

    private void handleAdminLogin() {
        // Create a simple login dialog for admin
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Administrator Login");
        dialog.setHeaderText("Please enter your administrator credentials");
        
        // Set the button types
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        
        // Create the username and password labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Admin Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Admin Password");
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result to a username-password-pair when the login button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(usernameField.getText(), passwordField.getText());
            }
            return null;
        });
        
        Optional<Pair<String, String>> result = dialog.showAndWait();
        
        result.ifPresent(usernamePassword -> {
            String username = usernamePassword.getKey();
            String password = usernamePassword.getValue();
            
            // Check if the user exists and has admin role, or use default admin credentials
            if ((model.authenticateUser(username, password) && model.isAdmin(username)) || 
                ("admin".equals(username) && model.verifyAdminPassword(password))) {
                view.showAdminPanel();
            } else {
                view.showMessage("Invalid administrator credentials");
            }
        });
    }
}