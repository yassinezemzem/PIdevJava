package org.example.controller;

import org.example.entities.User;
import org.example.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegisterController {
    @FXML
    private TextField fullNameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private RadioButton userRoleButton;
    
    @FXML
    private RadioButton adminRoleButton;
    
    @FXML
    private Hyperlink loginLink;
    
    private UserService userService;
    
    public RegisterController() {
        this.userService = new UserService();
    }
    
    @FXML
    private void handleRegister(ActionEvent event) {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validate input
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match");
            return;
        }
        
        // Get selected role with ROLE_ prefix
        String role = userRoleButton.isSelected() ? "ROLE_USER" : "ROLE_ADMIN";
        
        try {
            // Create user with proper role format
            User user = userService.createUser(
                fullName,
                email,
                phone,
                Collections.singletonList(role),
                password
            );
            
            // Show success message and redirect to login
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Please login.");
            handleLogin(event);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Better Health");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login form: " + e.getMessage());
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