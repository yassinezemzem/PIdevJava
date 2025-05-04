package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class FrontPageController {

    @FXML
    private StackPane contentArea;

    @FXML
    void initialize() {
        // No initialization needed
    }

    @FXML
    void gotoAccueil(ActionEvent event) {
        loadUI("afficher-therapie-user.fxml");
    }

    @FXML
    void gotoCentreDeDon(ActionEvent event) {
        loadUI("CentreDeDonListView.fxml");
    }

    @FXML
    void gotoTherapie(ActionEvent event) {
        loadUI("afficher-therapie-user.fxml");
    }

    @FXML
    void gotoExerciceMental(ActionEvent event) {
        loadUI("afficher-exercice-user.fxml");
    }

    @FXML
    void onBloodDonation(ActionEvent event) {
        loadUI("fxml/BloodDonationPage.fxml");
    }

    @FXML
    void onAppointments(ActionEvent event) {
        loadUI("fxml/AppointmentsPage.fxml");
    }

    @FXML
    void onMentalHealth(ActionEvent event) {
        loadUI("fxml/MentalHealthPage.fxml");
    }

    @FXML
    void onEvents(ActionEvent event) {
        loadUI("fxml/EventsPage.fxml");
    }

    @FXML
    void onMedicalProducts(ActionEvent event) {
        loadUI("fxml/MedicalProductsPage.fxml");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        // TODO: Implement logout logic
    }

    private void loadUI(String uiPath) {
        try {
            // Remove leading slash if present
            String path = uiPath.startsWith("/") ? uiPath.substring(1) : uiPath;
            Parent root = FXMLLoader.load(getClass().getResource("/" + path));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            System.err.println("Error loading UI " + uiPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
} 