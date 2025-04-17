package com.example.voting;

import java.util.List;
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
    
    // Dashboard components
    private Label positionsValueLabel;
    private Label candidatesValueLabel;
    
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
        setupStage();
    }
    
    private void setupStage() {
        primaryStage.setTitle("Electronic Voting System");
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
        BorderPane header = createHeader("Dashboard");
        
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
        Label titleLabel = new Label("E-Voting System");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web(LIGHT_TEXT_COLOR));
        titleLabel.setPadding(new Insets(25, 0, 30, 0));
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        
        sideMenu.getChildren().add(titleLabel);
        
        // Menu items
        String[] menuItems = {"Dashboard", "Login", "Sign Up", "View Results", "Register Candidate", "View Feedback Report", "Exit"};
        for (String item : menuItems) {
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
            
            // Add click handler
            menuItemBox.setOnMouseClicked(e -> {
                if (mainMenuListener != null) {
                    mainMenuListener.accept(item);
                }
            });
            
            sideMenu.getChildren().add(menuItemBox);
        }
        
        return sideMenu;
    }
    
    private BorderPane createHeader(String title) {
        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        header.setPadding(new Insets(20, 25, 20, 25));
        
        Label headerLabel = new Label(title);
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
        
        // Create dashboard blocks
        VBox block1 = createDashboardBlock("🏛️", "No. of Positions", "4", "More info", "#9b59b6", "positions");
        VBox block2 = createDashboardBlock("👤", "No. of Candidates", "9", "More info", "#2ecc71", "candidates");
        VBox block3 = createDashboardBlock("🗳️", "Total Votes", "0", "View Details", "#e67e22", "votes");
        VBox block4 = createDashboardBlock("🌍", "Active Constituencies", "4", "View Details", "#3498db", "constituencies");
        
        // Add blocks to grid
        dashboard.add(block1, 0, 0);
        dashboard.add(block2, 1, 0);
        dashboard.add(block3, 0, 1);
        dashboard.add(block4, 1, 1);
        
        // Set column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        dashboard.getColumnConstraints().addAll(col1, col2);
        
        // Set row constraints
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        dashboard.getRowConstraints().addAll(row1, row2);
        
        return dashboard;
    }
    
    private VBox createDashboardBlock(String icon, String title, String value, String buttonText, String colorHex, String identifier) {
        VBox block = new VBox(5);
        block.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        block.setPadding(new Insets(20, 25, 20, 25));
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 36));
        iconLabel.setTextFill(Color.web(colorHex));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", 16));
        titleLabel.setTextFill(Color.web(TEXT_COLOR));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.web(colorHex));
        
        // Store references to value labels
        if (identifier.equals("positions")) {
            positionsValueLabel = valueLabel;
        } else if (identifier.equals("candidates")) {
            candidatesValueLabel = valueLabel;
        }
        
        Button button = new Button(buttonText);
        button.getStyleClass().add("dashboard-button");
        button.setStyle("-fx-text-fill: " + colorHex + "; -fx-border-color: " + colorHex + ";");
        
        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white;");
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorHex + "; -fx-border-color: " + colorHex + ";");
        });
        
        // Add click handler
        button.setOnAction(e -> {
            switch (identifier) {
                case "positions":
                    showMessage("List of available positions in the election system");
                    break;
                case "candidates":
                    showMessage("List of registered candidates across all constituencies");
                    break;
                case "votes":
                    showMessage("Current voting statistics and progress");
                    break;
                case "constituencies":
                    showMessage("Active constituencies in the current election");
                    break;
            }
        });
        
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(button);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        block.getChildren().addAll(iconLabel, titleLabel, valueLabel, buttonBox);
        return block;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");
        
        Label footerLabel = new Label("© 2023 Electronic Voting System | OOAD Mini Project");
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
        if (positionsValueLabel != null) {
            Platform.runLater(() -> positionsValueLabel.setText(String.valueOf(count)));
        }
    }
    
    @Override
    public void setCandidatesCount(int count) {
        if (candidatesValueLabel != null) {
            Platform.runLater(() -> candidatesValueLabel.setText(String.valueOf(count)));
        }
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
        BorderPane header = createHeader("Cast Your Vote");
        
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
        BorderPane header = createHeader("Select Your Constituency");
        
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
        BorderPane header = createHeader("Election Results");
        
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
        BorderPane header = createHeader("Provide Feedback");
        
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
        BorderPane header = createHeader("Feedback Report");
        
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
        BorderPane header = createHeader("View Results by Constituency");
        
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
        BorderPane header = createHeader("Constituency Results");
        
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
        BorderPane header = createHeader("User Login");
        
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
        BorderPane header = createHeader("User Registration");
        
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
    public void showCandidateRegistrationScreen(Consumer<String[]> candidateRegistrationListener) {
        mainLayout.getChildren().clear();
        
        // Create header
        BorderPane header = createHeader("Register New Candidate");
        
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
    
    // Helper class for TableView
    public static class CandidateVote {
        private final javafx.beans.property.SimpleStringProperty candidateName;
        private final javafx.beans.property.SimpleStringProperty votes;
        
        private CandidateVote(String candidateName, String votes) {
            this.candidateName = new javafx.beans.property.SimpleStringProperty(candidateName);
            this.votes = new javafx.beans.property.SimpleStringProperty(votes);
        }
        
        public javafx.beans.property.StringProperty candidateNameProperty() {
            return candidateName;
        }
        
        public javafx.beans.property.StringProperty votesProperty() {
            return votes;
        }
    }
} 