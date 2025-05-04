package Controllers;

import Entities.ServiceProvider;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import Services.ServiceProviderService;
import utils.SmsSender;

import java.io.IOException;
import java.util.Locale;

public class ServiceProviderFormController {

    @FXML private TextField nameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> serviceComboBox;
    @FXML private WebView mapWebView;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Button goToTableViewButton;

    private final ServiceProviderService serviceProviderService = new ServiceProviderService();
    private ServiceProviderTableController tableController;
    private WebEngine webEngine;
    private ServiceProvider serviceProviderBeingEdited;

    @FXML
    public void initialize() {
        serviceComboBox.getItems().setAll("Nurse", "Physiotherapist", "Doctor", "Lab Test");
        if (mapWebView != null) {
            setupMap();
        }
    }

    public void setTableController(ServiceProviderTableController controller) {
        this.tableController = controller;
    }

    @FXML
    public void handleReturnButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AppointmentsPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Appointments");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to return to appointments page: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleAddServiceProvider() {
        ServiceProvider provider = getServiceProviderFromForm();
        if (provider == null) return;

        try {
            if (serviceProviderBeingEdited != null) {
                provider.setId(serviceProviderBeingEdited.getId());
                serviceProviderService.updateServiceProvider(provider);
                showAlert("Service provider updated successfully.");
                serviceProviderBeingEdited = null;
            } else {
                serviceProviderService.saveServiceProvider(provider);
                SmsSender.sendSms(
                    provider.getPhone(),
                    "✅ Hello " + provider.getName() + ", your service provider information has been successfully saved, we'll contact you soon."
                );
                showAlert("Service provider saved successfully.");
            }

            if (tableController != null) {
                tableController.loadServiceProviders();
            }

            clearForm();
        } catch (Exception e) {
            showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ServiceProvider getServiceProviderFromForm() {
        String name = nameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String service = serviceComboBox.getValue();
        String latitudeText = latitudeField.getText().trim();
        String longitudeText = longitudeField.getText().trim();

        if (name.isEmpty() || lastName.isEmpty() || phone.isEmpty() || service == null ||
                latitudeText.isEmpty() || longitudeText.isEmpty()) {
            showAlert("All fields must be filled out.");
            return null;
        }

        if (!phone.matches("\\d{8}")) {
            showAlert("Phone must be exactly 8 digits.");
            return null;
        }

        phone = "+216" + phone;

        try {
            double latitude = Double.parseDouble(latitudeText);
            double longitude = Double.parseDouble(longitudeText);
            return new ServiceProvider(name, lastName, phone, service, latitude, longitude);
        } catch (NumberFormatException e) {
            showAlert("Latitude and Longitude must be valid numbers.");
            return null;
        }
    }

    public void prefillForm(ServiceProvider provider) {
        nameField.setText(provider.getName());
        lastNameField.setText(provider.getLastName());

        String phone = provider.getPhone();
        if (phone.startsWith("+216")) {
            phone = phone.substring(4);
        }
        phoneField.setText(phone);

        serviceComboBox.setValue(provider.getService());
        latitudeField.setText(String.valueOf(provider.getLatitude()));
        longitudeField.setText(String.valueOf(provider.getLongitude()));

        serviceProviderBeingEdited = provider;
    }

    private void clearForm() {
        nameField.clear();
        lastNameField.clear();
        phoneField.clear();
        serviceComboBox.setValue(null);
        latitudeField.clear();
        longitudeField.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void handleGoToTableView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/service_provider_table.fxml"));
            Parent root = loader.load();

            ServiceProviderTableController controller = loader.getController();
            controller.loadServiceProviders();

            Stage stage = (Stage) mapWebView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading table view: " + e.getMessage());
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
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaConnector", new JavaConnector());

                    Platform.runLater(() -> {
                        webEngine.executeScript("initializeMap()");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error setting up map: " + e.getMessage());
                }
            }
        });

        String mapHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>Current Location</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css" />
                <style>
                    html, body { height: 100%; margin: 0; padding: 0; }
                    #map { height: 100%; width: 100%; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
                <script>
                    let map;
                    let marker;

                    function initializeMap() {
                        map = L.map('map').setView([36.8189, 10.1657], 13);
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            maxZoom: 18,
                            attribution: '© OpenStreetMap contributors'
                        }).addTo(map);

                        addDefaultMarker();

                        map.on('click', function(e) {
                            updateMarkerPosition(e.latlng.lat, e.latlng.lng);
                        });
                    }

                    function addDefaultMarker() {
                        const center = map.getCenter();
                        marker = L.marker([center.lat, center.lng]).addTo(map)
                            .bindPopup("Default location - click map to change").openPopup();

                        if (window.javaConnector) {
                            window.javaConnector.updateCoordinates(center.lat, center.lng);
                        }
                    }

                    function updateMarkerPosition(lat, lng) {
                        if (marker) {
                            map.removeLayer(marker);
                        }

                        marker = L.marker([lat, lng]).addTo(map)
                            .bindPopup("Selected location").openPopup();

                        if (window.javaConnector) {
                            window.javaConnector.updateCoordinates(lat, lng);
                        }
                    }
                </script>
            </body>
            </html>
        """;

        Platform.runLater(() -> {
            webEngine.loadContent(mapHtml);
        });
    }
} 