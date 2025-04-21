package com.example.voting;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;

import java.util.List;
import java.util.function.Consumer;

/**
 * AdminView class to handle the administrative interface
 */
public class AdminView {
    private Stage primaryStage;
    private Scene adminScene;
    private Scene previousScene; // Store the previous scene
    private BorderPane mainLayout;
    private Model model;
    private LanguageManager langManager;
    private ViewInterface parentView;
    
    // Colors for UI
    private final String PRIMARY_COLOR = "#2980b9";
    private final String SECONDARY_COLOR = "#e74c3c";
    private final String ACCENT_COLOR = "#3498db";
    private final String BACKGROUND_COLOR = "#f0f4f8";
    private final String TEXT_COLOR = "#2c3e50";
    private final String LIGHT_TEXT_COLOR = "#ecf0f1";
    
    /**
     * Create a new AdminView
     * @param primaryStage The primary stage
     * @param model The model
     * @param parentView The parent view to return to when admin panel is closed
     */
    public AdminView(Stage primaryStage, Model model, ViewInterface parentView) {
        this.primaryStage = primaryStage;
        this.model = model;
        this.parentView = parentView;
        this.langManager = LanguageManager.getInstance();
        
        setupAdminInterface();
    }
    
    /**
     * Setup the admin interface
     */
    private void setupAdminInterface() {
        mainLayout = new BorderPane();
        adminScene = new Scene(mainLayout, 1024, 768);
        
        // Load CSS if available
        try {
            String cssPath = "/styles/main.css";
            java.net.URL cssResource = getClass().getResource(cssPath);
            if (cssResource != null) {
                adminScene.getStylesheets().add(cssResource.toExternalForm());
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load CSS file: " + e.getMessage());
        }
        
        // Create header
        HBox header = createHeader();
        mainLayout.setTop(header);
        
        // Create tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Dashboard tab
        Tab dashboardTab = new Tab(langManager.getMessage("admin.dashboard"));
        dashboardTab.setContent(createDashboardContent());
        
        // Candidate Management tab
        Tab candidateTab = new Tab(langManager.getMessage("admin.candidates"));
        ScrollPane candidateScrollPane = new ScrollPane(createCandidateManagementContent());
        candidateScrollPane.setFitToWidth(true);
        candidateScrollPane.setPannable(true);
        candidateTab.setContent(candidateScrollPane);
        
        // System Management tab
        Tab systemTab = new Tab(langManager.getMessage("admin.system"));
        ScrollPane systemScrollPane = new ScrollPane(createSystemManagementContent());
        systemScrollPane.setFitToWidth(true);
        systemScrollPane.setPannable(true);
        systemTab.setContent(systemScrollPane);
        
        tabPane.getTabs().addAll(dashboardTab, candidateTab, systemTab);
        
        // Create content panel
        BorderPane contentPanel = new BorderPane();
        contentPanel.setCenter(tabPane);
        contentPanel.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create footer
        HBox footer = createFooter();
        contentPanel.setBottom(footer);
        
        mainLayout.setCenter(contentPanel);
    }
    
    /**
     * Show the admin interface
     */
    public void show() {
        // Save the previous scene
        this.previousScene = primaryStage.getScene();
        
        // Set up the admin panel
        primaryStage.setTitle(langManager.getMessage("admin.title"));
        primaryStage.setScene(adminScene);
        
        // Add a close handler to go back to the main view
        primaryStage.setOnCloseRequest(e -> {
            e.consume(); // Prevent the window from closing
            exitAdminPanel();
        });
    }
    
    /**
     * Exit the admin panel and return to main view
     */
    private void exitAdminPanel() {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(langManager.getMessage("admin.exit.title"));
        alert.setHeaderText(null);
        alert.setContentText(langManager.getMessage("admin.exit.message"));
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            // Remove the close handler
            primaryStage.setOnCloseRequest(null);
            
            // Restore the previous scene
            if (previousScene != null) {
                primaryStage.setScene(previousScene);
            }
            
            // Reset the window title
            primaryStage.setTitle(langManager.getMessage("app.title"));
            
            // Tell the parent view to show the main menu
            if (parentView != null) {
                parentView.showMainMenu();
            }
        }
    }
    
    /**
     * Create the header for the admin panel
     */
    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);
        header.setStyle("-fx-background-color: " + PRIMARY_COLOR + ";");
        
        Label titleLabel = new Label(langManager.getMessage("admin.title"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.WHITE);
        
        Button exitButton = new Button(langManager.getMessage("admin.exit"));
        exitButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white;");
        exitButton.setOnAction(e -> exitAdminPanel());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, exitButton);
        return header;
    }
    
    /**
     * Create the footer
     */
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");
        
        Label footerLabel = new Label(langManager.getMessage("admin.footer"));
        footerLabel.setFont(Font.font("System", 12));
        footerLabel.setTextFill(Color.web("#808080"));
        
        footer.getChildren().add(footerLabel);
        return footer;
    }
    
    /**
     * Create the dashboard content
     */
    private VBox createDashboardContent() {
        VBox dashboard = new VBox(30); // Increased spacing between elements
        dashboard.setPadding(new Insets(30));
        
        // Summary statistics
        HBox statsBox = createSummaryStats();
        
        // Vote distribution chart
        PieChart voteChart = createVoteDistributionChart();
        
        // Voter turnout chart
        HBox turnoutBox = createVoterTurnoutBox();
        
        dashboard.getChildren().addAll(
            createSectionHeader(langManager.getMessage("admin.summary")),
            statsBox,
            createSectionHeader(langManager.getMessage("admin.vote.distribution")),
            voteChart,
            createSectionHeader(langManager.getMessage("admin.voter.turnout")),
            turnoutBox
        );
        
        // Wrap the dashboard in a ScrollPane to enable scrolling
        ScrollPane scrollPane = new ScrollPane(dashboard);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        
        // Create a container for the ScrollPane
        VBox container = new VBox();
        container.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return container;
    }
    
    /**
     * Create summary statistics boxes
     */
    private HBox createSummaryStats() {
        HBox statsBox = new HBox(20);
        
        VBox totalVotesBox = createStatBox("🗳️", langManager.getMessage("admin.total.votes"), 
            String.valueOf(model.getTotalVotes()), "#3498db");
            
        VBox totalCandidatesBox = createStatBox("👤", langManager.getMessage("admin.total.candidates"), 
            String.valueOf(model.getTotalCandidates()), "#27ae60");
            
        VBox totalUsersBox = createStatBox("👥", langManager.getMessage("admin.total.users"), 
            String.valueOf(model.getTotalUsers()), "#9b59b6");
            
        VBox totalConstituenciesBox = createStatBox("🌍", langManager.getMessage("admin.total.constituencies"), 
            String.valueOf(model.getTotalConstituencies()), "#e67e22");
        
        statsBox.getChildren().addAll(totalVotesBox, totalCandidatesBox, totalUsersBox, totalConstituenciesBox);
        return statsBox;
    }
    
    /**
     * Create a statistics box
     */
    private VBox createStatBox(String icon, String title, String value, String colorHex) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5);");
        box.setMinWidth(200);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 36));
        iconLabel.setTextFill(Color.web(colorHex));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", 14));
        titleLabel.setTextFill(Color.web(TEXT_COLOR));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.web(colorHex));
        
        box.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return box;
    }
    
    /**
     * Create a vote distribution chart
     */
    private PieChart createVoteDistributionChart() {
        // Get candidate vote data from model
        List<CandidateVote> candidateVotes = model.getCandidateVotesForChart();
        
        // Create pie chart data
        PieChart.Data[] pieChartData = new PieChart.Data[candidateVotes.size()];
        for (int i = 0; i < candidateVotes.size(); i++) {
            CandidateVote vote = candidateVotes.get(i);
            pieChartData[i] = new PieChart.Data(
                vote.getCandidateName() + " (" + vote.getVotes() + ")", 
                vote.getVotes()
            );
        }
        
        PieChart chart = new PieChart(FXCollections.observableArrayList(pieChartData));
        chart.setTitle(langManager.getMessage("admin.votes.by.candidate"));
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        
        // Set preferred height to limit the pie chart size
        chart.setPrefHeight(350);
        chart.setMaxHeight(350);
        
        return chart;
    }
    
    /**
     * Create voter turnout box
     */
    private HBox createVoterTurnoutBox() {
        HBox turnoutBox = new HBox(30);
        turnoutBox.setPadding(new Insets(20));
        turnoutBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5);");
        turnoutBox.setAlignment(Pos.CENTER_LEFT);
        turnoutBox.setMinHeight(200); // Set minimum height
        
        // Make the turnout box responsive
        turnoutBox.setMaxWidth(Double.MAX_VALUE);
        
        VBox statsBox = new VBox(10);
        
        // Get voter turnout data
        int totalUsers = model.getTotalUsers();
        int votedUsers = model.getVotedUsers();
        int nonVotedUsers = totalUsers - votedUsers;
        double turnoutPercentage = totalUsers > 0 ? ((double) votedUsers / totalUsers) * 100 : 0;
        
        Label titleLabel = new Label(langManager.getMessage("admin.voter.turnout"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web(TEXT_COLOR));
        
        Label totalUsersLabel = new Label(langManager.getMessage("admin.total.users") + ": " + totalUsers);
        totalUsersLabel.setFont(Font.font("System", 14));
        
        Label votedUsersLabel = new Label(langManager.getMessage("admin.voted.users") + ": " + votedUsers);
        votedUsersLabel.setFont(Font.font("System", 14));
        
        Label nonVotedUsersLabel = new Label(langManager.getMessage("admin.non.voted.users") + ": " + nonVotedUsers);
        nonVotedUsersLabel.setFont(Font.font("System", 14));
        
        Label turnoutLabel = new Label(String.format(langManager.getMessage("admin.turnout.percentage") + ": %.1f%%", turnoutPercentage));
        turnoutLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        turnoutLabel.setTextFill(Color.web(PRIMARY_COLOR));
        
        statsBox.getChildren().addAll(titleLabel, totalUsersLabel, votedUsersLabel, nonVotedUsersLabel, turnoutLabel);
        HBox.setHgrow(statsBox, Priority.ALWAYS);
        
        // Create a simple progress bar representation of turnout
        VBox progressBox = new VBox(10);
        progressBox.setAlignment(Pos.CENTER);
        
        ProgressBar progressBar = new ProgressBar(turnoutPercentage / 100);
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(30);
        progressBar.setStyle("-fx-accent: " + PRIMARY_COLOR + ";");
        
        Label progressLabel = new Label(String.format("%.1f%%", turnoutPercentage));
        progressLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        progressLabel.setTextFill(Color.web(PRIMARY_COLOR));
        
        progressBox.getChildren().addAll(progressBar, progressLabel);
        
        turnoutBox.getChildren().addAll(statsBox, progressBox);
        return turnoutBox;
    }
    
    /**
     * Create a section header
     */
    private Label createSectionHeader(String title) {
        Label header = new Label(title);
        header.setFont(Font.font("System", FontWeight.BOLD, 18));
        header.setTextFill(Color.web(TEXT_COLOR));
        header.setPadding(new Insets(10, 0, 10, 0));
        return header;
    }
    
    /**
     * Create candidate management content
     */
    private VBox createCandidateManagementContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        // Create the candidates table
        TableView<CandidateItem> candidatesTable = new TableView<>();
        
        TableColumn<CandidateItem, String> nameColumn = new TableColumn<>(langManager.getMessage("admin.candidate.name"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);
        
        TableColumn<CandidateItem, String> constituencyColumn = new TableColumn<>(langManager.getMessage("admin.candidate.constituency"));
        constituencyColumn.setCellValueFactory(new PropertyValueFactory<>("constituency"));
        constituencyColumn.setPrefWidth(200);
        
        TableColumn<CandidateItem, Integer> votesColumn = new TableColumn<>(langManager.getMessage("admin.candidate.votes"));
        votesColumn.setCellValueFactory(new PropertyValueFactory<>("votes"));
        votesColumn.setPrefWidth(100);
        
        // Create action column with delete button
        TableColumn<CandidateItem, Void> actionColumn = new TableColumn<>(langManager.getMessage("admin.actions"));
        actionColumn.setPrefWidth(100);
        actionColumn.setCellFactory(col -> {
            TableCell<CandidateItem, Void> cell = new TableCell<>() {
                private final Button deleteButton = new Button(langManager.getMessage("admin.delete"));
                
                {
                    deleteButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> {
                        CandidateItem candidate = getTableView().getItems().get(getIndex());
                        handleDeleteCandidate(candidate);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
            return cell;
        });
        
        candidatesTable.getColumns().addAll(nameColumn, constituencyColumn, votesColumn, actionColumn);
        
        // Add candidates to the table
        refreshCandidatesTable(candidatesTable);
        
        // Create form for adding new candidates
        GridPane addCandidateForm = new GridPane();
        addCandidateForm.setHgap(10);
        addCandidateForm.setVgap(10);
        addCandidateForm.setPadding(new Insets(20));
        addCandidateForm.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        Label formTitle = new Label(langManager.getMessage("admin.add.candidate"));
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Label nameLabel = new Label(langManager.getMessage("admin.candidate.name"));
        TextField nameField = new TextField();
        nameField.setPromptText(langManager.getMessage("admin.enter.candidate.name"));
        
        Label constituencyLabel = new Label(langManager.getMessage("admin.candidate.constituency"));
        
        // Create constituency dropdown
        ComboBox<String> constituencyComboBox = new ComboBox<>();
        List<String> constituencies = model.getConstituencies();
        constituencyComboBox.getItems().addAll(constituencies);
        if (!constituencies.isEmpty()) {
            constituencyComboBox.setValue(constituencies.get(0));
        }
        
        Button addButton = new Button(langManager.getMessage("admin.add"));
        addButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String constituency = constituencyComboBox.getValue();
            
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                    langManager.getMessage("admin.name.required"));
                return;
            }
            
            if (constituency == null || constituency.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                    langManager.getMessage("admin.constituency.required"));
                return;
            }
            
            if (model.registerCandidate(name, constituency)) {
                nameField.clear();
                refreshCandidatesTable(candidatesTable);
                showAlert(Alert.AlertType.INFORMATION, langManager.getMessage("admin.success"), 
                    langManager.getMessage("admin.candidate.added"));
            } else {
                showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                    langManager.getMessage("admin.candidate.exists"));
            }
        });
        
        addCandidateForm.add(formTitle, 0, 0, 2, 1);
        addCandidateForm.add(nameLabel, 0, 1);
        addCandidateForm.add(nameField, 1, 1);
        addCandidateForm.add(constituencyLabel, 0, 2);
        addCandidateForm.add(constituencyComboBox, 1, 2);
        addCandidateForm.add(addButton, 1, 3);
        
        content.getChildren().addAll(
            createSectionHeader(langManager.getMessage("admin.manage.candidates")),
            candidatesTable,
            createSectionHeader(langManager.getMessage("admin.add.new.candidate")),
            addCandidateForm
        );
        
        return content;
    }
    
    /**
     * Refresh the candidates table
     */
    private void refreshCandidatesTable(TableView<CandidateItem> table) {
        table.getItems().clear();
        List<CandidateItem> candidates = model.getAllCandidates();
        table.getItems().addAll(candidates);
    }
    
    /**
     * Handle deleting a candidate
     */
    private void handleDeleteCandidate(CandidateItem candidate) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(langManager.getMessage("admin.confirm.delete"));
        alert.setHeaderText(null);
        alert.setContentText(langManager.getMessage("admin.confirm.delete.message") + candidate.getName() + "?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (model.deleteCandidate(candidate.getName(), candidate.getConstituency())) {
                // Refresh the candidates table
                TabPane tabPane = (TabPane) mainLayout.getCenter().lookup(".tab-pane");
                if (tabPane != null) {
                    Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
                    if (selectedTab != null && selectedTab.getText().equals(langManager.getMessage("admin.candidates"))) {
                        // Get the ScrollPane that contains the content
                        ScrollPane scrollPane = (ScrollPane) selectedTab.getContent();
                        if (scrollPane != null) {
                            // Get the VBox inside the ScrollPane
                            VBox content = (VBox) scrollPane.getContent();
                            if (content != null) {
                                // Find the TableView in the VBox children
                                for (javafx.scene.Node node : content.getChildren()) {
                                    if (node instanceof TableView) {
                                        @SuppressWarnings("unchecked")
                                        TableView<CandidateItem> candidatesTable = (TableView<CandidateItem>) node;
                                        refreshCandidatesTable(candidatesTable);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                showAlert(Alert.AlertType.INFORMATION, langManager.getMessage("admin.success"), 
                    langManager.getMessage("admin.candidate.deleted"));
            } else {
                showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                    langManager.getMessage("admin.candidate.delete.error"));
            }
        }
    }
    
    /**
     * Create system management content
     */
    private VBox createSystemManagementContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        // Reset votes
        VBox resetVotesBox = createActionBox(
            langManager.getMessage("admin.reset.votes"),
            langManager.getMessage("admin.reset.votes.desc"),
            langManager.getMessage("admin.reset"),
            e -> handleResetVotes()
        );
        
        // Reset users has_voted flag
        VBox resetUserVotesBox = createActionBox(
            langManager.getMessage("admin.reset.user.votes"),
            langManager.getMessage("admin.reset.user.votes.desc"),
            langManager.getMessage("admin.reset"),
            e -> handleResetUserVotes()
        );
        
        // Delete all candidates
        VBox deleteAllCandidatesBox = createActionBox(
            langManager.getMessage("admin.delete.all.candidates"),
            langManager.getMessage("admin.delete.all.candidates.desc"),
            langManager.getMessage("admin.delete.all"),
            e -> handleDeleteAllCandidates()
        );
        
        // Full system reset
        VBox fullResetBox = createActionBox(
            langManager.getMessage("admin.full.reset"),
            langManager.getMessage("admin.full.reset.desc"),
            langManager.getMessage("admin.full.reset"),
            e -> handleFullReset()
        );
        
        // Danger zone styling
        VBox dangerZone = new VBox(20);
        dangerZone.setPadding(new Insets(20));
        dangerZone.setStyle("-fx-background-color: #ffebee; -fx-background-radius: 10; -fx-border-color: " + SECONDARY_COLOR + "; -fx-border-radius: 10; -fx-border-width: 1;");
        
        Label dangerTitle = new Label(langManager.getMessage("admin.danger.zone"));
        dangerTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        dangerTitle.setTextFill(Color.web(SECONDARY_COLOR));
        
        dangerZone.getChildren().addAll(dangerTitle, deleteAllCandidatesBox, fullResetBox);
        
        content.getChildren().addAll(
            createSectionHeader(langManager.getMessage("admin.system.management")),
            resetVotesBox,
            resetUserVotesBox,
            createSectionHeader(langManager.getMessage("admin.danger.zone")),
            dangerZone
        );
        
        return content;
    }
    
    /**
     * Create an action box for system management
     */
    private VBox createActionBox(String title, String description, String buttonText, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(TEXT_COLOR));
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", 14));
        descLabel.setWrapText(true);
        
        Button actionButton = new Button(buttonText);
        
        // Set button style based on action
        if (buttonText.contains(langManager.getMessage("admin.delete"))) {
            actionButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white;");
        } else {
            actionButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;");
        }
        
        actionButton.setOnAction(handler);
        
        box.getChildren().addAll(titleLabel, descLabel, actionButton);
        return box;
    }
    
    /**
     * Handle resetting votes
     */
    private void handleResetVotes() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(langManager.getMessage("admin.confirm.reset"));
        alert.setHeaderText(null);
        alert.setContentText(langManager.getMessage("admin.confirm.reset.votes"));
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (model.resetAllVotes()) {
                showAlert(Alert.AlertType.INFORMATION, langManager.getMessage("admin.success"), 
                    langManager.getMessage("admin.votes.reset"));
            } else {
                showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                    langManager.getMessage("admin.votes.reset.error"));
            }
        }
    }
    
    /**
     * Handle resetting user votes
     */
    private void handleResetUserVotes() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(langManager.getMessage("admin.confirm.reset"));
        alert.setHeaderText(null);
        alert.setContentText(langManager.getMessage("admin.confirm.reset.user.votes"));
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (model.resetUserVoteStatus()) {
                showAlert(Alert.AlertType.INFORMATION, langManager.getMessage("admin.success"), 
                    langManager.getMessage("admin.user.votes.reset"));
            } else {
                showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                    langManager.getMessage("admin.user.votes.reset.error"));
            }
        }
    }
    
    /**
     * Handle deleting all candidates
     */
    private void handleDeleteAllCandidates() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(langManager.getMessage("admin.confirm.delete"));
        alert.setHeaderText(null);
        alert.setContentText(langManager.getMessage("admin.confirm.delete.all.candidates"));
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            // Double-check with password
            String adminPassword = showPasswordDialog(langManager.getMessage("admin.enter.password"));
            if (adminPassword != null && model.verifyAdminPassword(adminPassword)) {
                if (model.deleteAllCandidates()) {
                    showAlert(Alert.AlertType.INFORMATION, langManager.getMessage("admin.success"), 
                        langManager.getMessage("admin.all.candidates.deleted"));
                } else {
                    showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                        langManager.getMessage("admin.delete.candidates.error"));
                }
            } else {
                showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                    langManager.getMessage("admin.invalid.password"));
            }
        }
    }
    
    /**
     * Handle full system reset
     */
    private void handleFullReset() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(langManager.getMessage("admin.confirm.reset"));
        alert.setHeaderText(langManager.getMessage("admin.warning"));
        alert.setContentText(langManager.getMessage("admin.confirm.full.reset"));
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            // Double-check with password
            String adminPassword = showPasswordDialog(langManager.getMessage("admin.enter.password"));
            if (adminPassword != null && model.verifyAdminPassword(adminPassword)) {
                if (model.fullSystemReset()) {
                    showAlert(Alert.AlertType.INFORMATION, langManager.getMessage("admin.success"), 
                        langManager.getMessage("admin.system.reset"));
                } else {
                    showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                        langManager.getMessage("admin.system.reset.error"));
                }
            } else {
                showAlert(Alert.AlertType.ERROR, langManager.getMessage("admin.error"), 
                    langManager.getMessage("admin.invalid.password"));
            }
        }
    }
    
    /**
     * Show a password dialog
     */
    private String showPasswordDialog(String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(langManager.getMessage("admin.password.required"));
        dialog.setHeaderText(message);
        
        ButtonType confirmButtonType = new ButtonType(langManager.getMessage("admin.confirm"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(langManager.getMessage("admin.password"));
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label(langManager.getMessage("admin.password") + ":"), 0, 0);
        grid.add(passwordField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return passwordField.getText();
            }
            return null;
        });
        
        return dialog.showAndWait().orElse(null);
    }
    
    /**
     * Show an alert
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Candidate item for the table view
     */
    public static class CandidateItem {
        private String name;
        private String constituency;
        private int votes;
        
        public CandidateItem(String name, String constituency, int votes) {
            this.name = name;
            this.constituency = constituency;
            this.votes = votes;
        }
        
        public String getName() {
            return name;
        }
        
        public String getConstituency() {
            return constituency;
        }
        
        public int getVotes() {
            return votes;
        }
    }
    
    /**
     * Candidate vote data for charts
     */
    public static class CandidateVote {
        private String candidateName;
        private int votes;
        
        public CandidateVote(String candidateName, int votes) {
            this.candidateName = candidateName;
            this.votes = votes;
        }
        
        public String getCandidateName() {
            return candidateName;
        }
        
        public int getVotes() {
            return votes;
        }
    }
} 