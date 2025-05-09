package Controllers;

import Entities.Appointment;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import Services.AppointmentService;
import utils.SmsSender;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Locale;

public class AddAppointmentController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> serviceComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private WebView mapWebView;
    @FXML private Button btnViewAppointments;

    private final AppointmentService appointmentService = new AppointmentService();
    private WebEngine webEngine;
    private Appointment appointmentBeingEdited;

    @FXML
    public void initialize() {
        serviceComboBox.getItems().setAll("Nurse", "Physiotherapist", "Doctor", "Lab Test");
        if (mapWebView != null) {
            setupMap();
        }
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        try {
            Appointment appointment = getAppointmentFromForm();
            if (appointment == null) return;

            if (appointmentBeingEdited != null) {
                appointment.setId(appointmentBeingEdited.getId());
                appointmentService.updateAppointment(appointment);
                showAlert("Success", "Appointment updated successfully.");
                appointmentBeingEdited = null;
            } else {
                appointmentService.saveAppointment(appointment);
                SmsSender.sendSms(
                        appointment.getPhone(),
                        "✅ Hello " + appointment.getName() + ", your appointment has been successfully scheduled."
                );
                showAlert("Success", "Appointment saved successfully.");
            }

            clearForm();
            navigateToAppointmentTable(event);

        } catch (Exception e) {
            showAlert("Error", "Error saving appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAppointments(ActionEvent event) {
        navigateToAppointmentTable(event);
    }

    @FXML
    public void handleReturnButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AppointmentsPage.fxml"));
            Parent view = loader.load();
            Node source = (Node) event.getSource();
            Scene scene = source.getScene();
            StackPane contentArea = (StackPane) scene.lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            } else {
                showAlert("Error", "Could not find content area");
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to return to appointments page: " + e.getMessage());
        }
    }

    private Appointment getAppointmentFromForm() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String service = serviceComboBox.getValue();
        LocalDate date = datePicker.getValue();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || service == null || date == null) {
            showAlert("Error", "All fields must be filled out.");
            return null;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Error", "Invalid email format.");
            return null;
        }

        if (!phone.matches("\\d{8}")) {
            showAlert("Error", "Phone must be exactly 8 digits.");
            return null;
        }

        phone = "+216" + phone;

        String latitudeText = latitudeField.getText().trim();
        String longitudeText = longitudeField.getText().trim();

        if (latitudeText.isEmpty() || longitudeText.isEmpty()) {
            showAlert("Error", "Please select a location on the map.");
            return null;
        }

        double latitude = Double.parseDouble(latitudeText);
        double longitude = Double.parseDouble(longitudeText);

        return new Appointment(name, email, phone, service, Date.valueOf(date), latitude, longitude);
    }

    public void prefillForm(Appointment appointment) {
        nameField.setText(appointment.getName());
        emailField.setText(appointment.getEmail());

        String phone = appointment.getPhone();
        if (phone.startsWith("+216")) {
            phone = phone.substring(4);
        }
        phoneField.setText(phone);

        serviceComboBox.setValue(appointment.getService());
        datePicker.setValue(appointment.getDate().toLocalDate());
        latitudeField.setText(String.valueOf(appointment.getLatitude()));
        longitudeField.setText(String.valueOf(appointment.getLongitude()));

        appointmentBeingEdited = appointment;
    }

    private void clearForm() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        serviceComboBox.setValue(null);
        datePicker.setValue(null);
        latitudeField.clear();
        longitudeField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToAppointmentTable(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/appointment_table.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error navigating to appointments table: ", e.getMessage());
        }
    }

    public class JavaConnector {
        public void updateCoordinates(double latitude, double longitude) {
            Platform.runLater(() -> {
                latitudeField.setText(String.format(Locale.US, "%.6f", latitude));
                longitudeField.setText(String.format(Locale.US, "%.6f", longitude));
            });
        }
    }

    private void setupMap() {
        webEngine = mapWebView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            // No JS-Java communication needed for this simple map
        });

        String mapHtml = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Leaflet Map</title>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <style>
                    html, body { height: 100%; margin: 0; padding: 0; }
                    #map { width: 100%; height: 100%; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <script>
                    var map = L.map('map').setView([36.8189, 10.1657], 13);
                    
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    }).addTo(map);

                    var marker = L.marker([36.8189, 10.1657]).addTo(map)
                        .bindPopup("Click map to change location").openPopup();

                    map.on('click', function(e) {
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        marker = L.marker(e.latlng).addTo(map)
                            .bindPopup("Selected location").openPopup();
                        
                        if (window.javaConnector) {
                            window.javaConnector.updateCoordinates(e.latlng.lat, e.latlng.lng);
                        }
                    });
                </script>
            </body>
            </html>
        """;

        Platform.runLater(() -> {
            webEngine.loadContent(mapHtml);
        });
    }
}
