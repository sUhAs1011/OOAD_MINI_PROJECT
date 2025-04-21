package com.example.voting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Pair;

public class JavaFXView implements ViewInterface {
    private Stage primaryStage;
    private Scene mainScene;
    private BorderPane mainLayout;
    private Model model;
    private Consumer<String> mainMenuListener;
    private LanguageManager langManager;
    
    // Colors for UI
    private final String PRIMARY_COLOR = "#2980b9";
    private final String ACCENT_COLOR = "#3498db";
    private final String BACKGROUND_COLOR = "#f0f4f8";
    private final String TEXT_COLOR = "#2c3e50";
    private final String MENU_BG_COLOR = "#2c3e50";
    private final String MENU_HOVER_COLOR = "#34495e";
    private final String LIGHT_TEXT_COLOR = "#ecf0f1";
    
    public JavaFXView(Stage stage) {
        this.primaryStage = stage;
        this.langManager = LanguageManager.getInstance();
        setupStage();
    }
    
    private void setupStage() {
        primaryStage.setTitle(langManager.getMessage("app.title"));
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        
        mainLayout = new BorderPane();
        mainScene = new Scene(mainLayout);
        
        // Load CSS if available
        try {
            String cssPath = "/styles/main.css";
            java.net.URL cssResource = getClass().getResource(cssPath);
            if (cssResource != null) {
                mainScene.getStylesheets().add(cssResource.toExternalForm());
            } else {
                System.out.println("Warning: CSS file not found at: " + cssPath);
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load CSS file: " + e.getMessage());
        }
        
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    
    @Override
    public void setModel(Model model) {
        this.model = model;
    }
    
    @Override
    public void showMainMenu() {
        mainLayout.getChildren().clear();
        
        // Create side menu
        VBox sideMenu = createSideMenu();
        mainLayout.setLeft(sideMenu);
        
        // Create header
        BorderPane header = createHeader("dashboard.title");
        
        // Create dashboard content
        GridPane dashboardContent = createDashboardContent();
        
        // Create footer
        HBox footer = createFooter();
        
        // Create content panel
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(dashboardContent);
        contentPanel.setBottom(footer);
        contentPanel.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        mainLayout.setCenter(contentPanel);
    }
    
    private VBox createSideMenu() {
        VBox sideMenu = new VBox();
        sideMenu.setPrefWidth(220);
        sideMenu.setStyle("-fx-background-color: linear-gradient(to bottom, " + MENU_BG_COLOR + ", " + MENU_HOVER_COLOR + ");");
        
        // Title
        Label titleLabel = new Label(langManager.getMessage("app.title"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web(LIGHT_TEXT_COLOR));
        titleLabel.setPadding(new Insets(25, 0, 30, 0));
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        
        sideMenu.getChildren().add(titleLabel);
        
        // Menu items with localized text
        String[] menuKeys = {
            "dashboard.title", "login.label", "signup.label", 
            "viewresults.label", "registercandidate.label", 
            "viewfeedback.label", "admin.login.label", "exit.label"
        };
        
        for (String key : menuKeys) {
            String item = langManager.getMessage(key);
            HBox menuItemBox = new HBox();
            menuItemBox.setAlignment(Pos.CENTER_LEFT);
            menuItemBox.setPadding(new Insets(12, 10, 12, 25));
            menuItemBox.setPrefHeight(50);
            menuItemBox.setMaxWidth(Double.MAX_VALUE);
            menuItemBox.getStyleClass().add("menu-item");
            
            Label menuLabel = new Label(item);
            menuLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));
            menuLabel.setTextFill(Color.web(LIGHT_TEXT_COLOR));
            
            menuItemBox.getChildren().add(menuLabel);
            
            // Add hover effect
            menuItemBox.setOnMouseEntered(e -> {
                menuItemBox.setStyle("-fx-background-color: " + MENU_HOVER_COLOR + ";");
            });
            
            menuItemBox.setOnMouseExited(e -> {
                menuItemBox.setStyle("-fx-background-color: transparent;");
            });
            
            // Store original key for mapping back to English menu item
            String originalKey = key;
            
            // Add click handler
            menuItemBox.setOnMouseClicked(e -> {
                if (mainMenuListener != null) {
                    // Get the English version of the menu item for the controller
                    String englishItem = getEnglishMenuItemFromKey(originalKey);
                    mainMenuListener.accept(englishItem);
                }
            });
            
            sideMenu.getChildren().add(menuItemBox);
        }
        
        return sideMenu;
    }
    
    /**
     * Maps the resource key back to the English menu item
     * This is needed because the Controller expects English menu items
     */
    private String getEnglishMenuItemFromKey(String key) {
        switch (key) {
            case "dashboard.title": return "Dashboard";
            case "login.label": return "Login";
            case "signup.label": return "Sign Up";
            case "viewresults.label": return "View Results";
            case "registercandidate.label": return "Register Candidate";
            case "viewfeedback.label": return "View Feedback Report";
            case "admin.login.label": return "Admin Login";
            case "exit.label": return "Exit";
            default: return key;
        }
    }
    
    private BorderPane createHeader(String titleKey) {
        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        header.setPadding(new Insets(20, 25, 20, 25));
        
        Label headerLabel = new Label(langManager.getMessage(titleKey));
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web(TEXT_COLOR));
        
        header.setLeft(headerLabel);
        return header;
    }
    
    private GridPane createDashboardContent() {
        GridPane dashboard = new GridPane();
        dashboard.setHgap(30);
        dashboard.setVgap(30);
        dashboard.setPadding(new Insets(30));
        dashboard.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create a heading for the constituency charts section
        Label chartsHeading = new Label("Constituency Vote Distribution");
        chartsHeading.setFont(Font.font("System", FontWeight.BOLD, 24));
        chartsHeading.setTextFill(Color.web(TEXT_COLOR));
        chartsHeading.setPadding(new Insets(0, 0, 20, 0));
        
        dashboard.add(chartsHeading, 0, 0, 2, 1);
        
        // Create constituency pie charts
        if (model != null) {
            Map<String, List<CandidateVote>> constituencyVotes = model.getCandidateVotesByConstituencyForCharts();
            if (constituencyVotes != null && !constituencyVotes.isEmpty()) {
                // Create a scrollable container for pie charts
                ScrollPane chartsScroll = new ScrollPane();
                chartsScroll.setFitToWidth(true);
                chartsScroll.setPrefHeight(600); // Increased height since we removed the stat blocks
                chartsScroll.setStyle("-fx-background-color: transparent;");
                
                // Create a FlowPane to hold the charts
                FlowPane chartsContainer = new FlowPane();
                chartsContainer.setHgap(30);
                chartsContainer.setVgap(30);
                chartsContainer.setPadding(new Insets(10));
                chartsContainer.setAlignment(Pos.CENTER);
                
                // Add a pie chart for each constituency
                for (Map.Entry<String, List<CandidateVote>> entry : constituencyVotes.entrySet()) {
                    String constituency = entry.getKey();
                    List<CandidateVote> votes = entry.getValue();
                    
                    VBox chartBox = createConstituencyPieChart(constituency, votes);
                    chartsContainer.getChildren().add(chartBox);
                }
                
                chartsScroll.setContent(chartsContainer);
                dashboard.add(chartsScroll, 0, 1, 2, 1);
            } else {
                // Show a message if no constituency data is available
                VBox noDataBox = new VBox(20);
                noDataBox.setAlignment(Pos.CENTER);
                noDataBox.setPadding(new Insets(50, 0, 0, 0));
                
                Label noDataLabel = new Label("No voting data available");
                noDataLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
                noDataLabel.setTextFill(Color.web("#999999"));
                
                Label instructionLabel = new Label("Register candidates and cast votes to see voting statistics");
                instructionLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));
                instructionLabel.setTextFill(Color.web("#999999"));
                
                Button registerButton = createPrimaryButton("Register Candidates");
                registerButton.setOnAction(e -> {
                    if (mainMenuListener != null) {
                        mainMenuListener.accept("Register Candidate");
                    }
                });
                
                noDataBox.getChildren().addAll(noDataLabel, instructionLabel, registerButton);
                dashboard.add(noDataBox, 0, 1, 2, 1);
            }
        }
        
        // Set column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        dashboard.getColumnConstraints().addAll(col1, col2);
        
        // Set row constraints - header and content
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(10);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(90);
        dashboard.getRowConstraints().addAll(row1, row2);
        
        return dashboard;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");
        
        Label footerLabel = new Label(langManager.getMessage("footer.copyright"));
        footerLabel.setFont(Font.font("System", 12));
        footerLabel.setTextFill(Color.web("#808080"));
        
        footer.getChildren().add(footerLabel);
        return footer;
    }
    
    @Override
    public void setMainMenuListener(Consumer<String> listener) {
        this.mainMenuListener = listener;
    }
    
    @Override
    public void setPositionsCount(int count) {
        // This method is no longer used since we removed the dashboard blocks
        // But we keep it for compatibility with the interface
    }
    
    @Override
    public void setCandidatesCount(int count) {
        // This method is no longer used since we removed the dashboard blocks
        // But we keep it for compatibility with the interface
    }
    
    @Override
    public String showOTPPrompt() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("OTP Verification");
        dialog.setHeaderText("Please enter the One-Time Password (OTP) sent to your email");
        
        ButtonType confirmButtonType = new ButtonType("Verify", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField otpField = new TextField();
        otpField.setPromptText("Enter OTP");
        
        grid.add(new Label("OTP:"), 0, 0);
        grid.add(otpField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        Platform.runLater(otpField::requestFocus);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return otpField.getText();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    @Override
    public void showVoteScreen(List<String> candidates, Consumer<String> voteListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("castyourvote.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        Label instructionLabel = new Label("Please select your preferred candidate:");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        instructionLabel.setTextFill(Color.web(TEXT_COLOR));
        
        VBox candidatesBox = new VBox(5);
        candidatesBox.setPadding(new Insets(20));
        candidatesBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        ToggleGroup candidateGroup = new ToggleGroup();
        
        for (String candidate : candidates) {
            RadioButton radioButton = new RadioButton(candidate);
            radioButton.setToggleGroup(candidateGroup);
            radioButton.setFont(Font.font("System", 14));
            radioButton.setPadding(new Insets(8, 0, 8, 0));
            candidatesBox.getChildren().add(radioButton);
        }
        
        // Add NOTA option
        RadioButton notaButton = new RadioButton("None of the Above (NOTA)");
        notaButton.setToggleGroup(candidateGroup);
        notaButton.setFont(Font.font("System", 14));
        notaButton.setPadding(new Insets(8, 0, 8, 0));
        candidatesBox.getChildren().add(notaButton);
        
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button voteButton = createPrimaryButton("Submit Vote");
        Button backButton = createSecondaryButton("Back to Main Menu");
        
        voteButton.setOnAction(e -> {
            RadioButton selectedButton = (RadioButton) candidateGroup.getSelectedToggle();
            if (selectedButton != null) {
                voteListener.accept(selectedButton.getText());
            } else {
                showMessage("Please select a candidate to proceed.");
            }
        });
        
        backButton.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(voteButton, backButton);
        
        content.getChildren().addAll(instructionLabel, candidatesBox, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showConstituencyScreen(List<String> constituencies, Consumer<String> constituencyListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("selectyourconstituency.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        Label instructionLabel = new Label("Please select your constituency:");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        instructionLabel.setTextFill(Color.web(TEXT_COLOR));
        
        VBox constituenciesBox = new VBox(5);
        constituenciesBox.setPadding(new Insets(20));
        constituenciesBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        ToggleGroup constituencyGroup = new ToggleGroup();
        
        for (String constituency : constituencies) {
            RadioButton radioButton = new RadioButton(constituency);
            radioButton.setToggleGroup(constituencyGroup);
            radioButton.setFont(Font.font("System", 14));
            radioButton.setPadding(new Insets(8, 0, 8, 0));
            constituenciesBox.getChildren().add(radioButton);
        }
        
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button continueButton = createPrimaryButton("Continue");
        Button backButton = createSecondaryButton("Back to Main Menu");
        
        continueButton.setOnAction(e -> {
            RadioButton selectedButton = (RadioButton) constituencyGroup.getSelectedToggle();
            if (selectedButton != null) {
                constituencyListener.accept(selectedButton.getText());
            } else {
                showMessage("Please select a constituency to proceed.");
            }
        });
        
        backButton.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(continueButton, backButton);
        
        content.getChildren().addAll(instructionLabel, constituenciesBox, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showResultsScreen(List<String> candidatesWithVotes) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("electionresults.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create table
        TableView<CandidateVote> table = new TableView<>();
        table.setEditable(false);
        
        TableColumn<CandidateVote, String> candidateColumn = new TableColumn<>("Candidate");
        candidateColumn.setCellValueFactory(cellData -> cellData.getValue().candidateNameProperty());
        candidateColumn.setPrefWidth(300);
        
        TableColumn<CandidateVote, String> votesColumn = new TableColumn<>("Votes");
        votesColumn.setCellValueFactory(cellData -> cellData.getValue().votesProperty());
        votesColumn.setPrefWidth(100);
        votesColumn.setStyle("-fx-alignment: CENTER;");
        
        table.getColumns().addAll(candidateColumn, votesColumn);
        
        // Add data to table
        for (String candidateWithVotes : candidatesWithVotes) {
            String[] parts = candidateWithVotes.split(" - Votes: ");
            table.getItems().add(new CandidateVote(parts[0], parts[1]));
        }
        
        Button backButton = createPrimaryButton("Back to Main Menu");
        backButton.setOnAction(e -> showMainMenu());
        
        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(backButton);
        
        content.getChildren().addAll(table, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showFeedbackScreen(Consumer<String> feedbackListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("providefeedback.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        Label instructionLabel = new Label("Please share your voting experience:");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        instructionLabel.setTextFill(Color.web(TEXT_COLOR));
        
        TextArea feedbackArea = new TextArea();
        feedbackArea.setPrefRowCount(8);
        feedbackArea.setWrapText(true);
        feedbackArea.setFont(Font.font("System", 14));
        
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button submitButton = createPrimaryButton("Submit Feedback");
        Button skipButton = createSecondaryButton("Skip");
        
        submitButton.setOnAction(e -> {
            String feedback = feedbackArea.getText().trim();
            if (feedback.isEmpty()) {
                showMessage("Please enter your feedback before submitting.");
            } else {
                feedbackListener.accept(feedback);
            }
        });
        
        skipButton.setOnAction(e -> feedbackListener.accept(""));
        
        buttonBox.getChildren().addAll(skipButton, submitButton);
        
        content.getChildren().addAll(instructionLabel, feedbackArea, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showFeedbackReport(List<String> feedbackList) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("feedbackreport.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create scrollable feedback list
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        VBox feedbackContainer = new VBox(10);
        feedbackContainer.setPadding(new Insets(10));
        
        for (String feedback : feedbackList) {
            VBox feedbackBox = new VBox(5);
            feedbackBox.setPadding(new Insets(15));
            feedbackBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
            
            String[] parts = feedback.split(" \\| Feedback: ");
            String username = parts[0].substring(6); // Remove "User: "
            String feedbackText = parts.length > 1 ? parts[1] : "";
            
            Label usernameLabel = new Label(username);
            usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            usernameLabel.setTextFill(Color.web(PRIMARY_COLOR));
            
            Label feedbackTextLabel = new Label(feedbackText);
            feedbackTextLabel.setFont(Font.font("System", 14));
            feedbackTextLabel.setTextFill(Color.web(TEXT_COLOR));
            feedbackTextLabel.setWrapText(true);
            
            feedbackBox.getChildren().addAll(usernameLabel, feedbackTextLabel);
            feedbackContainer.getChildren().add(feedbackBox);
        }
        
        scrollPane.setContent(feedbackContainer);
        
        Button backButton = createPrimaryButton("Back to Main Menu");
        backButton.setOnAction(e -> showMainMenu());
        
        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(backButton);
        
        content.getChildren().addAll(scrollPane, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public String getInput(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    @Override
    public void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("E-Voting System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void showConstituencyResultsScreen(List<String> constituencies, Consumer<String> constituencyListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("viewresultsbyconstituency.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        Label instructionLabel = new Label("Select a constituency to view results:");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        instructionLabel.setTextFill(Color.web(TEXT_COLOR));
        
        VBox constituenciesBox = new VBox(5);
        constituenciesBox.setPadding(new Insets(20));
        constituenciesBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        ToggleGroup constituencyGroup = new ToggleGroup();
        
        for (String constituency : constituencies) {
            RadioButton radioButton = new RadioButton(constituency);
            radioButton.setToggleGroup(constituencyGroup);
            radioButton.setFont(Font.font("System", 14));
            radioButton.setPadding(new Insets(8, 0, 8, 0));
            constituenciesBox.getChildren().add(radioButton);
        }
        
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button viewButton = createPrimaryButton("View Results");
        Button backButton = createSecondaryButton("Back to Main Menu");
        
        viewButton.setOnAction(e -> {
            RadioButton selectedButton = (RadioButton) constituencyGroup.getSelectedToggle();
            if (selectedButton != null) {
                constituencyListener.accept(selectedButton.getText());
            } else {
                showMessage("Please select a constituency to view results.");
            }
        });
        
        backButton.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(viewButton, backButton);
        
        content.getChildren().addAll(instructionLabel, constituenciesBox, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showResultsByConstituencyScreen(List<String> candidatesWithVotes) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("constituencyresults.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create table
        TableView<CandidateVote> table = new TableView<>();
        table.setEditable(false);
        
        TableColumn<CandidateVote, String> candidateColumn = new TableColumn<>("Candidate");
        candidateColumn.setCellValueFactory(cellData -> cellData.getValue().candidateNameProperty());
        candidateColumn.setPrefWidth(300);
        
        TableColumn<CandidateVote, String> votesColumn = new TableColumn<>("Votes");
        votesColumn.setCellValueFactory(cellData -> cellData.getValue().votesProperty());
        votesColumn.setPrefWidth(100);
        votesColumn.setStyle("-fx-alignment: CENTER;");
        
        table.getColumns().addAll(candidateColumn, votesColumn);
        
        // Add data to table
        for (String candidateWithVotes : candidatesWithVotes) {
            String[] parts = candidateWithVotes.split(" - Votes: ");
            table.getItems().add(new CandidateVote(parts[0], parts[1]));
        }
        
        // Create buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button backToConsButton = createPrimaryButton("Back to Constituencies");
        Button mainMenuButton = createSecondaryButton("Back to Main Menu");
        
        List<String> constituencies = getConstituencies();
        
        backToConsButton.setOnAction(e -> 
            showConstituencyResultsScreen(constituencies, this::handleConstituencySelection));
        
        mainMenuButton.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(mainMenuButton, backToConsButton);
        
        content.getChildren().addAll(table, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    private void handleConstituencySelection(String constituency) {
        // This is a placeholder - this would need to be connected to the Controller
        showMessage("You would see results for " + constituency + " here.");
    }
    
    private List<String> getConstituencies() {
        // This is a placeholder - in practice would get data from the model via controller
        return java.util.Arrays.asList("North District", "South District", "East District", "West District");
    }
    
    @Override
    public void showLoginScreen(Consumer<String[]> loginListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("userlogin.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(50, 100, 50, 100));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(20);
        form.setAlignment(Pos.CENTER);
        
        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        
        form.add(usernameLabel, 0, 0);
        form.add(usernameField, 1, 0);
        form.add(passwordLabel, 0, 1);
        form.add(passwordField, 1, 1);
        
        // Create buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);
        
        Button loginButton = createPrimaryButton("Login");
        Button backButton = createSecondaryButton("Back to Main Menu");
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty()) {
                showMessage("Username cannot be empty");
                return;
            }
            
            if (password.isEmpty()) {
                showMessage("Password cannot be empty");
                return;
            }
            
            loginListener.accept(new String[]{username, password});
        });
        
        backButton.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(loginButton, backButton);
        
        content.getChildren().addAll(form, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showRegistrationScreen(Consumer<String[]> registrationListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("userregistration.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(50, 100, 50, 100));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(20);
        form.setAlignment(Pos.CENTER);
        
        Label usernameLabel = new Label("Choose a Username");
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        
        Label passwordLabel = new Label("Choose a Password");
        passwordLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        
        form.add(usernameLabel, 0, 0);
        form.add(usernameField, 1, 0);
        form.add(passwordLabel, 0, 1);
        form.add(passwordField, 1, 1);
        
        // Create buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);
        
        Button registerButton = createPrimaryButton("Register");
        Button backButton = createSecondaryButton("Back to Main Menu");
        
        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty()) {
                showMessage("Username cannot be empty");
                return;
            }
            
            if (password.isEmpty()) {
                showMessage("Password cannot be empty");
                return;
            }
            
            registrationListener.accept(new String[]{username, password});
        });
        
        backButton.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(registerButton, backButton);
        
        content.getChildren().addAll(form, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showRegistrationWithRoleScreen(Consumer<String[]> registrationListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("userregistration.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(50, 100, 50, 100));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(20);
        form.setAlignment(Pos.CENTER);
        
        Label usernameLabel = new Label("Choose a Username");
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        
        Label passwordLabel = new Label("Choose a Password");
        passwordLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        
        Label roleLabel = new Label("Select Role");
        roleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("voter", "admin");
        roleComboBox.setValue("voter"); // Default value
        roleComboBox.setMinWidth(200);
        
        form.add(usernameLabel, 0, 0);
        form.add(usernameField, 1, 0);
        form.add(passwordLabel, 0, 1);
        form.add(passwordField, 1, 1);
        form.add(roleLabel, 0, 2);
        form.add(roleComboBox, 1, 2);
        
        // Create buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);
        
        Button registerButton = createPrimaryButton("Register");
        Button backButton = createSecondaryButton("Back to Main Menu");
        
        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            
            if (username.isEmpty()) {
                showMessage("Username cannot be empty");
                return;
            }
            
            if (password.isEmpty()) {
                showMessage("Password cannot be empty");
                return;
            }
            
            registrationListener.accept(new String[]{username, password, role});
        });
        
        backButton.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(registerButton, backButton);
        
        content.getChildren().addAll(form, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showCandidateRegistrationScreen(Consumer<String[]> candidateRegistrationListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("registernewcandidate.title");
        
        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(50, 100, 50, 100));
        content.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(20);
        form.setAlignment(Pos.CENTER);
        
        Label nameLabel = new Label("Candidate Name");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Enter candidate name");
        
        Label constituencyLabel = new Label("Constituency");
        constituencyLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TextField constituencyField = new TextField();
        constituencyField.setPromptText("Enter constituency");
        
        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);
        form.add(constituencyLabel, 0, 1);
        form.add(constituencyField, 1, 1);
        
        // Create buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);
        
        Button registerButton = createPrimaryButton("Register Candidate");
        Button backButton = createSecondaryButton("Back to Main Menu");
        
        registerButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String constituency = constituencyField.getText().trim();
            
            if (name.isEmpty()) {
                showMessage("Candidate name cannot be empty");
                return;
            }
            
            if (constituency.isEmpty()) {
                showMessage("Constituency cannot be empty");
                return;
            }
            
            candidateRegistrationListener.accept(new String[]{name, constituency});
        });
        
        backButton.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(registerButton, backButton);
        
        content.getChildren().addAll(form, buttonBox);
        
        // Create footer
        HBox footer = createFooter();
        
        // Update layout
        BorderPane contentPanel = new BorderPane();
        contentPanel.setTop(header);
        contentPanel.setCenter(content);
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    @Override
    public void showAdminPanel() {
        // Create and show the admin panel
        AdminView adminView = new AdminView(primaryStage, model, this);
        adminView.show();
    }
    
    // Helper methods for UI components
    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        button.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;");
        button.setPrefHeight(40);
        button.setMinWidth(120);
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: white;"));
        
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;"));
        
        return button;
    }
    
    private Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        button.setStyle("-fx-background-color: white; -fx-text-fill: " + TEXT_COLOR + "; -fx-border-color: #c8c8c8;");
        button.setPrefHeight(40);
        button.setMinWidth(120);
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: " + TEXT_COLOR + "; -fx-border-color: #c8c8c8;"));
        
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: white; -fx-text-fill: " + TEXT_COLOR + "; -fx-border-color: #c8c8c8;"));
        
        return button;
    }
    
    /**
     * Create a pie chart for a specific constituency
     */
    private VBox createConstituencyPieChart(String constituency, List<CandidateVote> votes) {
        VBox chartBox = new VBox(15);
        chartBox.setPadding(new Insets(20));
        chartBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        chartBox.setPrefWidth(450);
        chartBox.setMaxWidth(450);
        
        // Create title for the chart
        Label titleLabel = new Label(constituency);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web(TEXT_COLOR));
        
        // Create pie chart
        javafx.collections.ObservableList<javafx.scene.chart.PieChart.Data> pieChartData = 
            javafx.collections.FXCollections.observableArrayList();
        
        // Add data for each candidate
        for (CandidateVote vote : votes) {
            String candidateName = vote.getCandidateName();
            int voteCount = Integer.parseInt(vote.getVotes());
            pieChartData.add(new javafx.scene.chart.PieChart.Data(candidateName + " (" + voteCount + ")", voteCount));
        }
        
        javafx.scene.chart.PieChart chart = new javafx.scene.chart.PieChart(pieChartData);
        chart.setTitle("Vote Distribution");
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        chart.setLegendSide(javafx.geometry.Side.RIGHT);
        chart.setPrefHeight(300);
        
        // Calculate total votes for this constituency
        int totalVotes = votes.stream()
            .mapToInt(v -> Integer.parseInt(v.getVotes()))
            .sum();
        
        // Identify the leading candidate
        CandidateVote leadingCandidate = votes.stream()
            .max((v1, v2) -> Integer.compare(
                Integer.parseInt(v1.getVotes()), 
                Integer.parseInt(v2.getVotes())))
            .orElse(null);
        
        // Add leading candidate and total votes info
        if (leadingCandidate != null) {
            HBox infoBox = new HBox(30);
            infoBox.setAlignment(Pos.CENTER);
            
            VBox leadingBox = new VBox(5);
            leadingBox.setAlignment(Pos.CENTER);
            Label leadingLabel = new Label("Leading Candidate");
            leadingLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
            Label leadingValueLabel = new Label(leadingCandidate.getCandidateName());
            leadingValueLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            leadingValueLabel.setTextFill(Color.web("#2ecc71"));
            leadingBox.getChildren().addAll(leadingLabel, leadingValueLabel);
            
            VBox votesBox = new VBox(5);
            votesBox.setAlignment(Pos.CENTER);
            Label votesLabel = new Label("Total Votes");
            votesLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
            Label votesValueLabel = new Label(String.valueOf(totalVotes));
            votesValueLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            votesValueLabel.setTextFill(Color.web("#e67e22"));
            votesBox.getChildren().addAll(votesLabel, votesValueLabel);
            
            infoBox.getChildren().addAll(leadingBox, votesBox);
            chartBox.getChildren().addAll(titleLabel, chart, infoBox);
        } else {
            chartBox.getChildren().addAll(titleLabel, chart);
        }
        
        return chartBox;
    }
    
    // Helper class for TableView - Making constructor public to allow Model to create instances
    public static class CandidateVote {
        private final javafx.beans.property.SimpleStringProperty candidateName;
        private final javafx.beans.property.SimpleStringProperty votes;
        
        public CandidateVote(String candidateName, String votes) {
            this.candidateName = new javafx.beans.property.SimpleStringProperty(candidateName);
            this.votes = new javafx.beans.property.SimpleStringProperty(votes);
        }
        
        public javafx.beans.property.StringProperty candidateNameProperty() {
            return candidateName;
        }
        
        public javafx.beans.property.StringProperty votesProperty() {
            return votes;
        }
        
        public String getCandidateName() {
            return candidateName.get();
        }
        
        public String getVotes() {
            return votes.get();
        }
    }
} 