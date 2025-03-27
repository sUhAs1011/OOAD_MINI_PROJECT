package com.example.voting;

import java.util.List;

public class Controller {
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        run();
    }

    public void run() {
        while (true) {
            String option = view.getInput("Choose an option:\n1. Login\n2. Sign Up\n3. View Results\n4. Register Candidate\n5. Exit");
            switch (option) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleSignUp();
                    break;
                case "3":
                    handleViewResults();
                    break;
                case "4":
                    handleCandidateRegistration();
                    break;
                case "5":
                    view.showMessage("Goodbye!");
                    return;
                default:
                    view.showMessage("Invalid option. Try again.");
            }
        }
    }

    private void handleLogin() {
        String username = view.getInput("Enter Username:");
        String password = view.getInput("Enter Password:");
        if (model.authenticateUser(username, password)) {
            if (!model.hasVoted(username)) {
                view.showMessage("Login successful!");
                String candidate = view.displayCandidateOptions(model.getCandidates());
                if (candidate != null) {
                    if (model.castVote(username, candidate)) {
                        view.showMessage("Vote successfully cast for " + candidate + "!");
                    } else {
                        view.showMessage("Error casting vote. Try again.");
                    }
                } else {
                    view.showMessage("No candidate selected.");
                }
            } else {
                view.showMessage("You have already voted!");
            }
        } else {
            view.showMessage("Invalid credentials. Try again.");
        }
    }

    private void handleSignUp() {
        String username = view.getInput("Enter New Username:");
        String password = view.getInput("Enter New Password:");
        if (model.registerUser(username, password)) {
            view.showMessage("Sign-up successful! You can now log in.");
        } else {
            view.showMessage("Sign-up failed. Username might already exist.");
        }
    }

    private void handleViewResults() {
        List<String> candidatesWithVotes = model.getCandidatesWithVotes();
        view.displayCandidates(candidatesWithVotes); // Display candidates with vote counts
    }

    private void handleCandidateRegistration() {
        String candidateName = view.getCandidateName();
        if (candidateName != null && !candidateName.trim().isEmpty()) {
            if (model.registerCandidate(candidateName)) {
                view.showMessage("Candidate " + candidateName