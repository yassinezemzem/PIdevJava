package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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
    private StackPane contentArea;
    @FXML
    private VBox mainContent;

    @FXML
    void onBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
            mainContent.setVisible(false);
            contentArea.setVisible(true);
        } catch (IOException e) {
            showAlert("Error", "Failed to load User Dashboard: " + e.getMessage());
        }
    }

    @FXML
    void onViewMap(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_appointment.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            mainContent.setVisible(false);
            contentArea.setVisible(true);
        } catch (IOException e) {
            showAlert("Error", "Failed to load appointment form: " + e.getMessage());
        }
    }

    @FXML
    void onMakeAppointment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_appointment.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            mainContent.setVisible(false);
            contentArea.setVisible(true);
        } catch (IOException e) {
            showAlert("Error", "Failed to load appointment form: " + e.getMessage());
        }
    }

    @FXML
    void onViewAppointments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/appointment_table.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            mainContent.setVisible(false);
            contentArea.setVisible(true);
        } catch (IOException e) {
            showAlert("Error", "Failed to load appointments list: " + e.getMessage());
        }
    }

    @FXML
    void onServiceProviderTable(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceProviderTable.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            mainContent.setVisible(false);
            contentArea.setVisible(true);
        } catch (IOException e) {
            showAlert("Error", "Failed to load service provider table: " + e.getMessage());
        }
    }

    @FXML
    void onAddServiceProvider(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service_Providers_form.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            mainContent.setVisible(false);
            contentArea.setVisible(true);
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