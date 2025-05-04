package org.example.controller;
import org.example.entities.User;
import org.example.entities.CentreDeDon;
import org.example.entities.DemandeDonSang;
import org.example.entities.User;
import org.example.service.DemandeDonSangService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DemandeDonSangListController implements Initializable {

    @FXML
    private TableView<DemandeDonSang> demandeTable;

    @FXML
    private TableColumn<DemandeDonSang, Integer> idColumn;

    @FXML
    private TableColumn<DemandeDonSang, CentreDeDon> centreColumn;

    @FXML
    private TableColumn<DemandeDonSang, String> groupeSanguinColumn;

    @FXML
    private TableColumn<DemandeDonSang, Double> quantiteColumn;

    @FXML
    private TableColumn<DemandeDonSang, String> statusColumn;

    @FXML
    private TableColumn<DemandeDonSang, Date> createdAtColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button refreshButton;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    // Chart fields
    @FXML
    private PieChart bloodGroupChart;

    @FXML
    private BarChart<String, Number> centreChart;

    @FXML
    private LineChart<String, Number> monthlyChart;

    private final DemandeDonSangService demandeService;
    private ObservableList<DemandeDonSang> demandesList;
    private User currentUser;
    private boolean isAdmin;

    public DemandeDonSangListController() {
        this.demandeService = new DemandeDonSangService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get current user and admin status
        currentUser = SessionManager.getCurrentUser();
        isAdmin = SessionManager.isAdmin();

        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        centreColumn.setCellValueFactory(new PropertyValueFactory<>("centreDeDon"));
        groupeSanguinColumn.setCellValueFactory(new PropertyValueFactory<>("groupeSanguin"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Initialize status filter
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "All", "pending", "accepted", "refused"
        );
        statusFilterComboBox.setItems(statusOptions);
        statusFilterComboBox.setValue("All");
        
        // Add listener to status filter
        statusFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterDemandes(newValue);
        });

        // Load data
        loadDemandes();
        updateStatistics();
    }

    private void loadDemandes() {
        List<DemandeDonSang> demandes;
        if (isAdmin) {
            // Admin sees all requests
            demandes = demandeService.getAllDemandes();
        } else {
            // Regular users see only their requests
            demandes = demandeService.getDemandesByUserId(currentUser.getId());
        }
        demandesList = FXCollections.observableArrayList(demandes);
        demandeTable.setItems(demandesList);
    }

    private void filterDemandes(String status) {
        if (status == null || status.equals("All")) {
            loadDemandes();
        } else {
            List<DemandeDonSang> filteredDemandes;
            if (isAdmin) {
                // Admin can filter all requests by status
                filteredDemandes = demandeService.getDemandesByStatus(status);
            } else {
                // Regular users can only filter their own requests by status
                filteredDemandes = demandeService.getDemandesByUserIdAndStatus(currentUser.getId(), status);
            }
            demandesList = FXCollections.observableArrayList(filteredDemandes);
            demandeTable.setItems(demandesList);
        }
        updateStatistics();
    }

    private void updateStatistics() {
        updateBloodGroupChart();
        updateCentreChart();
        updateMonthlyChart();
    }

    private void updateBloodGroupChart() {
        bloodGroupChart.getData().clear();
        
        // Group demands by blood group
        Map<String, Long> bloodGroupCounts = demandesList.stream()
                .collect(Collectors.groupingBy(
                        DemandeDonSang::getGroupeSanguin,
                        Collectors.counting()
                ));
        
        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        bloodGroupCounts.forEach((group, count) -> 
            pieChartData.add(new PieChart.Data(group, count))
        );
        
        bloodGroupChart.setData(pieChartData);
        bloodGroupChart.setTitle("Blood Group Distribution");
    }

    private void updateCentreChart() {
        centreChart.getData().clear();
        
        // Group demands by centre
        Map<String, Long> centreCounts = demandesList.stream()
                .collect(Collectors.groupingBy(
                        demande -> demande.getCentreDeDon().getName(),
                        Collectors.counting()
                ));
        
        // Create bar chart data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Requests by Centre");
        
        centreCounts.forEach((centre, count) -> 
            series.getData().add(new XYChart.Data<>(centre, count))
        );
        
        centreChart.getData().add(series);
    }

    private void updateMonthlyChart() {
        monthlyChart.getData().clear();
        
        // Group demands by month
        Map<String, Long> monthlyCounts = demandesList.stream()
                .collect(Collectors.groupingBy(
                        demande -> {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(demande.getCreatedAt());
                            return String.format("%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
                        },
                        Collectors.counting()
                ));
        
        // Create line chart data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Requests");
        
        // Sort months chronologically
        List<String> sortedMonths = new ArrayList<>(monthlyCounts.keySet());
        Collections.sort(sortedMonths);
        
        sortedMonths.forEach(month -> 
            series.getData().add(new XYChart.Data<>(month, monthlyCounts.get(month)))
        );
        
        monthlyChart.getData().add(series);
    }

    @FXML
    void onAddDemande(ActionEvent event) {
        try {
            // Try first with /fxml/ path
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddDemandeDonSang.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Add Blood Donation Request");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                
                // Show the dialog and wait until it's closed
                stage.showAndWait();
            } catch (Exception e) {
                // If it fails, try with / path
                System.out.println("Trying alternative path for AddDemandeDonSang.fxml");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddDemandeDonSang.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Add Blood Donation Request");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                
                // Show the dialog and wait until it's closed
                stage.showAndWait();
            }
            
            // Refresh the table after adding a new demande
            loadDemandes();
            updateStatistics();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open add form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onRefresh(ActionEvent event) {
        loadDemandes();
        statusFilterComboBox.setValue("All");
        updateStatistics();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

} 