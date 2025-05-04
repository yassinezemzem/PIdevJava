package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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
            showAlert("Error", "Failed to load User Dashboard: " + e.getMessage());
        }
    }

    @FXML
    void onViewMap(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_appointment.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) viewMapButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Book Appointment");
        } catch (IOException e) {
            showAlert("Error", "Failed to load appointment form: " + e.getMessage());
        }
    }

    @FXML
    void onMakeAppointment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_appointment.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) makeAppointmentButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Book Appointment");
        } catch (IOException e) {
            showAlert("Error", "Failed to load appointment form: " + e.getMessage());
        }
    }

    @FXML
    void onViewAppointments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/appointment_table.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) viewAppointmentsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - View Appointments");
        } catch (IOException e) {
            showAlert("Error", "Failed to load appointments list: " + e.getMessage());
        }
    }

    @FXML
    void onServiceProviderTable(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceProviderTable.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) makeAppointmentButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Service Providers");
        } catch (IOException e) {
            showAlert("Error", "Failed to load service provider table: " + e.getMessage());
        }
    }

    @FXML
    void onAddServiceProvider(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service_Providers_form.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) makeAppointmentButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Add Service Provider");
        } catch (IOException e) {
            showAlert("Error", "Failed to load add service provider form: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 