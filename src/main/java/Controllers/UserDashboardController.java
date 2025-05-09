package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    @FXML
    private BorderPane rootPane;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button bloodDonationButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button appointmentsButton;
    @FXML
    private Button mentalHealthButton;
    @FXML
    private Button eventsButton;
    @FXML
    private Button medicalProductsButton;
    @FXML
    private Button onlineStoreButton;

    @FXML
    private Button btnProfile;
    @FXML
    private Button btnLogout;

    private String currentView = "dashboard";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            loadLoginScreen();
            return;
        }

        // Set welcome message
        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getFullName());

        // Update window title
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setTitle("Better Health - User Dashboard");
        } catch (Exception e) {
            // This might not be available yet during initialization, which is OK
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        SessionManager.logout();
        loadLoginScreen();
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onBloodDonation(ActionEvent event) {
        if (!"blooddonation".equals(currentView)) {
            try {
                // Clear the content area
                contentArea.getChildren().clear();

                // Load the Blood Donation view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BloodDonationPage.fxml"));
                Parent view = loader.load();

                // Get the controller and set the current user
                BloodDonationController bloodDonationController = loader.getController();
                bloodDonationController.setCurrentUser(SessionManager.getCurrentUser());

                // Add the view to our content area
                contentArea.getChildren().add(view);

                // Update the current view tracker
                currentView = "blooddonation";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onAppointments(ActionEvent event) {
        if (!"appointments".equals(currentView)) {
            try {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AppointmentsPage.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "appointments";
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Appointments page: " + e.getMessage());
            }
        }
    }

    @FXML
    void onMentalHealth(ActionEvent event) {
        if (!"mentalhealth".equals(currentView)) {
            try {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MentalHealthPage.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "mentalhealth";
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Mental Health page: " + e.getMessage());
            }
        }
    }

    @FXML
    void onEvents(ActionEvent event) {
        if (!"events".equals(currentView)) {
            try {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventsPage.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "events";
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Events page: " + e.getMessage());
            }
        }
    }

    @FXML
    void onMedicalProducts(ActionEvent event) {
        if (!"medicalproducts".equals(currentView)) {
            try {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MedicalProductsPage.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "medicalproducts";
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Medical Products page: " + e.getMessage());
            }
        }
    }

    @FXML
    void onOnlineStore(ActionEvent event) {
        if (!"onlinestore".equals(currentView)) {
            try {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "onlinestore";
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Online Store page: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void onProfile() {
        try {
            loadContent("/fxml/Profile.fxml");
        } catch (IOException e) {
            showError("Failed to load profile", e.getMessage());
        }
    }

    @FXML
    protected void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Login");
        } catch (IOException e) {
            showError("Failed to logout", e.getMessage());
        }
    }

    protected void loadContent(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Node content = loader.load();
        contentArea.getChildren().setAll(content);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}