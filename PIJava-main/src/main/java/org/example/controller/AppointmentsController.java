package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.entities.User;
import java.io.IOException;

public class AppointmentsController {
    @FXML
    private Button backButton;
    @FXML
    private Button viewMapButton;
    @FXML
    private Button makeAppointmentButton;
    @FXML
    private Button viewAppointmentsButton;

    @FXML
    void onBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - User Dashboard");
        } catch (IOException e) {
            showAlert("Failed to load User Dashboard", e.getMessage());
        }
    }

    @FXML
    void onViewMap(ActionEvent event) {
        showAlert("View Map", "Map feature coming soon!");
    }

    @FXML
    void onMakeAppointment(ActionEvent event) {
        showAlert("Make Appointment", "Appointment booking coming soon!");
    }

    @FXML
    void onViewAppointments(ActionEvent event) {
        showAlert("View Appointments", "Appointments list coming soon!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 