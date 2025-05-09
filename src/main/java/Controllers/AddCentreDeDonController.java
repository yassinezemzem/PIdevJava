package Controllers;

import Entities.CentreDeDon;
import Services.CentreDeDonService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.ValidationUtils;
import javafx.event.ActionEvent;
import utils.Utils;

public class AddCentreDeDonController {
    
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
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Label validationMessageLabel;
    
    private CentreDeDonController controller;
    private CentreDeDonService centreDeDonService;
    
    @FXML
    private void initialize() {
        // Initialize controller
        controller = new CentreDeDonController();
        centreDeDonService = new CentreDeDonService();
        
        // Initialize validation message label if it exists
        if (validationMessageLabel != null) {
            validationMessageLabel.setTextFill(Color.RED);
            validationMessageLabel.setText("");
        }
        
        // Setup validation for text fields
        ValidationUtils.setupLengthValidation(nameField, 30);
        ValidationUtils.setupLengthValidation(addressField, 100);
        ValidationUtils.setupLengthValidation(telephoneField, 10);
        ValidationUtils.setupLengthValidation(emailField, 100);
        
        // Setup numeric validation for coordinates
        ValidationUtils.setupNumericValidation(latitudeField, true);
        ValidationUtils.setupNumericValidation(longitudeField, true);
        
        // Add validation listeners for fields
        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // when focus is lost
                validateName();
            }
        });
        
        // Add field-specific hint display on focus
        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // when focus gained
                showValidationHint("Le nom doit contenir entre 6 et 30 caractères");
            } else {
                validateName();
            }
        });
        
        addressField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showValidationHint("L'adresse doit contenir au moins 5 caractères");
            } else {
                validateAddress();
            }
        });
        
        telephoneField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showValidationHint("Le numéro doit contenir uniquement des chiffres (maximum 10)");
            } else {
                validatePhone();
            }
        });
        
        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showValidationHint("Format d'email requis: exemple@domaine.com");
            } else {
                validateEmail();
            }
        });
        
        latitudeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showValidationHint("La latitude doit être un nombre entre -90 et 90");
            } else {
                validateLatitude();
            }
        });
        
        longitudeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showValidationHint("La longitude doit être un nombre entre -180 et 180");
            } else {
                validateLongitude();
            }
        });
    }
    
    /**
     * Shows a validation hint message in the validation message label
     * @param message The hint message to display
     */
    private void showValidationHint(String message) {
        if (validationMessageLabel != null) {
            validationMessageLabel.setTextFill(Color.BLUE);
            validationMessageLabel.setText("Aide: " + message);
        }
    }
    
    /**
     * Shows a validation error message in the validation message label
     * @param message The error message to display
     */
    private void showValidationError(String message) {
        if (validationMessageLabel != null) {
            validationMessageLabel.setTextFill(Color.RED);
            validationMessageLabel.setText("Erreur: " + message);
        }
    }
    
    /**
     * Clears the validation message label
     */
    private void clearValidationMessage() {
        if (validationMessageLabel != null) {
            validationMessageLabel.setText("");
        }
    }
    
    @FXML
    private void onSave(ActionEvent event) {
        try {
            // Validate all fields
            boolean isNameValid = validateName();
            boolean isAddressValid = validateAddress();
            boolean isPhoneValid = validatePhone();
            boolean isEmailValid = validateEmail();
            boolean isLatitudeValid = validateLatitude();
            boolean isLongitudeValid = validateLongitude();
            
            if (!isNameValid || !isAddressValid || !isPhoneValid || !isEmailValid || 
                !isLatitudeValid || !isLongitudeValid) {
                Utils.showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez corriger les erreurs de saisie");
                return;
            }
            
            // Get values from fields
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String email = emailField.getText().trim();
            
            // Get latitude and longitude if provided
            double latitude = 0.0;
            double longitude = 0.0;
            try {
                if (!latitudeField.getText().trim().isEmpty()) {
                    latitude = Double.parseDouble(latitudeField.getText().trim());
                }
                if (!longitudeField.getText().trim().isEmpty()) {
                    longitude = Double.parseDouble(longitudeField.getText().trim());
                }
            } catch (NumberFormatException e) {
                Utils.showAlert(Alert.AlertType.ERROR, "Erreur de format", "Latitude et longitude doivent être des nombres valides");
                return;
            }
            
            centreDeDonService.createCentre(name, address, telephone, email, latitude, longitude);
            Utils.showAlert(Alert.AlertType.INFORMATION, "Succès", "Centre de don ajouté avec succès");
            resetFields();
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout du centre: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        // Check required fields
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            errorMessage.append("Name is required.\n");
        } else if (name.length() < 6) {
            errorMessage.append("Name must be at least 6 characters long.\n");
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
            errorMessage.append("Email format is invalid. Must contain @ and end with a domain (e.g., .com).\n");
        }
        
        // Validate coordinates if provided
        try {
            if (!latitudeField.getText().trim().isEmpty()) {
                double lat = Double.parseDouble(latitudeField.getText().trim());
                if (lat < -90 || lat > 90) {
                    errorMessage.append("Latitude must be between -90 and 90.\n");
                }
            }
            
            if (!longitudeField.getText().trim().isEmpty()) {
                double lon = Double.parseDouble(longitudeField.getText().trim());
                if (lon < -180 || lon > 180) {
                    errorMessage.append("Longitude must be between -180 and 180.\n");
                }
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Latitude and Longitude must be valid numbers.\n");
        }
        
        // Show errors if any
        if (errorMessage.length() > 0) {
            Utils.showAlert(Alert.AlertType.ERROR, "Validation Error", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private boolean validateName() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            ValidationUtils.setErrorStyle(nameField);
            ValidationUtils.setTooltip(nameField, "Le nom est requis");
            showValidationError("Le nom est requis");
            return false;
        }
        
        if (name.length() < 6) {
            ValidationUtils.setErrorStyle(nameField);
            ValidationUtils.setTooltip(nameField, "Le nom doit contenir au moins 6 caractères");
            showValidationError("Le nom doit contenir au moins 6 caractères");
            return false;
        }
        
        if (name.length() > 30) {
            ValidationUtils.setErrorStyle(nameField);
            ValidationUtils.setTooltip(nameField, "Le nom ne doit pas dépasser 30 caractères");
            showValidationError("Le nom ne doit pas dépasser 30 caractères");
            return false;
        }
        
        ValidationUtils.setSuccessStyle(nameField);
        ValidationUtils.setTooltip(nameField, "Nom valide");
        clearValidationMessage();
        return true;
    }
    
    private boolean validateAddress() {
        String address = addressField.getText().trim();
        if (!ValidationUtils.hasMinimumLength(address, 5)) {
            ValidationUtils.setErrorStyle(addressField);
            ValidationUtils.setTooltip(addressField, "L'adresse doit contenir au moins 5 caractères");
            showValidationError("L'adresse doit contenir au moins 5 caractères");
            return false;
        }
        ValidationUtils.setSuccessStyle(addressField);
        ValidationUtils.setTooltip(addressField, "Adresse valide");
        clearValidationMessage();
        return true;
    }
    
    private boolean validatePhone() {
        String phone = telephoneField.getText().trim();
        if (phone.isEmpty()) {
            ValidationUtils.setErrorStyle(telephoneField);
            ValidationUtils.setTooltip(telephoneField, "Le numéro de téléphone est requis");
            showValidationError("Le numéro de téléphone est requis");
            return false;
        }
        
        if (!phone.matches("\\d+")) {
            ValidationUtils.setErrorStyle(telephoneField);
            ValidationUtils.setTooltip(telephoneField, "Le numéro doit contenir uniquement des chiffres");
            showValidationError("Le numéro doit contenir uniquement des chiffres");
            return false;
        }
        
        if (phone.length() > 10) {
            ValidationUtils.setErrorStyle(telephoneField);
            ValidationUtils.setTooltip(telephoneField, "Le numéro ne doit pas dépasser 10 chiffres");
            showValidationError("Le numéro ne doit pas dépasser 10 chiffres");
            return false;
        }
        
        ValidationUtils.setSuccessStyle(telephoneField);
        ValidationUtils.setTooltip(telephoneField, "Numéro de téléphone valide");
        clearValidationMessage();
        return true;
    }
    
    private boolean validateEmail() {
        String email = emailField.getText().trim();
        if (!ValidationUtils.isValidEmail(email)) {
            ValidationUtils.setErrorStyle(emailField);
            ValidationUtils.setTooltip(emailField, "Format d'email invalide");
            showValidationError("Format d'email invalide (exemple@domaine.com)");
            return false;
        }
        ValidationUtils.setSuccessStyle(emailField);
        ValidationUtils.setTooltip(emailField, "Email valide");
        clearValidationMessage();
        return true;
    }
    
    private boolean validateLatitude() {
        String latText = latitudeField.getText().trim();
        if (latText.isEmpty()) {
            clearValidationMessage();
            return true; // Optional field
        }
        
        try {
            double latitude = Double.parseDouble(latText);
            if (latitude < -90 || latitude > 90) {
                ValidationUtils.setErrorStyle(latitudeField);
                ValidationUtils.setTooltip(latitudeField, "La latitude doit être entre -90 et 90");
                showValidationError("La latitude doit être entre -90 et 90");
                return false;
            }
            
            ValidationUtils.setSuccessStyle(latitudeField);
            ValidationUtils.setTooltip(latitudeField, "Latitude valide");
            clearValidationMessage();
            return true;
        } catch (NumberFormatException e) {
            ValidationUtils.setErrorStyle(latitudeField);
            ValidationUtils.setTooltip(latitudeField, "La latitude doit être un nombre valide");
            showValidationError("La latitude doit être un nombre valide");
            return false;
        }
    }
    
    private boolean validateLongitude() {
        String longText = longitudeField.getText().trim();
        if (longText.isEmpty()) {
            clearValidationMessage();
            return true; // Optional field
        }
        
        try {
            double longitude = Double.parseDouble(longText);
            if (longitude < -180 || longitude > 180) {
                ValidationUtils.setErrorStyle(longitudeField);
                ValidationUtils.setTooltip(longitudeField, "La longitude doit être entre -180 et 180");
                showValidationError("La longitude doit être entre -180 et 180");
                return false;
            }
            
            ValidationUtils.setSuccessStyle(longitudeField);
            ValidationUtils.setTooltip(longitudeField, "Longitude valide");
            clearValidationMessage();
            return true;
        } catch (NumberFormatException e) {
            ValidationUtils.setErrorStyle(longitudeField);
            ValidationUtils.setTooltip(longitudeField, "La longitude doit être un nombre valide");
            showValidationError("La longitude doit être un nombre valide");
            return false;
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Resets all form fields to empty values and clears styling
     */
    private void resetFields() {
        nameField.clear();
        addressField.clear();
        telephoneField.clear();
        emailField.clear();
        latitudeField.clear();
        longitudeField.clear();
        
        // Clear styling
        ValidationUtils.clearStyle(nameField);
        ValidationUtils.clearStyle(addressField);
        ValidationUtils.clearStyle(telephoneField);
        ValidationUtils.clearStyle(emailField);
        ValidationUtils.clearStyle(latitudeField);
        ValidationUtils.clearStyle(longitudeField);
    }
    
    private boolean isValidEmail(String email) {
        // Enhanced email validation
        // Must contain @ and end with a domain (e.g., .com, .org, etc.)
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
} 