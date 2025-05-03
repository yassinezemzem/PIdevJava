package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DatabaseInitializer;
import Services.DonationEligibilityChecker;

import java.io.IOException;

public class TestFX extends Application {
    private DonationEligibilityChecker eligibilityChecker;

    public static void main(String[] args) {
        // Initialize the MySQL driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return;
        }
        
        // Initialize database and sample data
        System.out.println("Initializing database...");
        DatabaseInitializer.initializeDatabase();
        DatabaseInitializer.addSampleData();
        
        // Launch JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize and start the donation eligibility checker
            eligibilityChecker = new DonationEligibilityChecker();
            eligibilityChecker.startChecking();
            
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Better Health - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(500);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Stop the eligibility checker when the application closes
        if (eligibilityChecker != null) {
            eligibilityChecker.stopChecking();
        }
    }
}
