package Controllers;

import Entities.CentreDeDon;
import Entities.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CentreDeDonListController {

    @FXML
    private TextField searchField;
    
    @FXML
    private FlowPane centresFlowPane;
    
    @FXML
    private Button addButton;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button deleteButton;
    
    private CentreDeDonController controller;
    private ObservableList<CentreDeDon> centreList;
    private CentreDeDon selectedCentre;
    
    @FXML
    private void initialize() {
        // Initialize controller
        controller = new CentreDeDonController();
        
        // Set up role-based access control
        setupAccessControl();
        
        // Load data
        refreshCentreList();
    }
    
    private void setupAccessControl() {
        User currentUser = SessionManager.getCurrentUser();
        boolean isAdmin = currentUser != null && currentUser.hasRole("ADMIN");
        
        // Show/hide admin controls based on role
        addButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            refreshCentreList();
        } else {
            try {
                List<CentreDeDon> results = controller.searchCentresByName(searchTerm);
                centreList = FXCollections.observableArrayList(results);
                updateCentreCards();
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Search Error", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Search Error", "An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        refreshCentreList();
    }
    
    @FXML
    private void handleAddCentre() {
        if (!SessionManager.getCurrentUser().hasRole("ADMIN")) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", "Only administrators can add new centres.");
            return;
        }
        
        try {
            // Load the add centre FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddCentreDeDon.fxml"));
            Parent root = loader.load();
            
            // Set up the stage
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Centre de Don");
            stage.setScene(new Scene(root));
            
            // Show the stage and wait for it to close
            stage.showAndWait();
            
            // Refresh the list after adding
            refreshCentreList();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the add centre form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditCentre() {
        if (!SessionManager.getCurrentUser().hasRole("ADMIN")) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", "Only administrators can edit centres.");
            return;
        }
        
        if (selectedCentre == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                    "Please select a centre to edit.");
            return;
        }
        
        try {
            // Load the edit centre FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditCentreDeDon.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the centre to edit
            EditCentreDeDonController editController = loader.getController();
            editController.setCentreDeDon(selectedCentre);
            
            // Set up the stage
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Centre de Don");
            stage.setScene(new Scene(root));
            
            // Show the stage and wait for it to close
            stage.showAndWait();
            
            // Refresh the list after editing
            refreshCentreList();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the edit centre form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDeleteCentre() {
        if (!SessionManager.getCurrentUser().hasRole("ADMIN")) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", "Only administrators can delete centres.");
            return;
        }
        
        if (selectedCentre == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                    "Please select a centre to delete.");
            return;
        }
        
        // Confirm deletion
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Centre de Don");
        confirmDialog.setContentText("Are you sure you want to delete the centre: " + 
                selectedCentre.getName() + "?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = controller.deleteCentre(selectedCentre.getId());
                if (deleted) {
                    refreshCentreList();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Centre deleted successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not delete the centre.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void refreshCentreList() {
        try {
            List<CentreDeDon> centres = controller.getAllCentres();
            centreList = FXCollections.observableArrayList(centres);
            updateCentreCards();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load centres: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateCentreCards() {
        centresFlowPane.getChildren().clear();
        selectedCentre = null;
        
        if (centreList.isEmpty()) {
            Label emptyLabel = new Label("No centres available");
            emptyLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
            emptyLabel.setPadding(new Insets(20));
            centresFlowPane.getChildren().add(emptyLabel);
            return;
        }
        
        for (CentreDeDon centre : centreList) {
            VBox card = createCentreCard(centre);
            centresFlowPane.getChildren().add(card);
        }
    }
    
    private VBox createCentreCard(CentreDeDon centre) {
        // Create card container
        VBox card = new VBox(10);
        card.setPrefWidth(270);
        card.setPrefHeight(220);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Centre name heading
        Label nameLabel = new Label(centre.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(240);
        
        // Centre ID
        Label idLabel = new Label("ID: " + centre.getId());
        idLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        idLabel.setTextFill(Color.GRAY);
        
        // Address
        Label addressLabel = new Label("Address: " + centre.getAddress());
        addressLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        addressLabel.setWrapText(true);
        
        // Contact info
        Label phoneLabel = new Label("Phone: " + centre.getTelephone());
        phoneLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        
        // Email
        Label emailLabel = new Label("Email: " + (centre.getEmail() != null ? centre.getEmail() : "N/A"));
        emailLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        emailLabel.setWrapText(true);
        
        // Created date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String createdDate = centre.getCreatedAt() != null ? dateFormat.format(centre.getCreatedAt()) : "N/A";
        Label dateLabel = new Label("Created: " + createdDate);
        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        dateLabel.setTextFill(Color.GRAY);
        
        // Location info
        Label locationLabel = new Label(String.format("Location: %.4f, %.4f", 
                centre.getLatitude(), centre.getLongitude()));
        locationLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
        locationLabel.setTextFill(Color.GRAY);
        
        // Add all elements to card
        card.getChildren().addAll(
                nameLabel, idLabel, addressLabel, 
                phoneLabel, emailLabel, dateLabel, locationLabel
        );
        
        // Add selection behavior
        card.setOnMouseClicked(event -> {
            // Deselect any previously selected card
            for (Node node : centresFlowPane.getChildren()) {
                if (node instanceof VBox) {
                    node.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                }
            }
            
            // Select the clicked card
            card.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #007bff; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            selectedCentre = centre;
        });
        
        return card;
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 