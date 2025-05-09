package Controllers;

import Entities.Appointment;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import Services.AppointmentService;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class AppointmentTableController {

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> nameColumn;
    @FXML private TableColumn<Appointment, String> emailColumn;
    @FXML private TableColumn<Appointment, String> phoneColumn;
    @FXML private TableColumn<Appointment, String> serviceColumn;
    @FXML private TableColumn<Appointment, Date> dateColumn;
    @FXML private TableColumn<Appointment, Double> latitudeColumn;
    @FXML private TableColumn<Appointment, Double> longitudeColumn;
    @FXML private Button deleteButton;
    @FXML private Button modifyButton;
    @FXML private Button returnToAddAppointmentButton;

    private final AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAppointments();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));

        // Format latitude and longitude to display 6 decimal places
        latitudeColumn.setCellFactory(column -> new TableCell<Appointment, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.6f", item));
                }
            }
        });

        longitudeColumn.setCellFactory(column -> new TableCell<Appointment, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.6f", item));
                }
            }
        });
    }

    private void loadAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        appointmentTable.setItems(FXCollections.observableArrayList(appointments));
    }

    @FXML
    private void handleDelete() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            try {
                appointmentService.deleteAppointment(selectedAppointment.getId());
                loadAppointments();
                showAlert("Success", "Appointment deleted successfully!");
            } catch (Exception e) {
                showAlert("Error", "Failed to delete appointment: " + e.getMessage());
            }
        } else {
            showAlert("Warning", "Please select an appointment to delete.");
        }
    }

    @FXML
    private void handleModify() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_appointment.fxml"));
                Parent root = loader.load();
                AddAppointmentController controller = loader.getController();
                controller.prefillForm(selectedAppointment);

                Stage stage = (Stage) appointmentTable.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                showAlert("Error", "Failed to load modification form: " + e.getMessage());
            }
        } else {
            showAlert("Warning", "Please select an appointment to modify.");
        }
    }

    @FXML
    private void handleReturnToAddAppointment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_appointment.fxml"));
            Parent view = loader.load();
            Scene scene = returnToAddAppointmentButton.getScene();
            StackPane contentArea = (StackPane) scene.lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            } else {
                showAlert("Error", "Could not find content area");
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to return to add appointment form: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 