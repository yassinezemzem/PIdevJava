package Controllers;

import Entities.ServiceProvider;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Services.ServiceProviderService;

import java.io.IOException;
import java.util.List;

public class ServiceProviderTableController {

    @FXML private TableView<ServiceProvider> providerTable;
    @FXML private TableColumn<ServiceProvider, String> nameColumn;
    @FXML private TableColumn<ServiceProvider, String> lastNameColumn;
    @FXML private TableColumn<ServiceProvider, String> serviceColumn;
    @FXML private TableColumn<ServiceProvider, String> phoneColumn;
    @FXML private TableColumn<ServiceProvider, String> locationColumn;
    @FXML private Button deleteButton;
    @FXML private Button modifyButton;
    @FXML private Button returnToFormButton;

    private final ServiceProviderService serviceProviderService = new ServiceProviderService();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadServiceProviders();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        locationColumn.setCellValueFactory(cellData -> {
            ServiceProvider provider = cellData.getValue();
            return new ReadOnlyStringWrapper(
                String.format("%.6f, %.6f", provider.getLatitude(), provider.getLongitude())
            );
        });
    }

    public void loadServiceProviders() {
        List<ServiceProvider> providers = serviceProviderService.getAllServiceProviders();
        providerTable.setItems(FXCollections.observableArrayList(providers));
    }

    @FXML
    private void handleDelete() {
        ServiceProvider selected = providerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a service provider to delete.");
            return;
        }

        serviceProviderService.deleteServiceProvider(selected.getId());
        showAlert("Service provider deleted successfully.");
        loadServiceProviders();
    }

    @FXML
    private void handleModify() {
        ServiceProvider selected = providerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a service provider to modify.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/service_provider_form.fxml"));
            Parent root = loader.load();

            ServiceProviderFormController controller = loader.getController();
            controller.prefillForm(selected);

            Stage stage = (Stage) providerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modify Service Provider");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading modification form: " + e.getMessage());
        }
    }

    @FXML
    public void handleReturnToForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service_Providers_form.fxml"));
            Parent root = loader.load();

            ServiceProviderFormController controller = loader.getController();
            controller.setTableController(this);

            Stage stage = (Stage) providerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Service Provider");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading form: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteServiceProvider() {
        // TODO: Add your logic to delete the selected service provider
        System.out.println("Delete Service Provider button clicked!");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
} 