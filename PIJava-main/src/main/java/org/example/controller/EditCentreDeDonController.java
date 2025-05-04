package org.example.controller;

import org.example.entities.CentreDeDon;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditCentreDeDonController {
    
    @FXML
    private Label idLabel;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField addressField;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField latitudeField;
    
    @FXML
    private TextField longitudeField;
    
    @FXML
    private Label createdAtLabel;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private CentreDeDonController controller;
    private CentreDeDon centreDeDon;
    
    @FXML
    private void initialize() {
        // Initialize controller
        controller = new CentreDeDonController();
        
        // Add validation listeners for numeric fields
        setupValidation();
    }
    
    /**
     * Set the centre de don to be edited
     * @param centreId the ID of the centre to edit
     */
    public void setCentreId(int centreId) {
        try {
            // Load centre data from database
            this.centreDeDon = controller.getCentreById(centreId);
            
            if (centreDeDon == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Centre not found with ID: " + centreId);
                closeWindow();
                return;
            }
            
            // Populate fields with centre data
            populateFields();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading centre: " + e.getMessage());
            e.printStackTrace();
            closeWindow();
        }
    }
    
    /**
     * Set the centre de don to be edited directly
     * @param centre the centre object to edit
     */
    public void setCentreDeDon(CentreDeDon centre) {
        if (centre == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Centre cannot be null");
            closeWindow();
            return;
        }
        
        this.centreDeDon = centre;
        populateFields();
    }
    
    private void populateFields() {
        // Set ID label
        idLabel.setText("ID: " + centreDeDon.getId());
        
        // Populate text fields
        nameField.setText(centreDeDon.getName());
        addressField.setText(centreDeDon.getAddress());
        telephoneField.setText(centreDeDon.getTelephone());
        emailField.setText(centreDeDon.getEmail() != null ? centreDeDon.getEmail() : "");
        
        // Format double values
        latitudeField.setText(String.valueOf(centreDeDon.getLatitude()));
        longitudeField.setText(String.valueOf(centreDeDon.getLongitude()));
        
        // Format date
        Date createdAt = centreDeDon.getCreatedAt();
        if (createdAt != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            createdAtLabel.setText(dateFormat.format(createdAt));
        } else {
            createdAtLabel.setText("N/A");
        }
    }
    
    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }
        
        try {
            // Update centre with form data
            centreDeDon.setName(nameField.getText().trim());
            centreDeDon.setAddress(addressField.getText().trim());
            centreDeDon.setTelephone(telephoneField.getText().trim());
            centreDeDon.setEmail(emailField.getText().trim());
            
            // Parse coordinates
            try {
                centreDeDon.setLatitude(Double.parseDouble(latitudeField.getText().trim()));
                centreDeDon.setLongitude(Double.parseDouble(longitudeField.getText().trim()));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid coordinates format");
                return;
            }
            
            // Use controller to update the centre
            CentreDeDon updatedCentre = controller.updateCentre(centreDeDon);
            
            if (updatedCentre != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Centre de Don updated successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update centre");
            }
            
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        // Check required fields
        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Name is required.\n");
        }
        
        if (addressField.getText().trim().isEmpty()) {
            errorMessage.append("Address is required.\n");
        }
        
        if (telephoneField.getText().trim().isEmpty()) {
            errorMessage.append("Telephone is required.\n");
        }
        
        // Validate email if provided
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            errorMessage.append("Email format is invalid.\n");
        }
        
        // Validate coordinates
        try {
            double lat = Double.parseDouble(latitudeField.getText().trim());
            if (lat < -90 || lat > 90) {
                errorMessage.append("Latitude must be between -90 and 90.\n");
            }
            
            double lon = Double.parseDouble(longitudeField.getText().trim());
            if (lon < -180 || lon > 180) {
                errorMessage.append("Longitude must be between -180 and 180.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Latitude and Longitude must be valid numbers.\n");
        }
        
        // Show errors if any
        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private void setupValidation() {
        // Numeric input validation for latitude and longitude
        latitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*(\\.\\d*)?")) {
                latitudeField.setText(oldValue);
            }
        });
        
        longitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*(\\.\\d*)?")) {
                longitudeField.setText(oldValue);
            }
        });
        
        // Limit telephone to reasonable length
        telephoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 20) {
                telephoneField.setText(oldValue);
            }
        });
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
} 