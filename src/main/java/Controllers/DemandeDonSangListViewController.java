package Controllers;

import Entities.CentreDeDon;
import Entities.DemandeDonSang;
import Services.DemandeDonSangService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DemandeDonSangListViewController {

    @FXML
    private ComboBox<String> statusComboBox;
    
    @FXML
    private FlowPane demandesFlowPane;
    
    private DemandeDonSangService service;
    private ObservableList<DemandeDonSang> demandeList;
    private DemandeDonSang selectedDemande;
    
    @FXML
    private void initialize() {
        // Initialize service
        service = new DemandeDonSangService();
        
        // Initialize status combo box
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "All", "pending", "accepted", "refused"
        );
        statusComboBox.setItems(statusOptions);
        statusComboBox.setValue("All");
        
        // Load data
        refreshDemandeList();
    }
    
    @FXML
    private void handleFilter() {
        String selectedStatus = statusComboBox.getValue();
        if (selectedStatus == null || selectedStatus.equals("All")) {
            refreshDemandeList();
        } else {
            try {
                List<DemandeDonSang> filtered = service.getDemandesByStatus(selectedStatus);
                demandeList = FXCollections.observableArrayList(filtered);
                updateDemandeCards();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Filter Error", "An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleRefresh() {
        statusComboBox.setValue("All");
        refreshDemandeList();
    }
    
    @FXML
    private void handleAddDemande() {
        try {
            // Load the add demande FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddDemandeDonSang.fxml"));
            Parent root = loader.load();
            
            // Set up the stage
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Blood Donation Request");
            stage.setScene(new Scene(root));
            
            // Show the stage and wait for it to close
            stage.showAndWait();
            
            // Refresh the list after adding
            refreshDemandeList();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the add request form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleUpdateStatus() {
        if (selectedDemande == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                    "Please select a request to update.");
            return;
        }
        
        // Create a dialog for status selection
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Status");
        dialog.setHeaderText("Update Status for Request #" + selectedDemande.getId());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the status combo box
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("pending", "accepted", "refused");
        statusBox.setValue(selectedDemande.getStatus());
        
        // Layout the dialog
        dialog.getDialogPane().setContent(statusBox);
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return statusBox.getValue();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(status -> {
            try {
                DemandeDonSang updated = service.updateDemandeStatus(selectedDemande.getId(), status);
                if (updated != null) {
                    refreshDemandeList();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Status updated successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not update the status.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    @FXML
    private void handleDeleteDemande() {
        if (selectedDemande == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                    "Please select a request to delete.");
            return;
        }
        
        // Confirm deletion
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Blood Donation Request");
        confirmDialog.setContentText("Are you sure you want to delete the request #" + 
                selectedDemande.getId() + "?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = service.deleteDemande(selectedDemande.getId());
                if (deleted) {
                    refreshDemandeList();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Request deleted successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not delete the request.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void refreshDemandeList() {
        try {
            List<DemandeDonSang> demandes = service.getAllDemandes();
            demandeList = FXCollections.observableArrayList(demandes);
            updateDemandeCards();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load requests: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
    private void updateDemandeCards() {
        demandesFlowPane.getChildren().clear();
        selectedDemande = null;
        
        if (demandeList.isEmpty()) {
            Label emptyLabel = new Label("No blood donation requests available");
            emptyLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
            emptyLabel.setPadding(new Insets(20));
            demandesFlowPane.getChildren().add(emptyLabel);
            return;
        }
        
        for (DemandeDonSang demande : demandeList) {
            VBox card = createDemandeCard(demande);
            demandesFlowPane.getChildren().add(card);
        }
    }
    
    private VBox createDemandeCard(DemandeDonSang demande) {
        // Create card container
        VBox card = new VBox(10);
        card.setPrefWidth(270);
        card.setPrefHeight(200);
        card.setPadding(new Insets(15));
        
        // Set different background colors based on status
        String statusColor;
        switch (demande.getStatus()) {
            case "accepted":
                statusColor = "#e8f5e9"; // Light green
                break;
            case "refused":
                statusColor = "#ffebee"; // Light red
                break;
            default: // pending
                statusColor = "#fff8e1"; // Light yellow
                break;
        }
        
        card.setStyle("-fx-background-color: " + statusColor + "; -fx-border-color: #dddddd; " +
                "-fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Request ID heading
        Label idLabel = new Label("Request #" + demande.getId());
        idLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // Status
        HBox statusBox = new HBox(5);
        Label statusLabel = new Label("Status: ");
        statusLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        
        Label statusValueLabel = new Label(demande.getStatus());
        statusValueLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        // Set status text color
        switch (demande.getStatus()) {
            case "accepted":
                statusValueLabel.setTextFill(Color.GREEN);
                break;
            case "refused":
                statusValueLabel.setTextFill(Color.RED);
                break;
            default: // pending
                statusValueLabel.setTextFill(Color.ORANGE);
                break;
        }
        
        statusBox.getChildren().addAll(statusLabel, statusValueLabel);
        
        // Centre de Don
        String centreName = demande.getCentreDeDon() != null ? demande.getCentreDeDon().getName() : "N/A";
        Label centreLabel = new Label("Centre: " + centreName);
        centreLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        centreLabel.setWrapText(true);
        
        // Blood Group
        Label groupLabel = new Label("Blood Group: " + demande.getGroupeSanguin());
        groupLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        
        // Quantity
        Label quantityLabel = new Label(String.format("Quantity: %.2f L", demande.getQuantite()));
        quantityLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        
        // Created date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String createdDate = demande.getCreatedAt() != null ? dateFormat.format(demande.getCreatedAt()) : "N/A";
        Label dateLabel = new Label("Created: " + createdDate);
        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        dateLabel.setTextFill(Color.GRAY);
        
        // Add all elements to card
        card.getChildren().addAll(
                idLabel, statusBox, centreLabel, 
                groupLabel, quantityLabel, dateLabel
        );
        
        // Add selection behavior
        card.setOnMouseClicked(event -> {
            // Deselect any previously selected card
            for (Node node : demandesFlowPane.getChildren()) {
                if (node instanceof VBox) {
                    VBox vbox = (VBox) node;
                    DemandeDonSang cardDemande = (DemandeDonSang) vbox.getUserData();
                    
                    // Reset card style based on status
                    String color;
                    switch (cardDemande.getStatus()) {
                        case "accepted":
                            color = "#e8f5e9";
                            break;
                        case "refused":
                            color = "#ffebee";
                            break;
                        default: // pending
                            color = "#fff8e1";
                            break;
                    }
                    
                    vbox.setStyle("-fx-background-color: " + color + "; -fx-border-color: #dddddd; " +
                            "-fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                }
            }
            
            // Select this card
            card.setStyle("-fx-background-color: " + statusColor + "; -fx-border-color: #4a86e8; " +
                    "-fx-border-radius: 5; -fx-border-width: 2; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            selectedDemande = demande;
            
            // Double-click to update status
            if (event.getClickCount() == 2) {
                handleUpdateStatus();
            }
        });
        
        // Store the demande object in the card user data for reference
        card.setUserData(demande);
        
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