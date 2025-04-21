package com.example.voting;

import java.util.List;
import java.util.function.Consumer;

public interface ViewInterface {
    // Method to set the model reference
    void setModel(Model model);
    
    // Method to show the main menu
    void showMainMenu();
    
    // Set the main menu action listener
    void setMainMenuListener(Consumer<String> listener);
    
    // Show OTP verification dialog and return entered OTP
    String showOTPPrompt();
    
    // Show voting screen with candidate selection
    void showVoteScreen(List<String> candidates, Consumer<String> voteListener);
    
    // Show constituency selection screen
    void showConstituencyScreen(List<String> constituencies, Consumer<String> constituencyListener);
    
    // Show election results
    void showResultsScreen(List<String> candidatesWithVotes);
    
    // Show feedback collection screen
    void showFeedbackScreen(Consumer<String> feedbackListener);
    
    // Show feedback reports
    void showFeedbackReport(List<String> feedbackList);
    
    // Show generic input dialog
    String getInput(String message);
    
    // Show message dialog
    void showMessage(String message);
    
    // Show constituency-specific results screen
    void showConstituencyResultsScreen(List<String> constituencies, Consumer<String> constituencyListener);
    
    // Show results for a specific constituency
    void showResultsByConstituencyScreen(List<String> candidatesWithVotes);
    
    // Show login screen
    void showLoginScreen(Consumer<String[]> loginListener);
    
    // Show registration screen
    void showRegistrationScreen(Consumer<String[]> registrationListener);
    
    // Show registration screen with role selection (admin or voter)
    void showRegistrationWithRoleScreen(Consumer<String[]> registrationListener);
    
    // Show candidate registration screen
    void showCandidateRegistrationScreen(Consumer<String[]> candidateRegistrationListener);
    
    // Show administrator panel
    void showAdminPanel();
    
    // Update UI elements
    void setPositionsCount(int count);
    void setCandidatesCount(int count);
} 