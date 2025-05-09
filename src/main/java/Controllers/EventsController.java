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

public class EventsController {
    @FXML
    private Button backButton;
    @FXML
    private Button checkEventsButton;

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
    void onCheckEvents(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) checkEventsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Events");
        } catch (IOException e) {
            showAlert("Error", "Failed to load Events page: " + e.getMessage());
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