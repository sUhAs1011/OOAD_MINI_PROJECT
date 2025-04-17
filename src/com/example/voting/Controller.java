// Controller.java
package com.example.voting;

import java.util.List;

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
        view.showRegistrationScreen(credentials -> {
            String username = credentials[0];
            String password = credentials[1];
            
            if (model.registerUser(username, password)) {
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
}