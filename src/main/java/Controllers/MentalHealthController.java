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

public class MentalHealthController {
    @FXML
    private Button backButton;
    @FXML
    private Button checkOurServicesButton;

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
    void onCheckOurServices(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) checkOurServicesButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Front Page");
        } catch (IOException e) {
            showAlert("Failed to load Front Page", e.getMessage());
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