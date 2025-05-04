package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import Entities.User;

import java.io.IOException;

public class BloodDonationController {
    @FXML
    private Button requestButton;
    
    @FXML
    private Button centersButton;
    
    @FXML
    private Button nearbyButton;
    
    @FXML
    private Button notificationsButton;
    
    @FXML
    private Button statisticsButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    private ScrollPane mainServicesPane;
    
    private User currentUser;
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    @FXML
    public void initialize() {
        if (contentArea != null && mainServicesPane != null) {
            contentArea.getChildren().setAll(mainServicesPane);
        }
    }
    
    @FXML
    void onRequest(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DemandeDonSangList.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            showError("Failed to load blood donation requests", e.getMessage());
        }
    }
    
    @FXML
    void onCenters(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CentreDeDonListView.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            showError("Failed to load donation centers", e.getMessage());
        }
    }
    
    @FXML
    void onNearby(ActionEvent event) {
        try {
            MapView mapView = new MapView();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(mapView);
        } catch (Exception e) {
            showError("Failed to load map view", e.getMessage());
        }
    }
    
    @FXML
    void onNotifications(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NotificationView.fxml"));
            Parent view = loader.load();
            
            // Get the controller and set the current user
            NotificationController notificationController = loader.getController();
            notificationController.setCurrentUser(currentUser); // Make sure currentUser is a class field
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            showError("Failed to load notifications", e.getMessage());
        }
    }
    
    @FXML
    void onStatistics(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DemandeDonSangStatistics.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            showError("Failed to load statistics", e.getMessage());
        }
    }
    
    @FXML
    void onBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - User Dashboard");
        } catch (IOException e) {
            showError("Failed to load User Dashboard", e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 