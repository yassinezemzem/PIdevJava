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

public class MedicalProductsController {
    @FXML
    private Button backButton;
    @FXML
    private Button donateNowButton;
    @FXML
    private Button viewProductsButton;

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
    void onDonateNow(ActionEvent event) {
        showAlert("Donate Now", "Medical product donation coming soon!");
    }

    @FXML
    void onViewProducts(ActionEvent event) {
        showAlert("View Medical Products", "Medical products list coming soon!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 