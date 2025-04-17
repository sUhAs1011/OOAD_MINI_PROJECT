package com.example.voting;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the Model
            Model model = new Model();
            
            // Initialize the JavaFX View
            JavaFXView view = new JavaFXView(primaryStage);
            
            // Set the model reference in the view
            view.setModel(model);
            
            // Initialize the Controller and start the application
            new Controller(model, view);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}