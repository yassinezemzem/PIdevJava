package Controllers;

import Entities.CentreDeDon;
import Entities.DemandeDonSang;
import Entities.User;
import Services.CentreDeDonService;
import Services.DemandeDonSangService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.SessionManager;
import utils.ValidationUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddDemandeDonSangController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ComboBox<CentreDeDon> centreDeDonComboBox;

    @FXML
    private ComboBox<String> groupeSanguinComboBox;

    @FXML
    private TextField quantiteField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final DemandeDonSangService demandeService;
    private final CentreDeDonService centreService;
    private User currentUser;

    public AddDemandeDonSangController() {
        this.demandeService = new DemandeDonSangService();
        this.centreService = new CentreDeDonService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get current user
        currentUser = SessionManager.getCurrentUser();
        
        // Setup blood group options
        groupeSanguinComboBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        
        // Load donation centers
        loadCentres();
        
        // Add validation listeners
        ValidationUtils.setupNumericValidation(quantiteField, false);
        
        // Add focus listener for validation
        quantiteField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // when focus is lost
                validateQuantity(quantiteField.getText());
            }
        });

        // Add change listeners for combo boxes
        centreDeDonComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                ValidationUtils.setErrorStyle(centreDeDonComboBox);
                ValidationUtils.setTooltip(centreDeDonComboBox, "Veuillez sélectionner un centre de don");
            } else {
                ValidationUtils.setSuccessStyle(centreDeDonComboBox);
                ValidationUtils.setTooltip(centreDeDonComboBox, "Centre de don valide");
            }
        });

        groupeSanguinComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                ValidationUtils.setErrorStyle(groupeSanguinComboBox);
                ValidationUtils.setTooltip(groupeSanguinComboBox, "Veuillez sélectionner un groupe sanguin");
            } else {
                ValidationUtils.setSuccessStyle(groupeSanguinComboBox);
                ValidationUtils.setTooltip(groupeSanguinComboBox, "Groupe sanguin valide");
            }
        });
        
        // Set initial validation state
        if (centreDeDonComboBox.getValue() == null) {
            ValidationUtils.setErrorStyle(centreDeDonComboBox);
            ValidationUtils.setTooltip(centreDeDonComboBox, "Veuillez sélectionner un centre de don");
        }
        
        if (groupeSanguinComboBox.getValue() == null || groupeSanguinComboBox.getValue().isEmpty()) {
            ValidationUtils.setErrorStyle(groupeSanguinComboBox);
            ValidationUtils.setTooltip(groupeSanguinComboBox, "Veuillez sélectionner un groupe sanguin");
        }
    }

    private boolean validateQuantity(String quantityText) {
        if (!ValidationUtils.isNumeric(quantityText)) {
            ValidationUtils.setErrorStyle(quantiteField);
            ValidationUtils.setTooltip(quantiteField, "La quantité doit être un nombre valide");
            return false;
        }
        
        double quantity = Double.parseDouble(quantityText);
        if (quantity <= 0) {
            ValidationUtils.setErrorStyle(quantiteField);
            ValidationUtils.setTooltip(quantiteField, "La quantité doit être supérieure à 0");
            return false;
        }
        
        if (quantity > 5) {
            ValidationUtils.setErrorStyle(quantiteField);
            ValidationUtils.setTooltip(quantiteField, "La quantité ne peut pas dépasser 5 litres");
            return false;
        }
        
        ValidationUtils.setSuccessStyle(quantiteField);
        ValidationUtils.setTooltip(quantiteField, "Quantité valide");
        return true;
    }

    private void loadCentres() {
        List<CentreDeDon> centres = centreService.getAllCentres();
        ObservableList<CentreDeDon> centreList = FXCollections.observableArrayList(centres);
        centreDeDonComboBox.setItems(centreList);
        
        // Set a cell factory to display only the name of the centre
        centreDeDonComboBox.setCellFactory(param -> new ListCell<CentreDeDon>() {
            @Override
            protected void updateItem(CentreDeDon centre, boolean empty) {
                super.updateItem(centre, empty);
                if (empty || centre == null) {
                    setText(null);
                } else {
                    setText(centre.getName());
                }
            }
        });
        
        // Set a converter to display only the name in the ComboBox button
        centreDeDonComboBox.setButtonCell(new ListCell<CentreDeDon>() {
            @Override
            protected void updateItem(CentreDeDon centre, boolean empty) {
                super.updateItem(centre, empty);
                if (empty || centre == null) {
                    setText(null);
                } else {
                    setText(centre.getName());
                }
            }
        });
    }

    @FXML
    void onSave(ActionEvent event) {
        try {
            // Check if user is logged in
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez être connecté pour faire une demande de don");
                return;
            }

            // Validate center selection
            if (centreDeDonComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez sélectionner un centre de don");
                ValidationUtils.setErrorStyle(centreDeDonComboBox);
                return;
            }

            // Validate blood group selection
            if (groupeSanguinComboBox.getValue() == null || groupeSanguinComboBox.getValue().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez sélectionner un groupe sanguin");
                ValidationUtils.setErrorStyle(groupeSanguinComboBox);
                return;
            }

            // Validate quantity
            String quantityText = quantiteField.getText();
            if (!ValidationUtils.isNumeric(quantityText)) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez entrer une quantité valide");
                ValidationUtils.setErrorStyle(quantiteField);
                return;
            }

            double quantity = Double.parseDouble(quantityText);
            if (quantity <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "La quantité doit être supérieure à 0");
                ValidationUtils.setErrorStyle(quantiteField);
                return;
            }

            if (quantity > 5) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "La quantité ne peut pas dépasser 5 litres");
                ValidationUtils.setErrorStyle(quantiteField);
                return;
            }

            // Get selected center
            CentreDeDon selectedCenter = centreDeDonComboBox.getValue();
            int centreId = selectedCenter.getId();

            // Get selected blood group
            String groupeSanguin = groupeSanguinComboBox.getValue();

            // Create the donation request with the current user
            DemandeDonSang demande = demandeService.createDemande(currentUser.getId(), centreId, groupeSanguin, quantity);

            if (demande != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Donation request created successfully");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create donation request");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite: " + e.getMessage());
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 