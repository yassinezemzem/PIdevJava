package org.example.controller;
import org.example.entities.User;
import org.example.service.UserService;
import org.example.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Hyperlink registerLink;
    
    private UserService userService;
    
    public LoginController() {
        this.userService = new UserService();
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both email and password");
            return;
        }
        
        try {
            User user = userService.authenticateUser(email, password);
            if (user != null) {
                // Store the current user in a session manager or static variable
                SessionManager.setCurrentUser(user);
                
                // Load the dashboard
                loadDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid email or password");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register - Better Health");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load registration form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadDashboard() {
        try {
            String dashboardFxml = SessionManager.isAdmin() ? "/fxml/Dashboard.fxml" : "/fxml/UserDashboard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardFxml));
            Parent root = loader.load();
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Dashboard");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 