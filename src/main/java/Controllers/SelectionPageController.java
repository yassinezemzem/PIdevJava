package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;

public class SelectionPageController {

    @FXML
    private Button btnAdmin;

    @FXML
    private Button btnClient;

    @FXML
    void navigateToAdmin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/adminDashboard.fxml"));
            btnAdmin.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement du dashboard administrateur.");
        }
    }

    @FXML
    void navigateToClient(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/clientDashboard.fxml"));
            btnClient.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement du dashboard client.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 