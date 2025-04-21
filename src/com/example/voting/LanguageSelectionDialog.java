package com.example.voting;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Locale;

/**
 * Dialog for language selection when the application starts
 */
public class LanguageSelectionDialog {
    private Stage stage;
    private Locale selectedLocale;
    
    /**
     * Create a language selection dialog
     * @param ownerStage the owner stage
     * @return the selected locale
     */
    public Locale showAndWait(Stage ownerStage) {
        LanguageManager langManager = LanguageManager.getInstance();
        selectedLocale = langManager.getCurrentLocale();
        
        stage = new Stage();
        stage.initOwner(ownerStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Select Language / भाषा चुनें / ಭಾಷೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ");
        stage.setResizable(false);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Select Language / भाषा चुनें / ಭಾಷೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(15));
        optionsBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        ToggleGroup toggleGroup = new ToggleGroup();
        
        // Create radio button for each language
        Locale[] availableLocales = langManager.getAvailableLocales();
        for (Locale locale : availableLocales) {
            RadioButton rb = new RadioButton(langManager.getLanguageName(locale));
            rb.setUserData(locale);
            rb.setToggleGroup(toggleGroup);
            
            // Set the default selection
            if (locale.equals(langManager.getCurrentLocale())) {
                rb.setSelected(true);
            }
            
            optionsBox.getChildren().add(rb);
        }
        
        Button continueButton = new Button("Continue / जारी रखें / ಮುಂದುವರಿಸಿ");
        continueButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        continueButton.setPrefWidth(200);
        
        continueButton.setOnAction(e -> {
            if (toggleGroup.getSelectedToggle() != null) {
                selectedLocale = (Locale) toggleGroup.getSelectedToggle().getUserData();
                langManager.setLocale(selectedLocale);
            }
            stage.close();
        });
        
        content.getChildren().addAll(titleLabel, optionsBox, continueButton);
        
        Scene scene = new Scene(content, 350, 300);
        stage.setScene(scene);
        
        // Block until user makes a selection
        stage.showAndWait();
        
        return selectedLocale;
    }
} 