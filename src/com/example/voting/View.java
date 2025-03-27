package com.example.voting;

import java.util.List;
import javax.swing.JOptionPane;

public class View {
    public String getInput(String message) {
        return JOptionPane.showInputDialog(message);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public void displayCandidates(List<String> candidates) {
        StringBuilder message = new StringBuilder("Candidates:\n");
        for (String candidate : candidates) {
            message.append("- ").append(candidate).append("\n");
        }
        JOptionPane.showMessageDialog(null, message.toString());
    }

    public String displayCandidateOptions(List<String> candidates) {
        String[] candidateArray = candidates.toArray(new String[0]);
        String selectedCandidate = (String) JOptionPane.showInputDialog(
            null,
            "Select a candidate to vote for:",
            "Vote",
            JOptionPane.PLAIN_MESSAGE,
            null,
            candidateArray,
            candidateArray[0]
        );
        return selectedCandidate;
    }

    public String getCandidateName() {
        return JOptionPane.showInputDialog("Enter the name of the new candidate:");
    }
}