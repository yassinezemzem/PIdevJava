package Controllers;

import Entities.DemandeDonSang;
import Services.DemandeDonSangService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DemandeDonSangStatisticsController implements Initializable {
    @FXML
    private PieChart bloodGroupChart;
    @FXML
    private BarChart<String, Number> centreChart;
    @FXML
    private LineChart<String, Number> monthlyChart;

    private final DemandeDonSangService demandeService = new DemandeDonSangService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<DemandeDonSang> demandes = demandeService.getAllDemandes();
        updateBloodGroupChart(demandes);
        updateCentreChart(demandes);
        updateMonthlyChart(demandes);
    }

    private void updateBloodGroupChart(List<DemandeDonSang> demandes) {
        bloodGroupChart.getData().clear();
        Map<String, Long> data = demandes.stream()
                .collect(Collectors.groupingBy(DemandeDonSang::getGroupeSanguin, Collectors.counting()));
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        data.forEach((group, count) -> pieChartData.add(new PieChart.Data(group, count)));
        bloodGroupChart.setData(pieChartData);
        bloodGroupChart.setTitle("Requests by Blood Group");
    }

    private void updateCentreChart(List<DemandeDonSang> demandes) {
        centreChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Long> data = demandes.stream()
                .collect(Collectors.groupingBy(d -> d.getCentreDeDon().getName(), Collectors.counting()));
        data.forEach((centre, count) -> series.getData().add(new XYChart.Data<>(centre, count)));
        centreChart.getData().add(series);
        centreChart.setTitle("Requests by Centre");
    }

    private void updateMonthlyChart(List<DemandeDonSang> demandes) {
        monthlyChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Map<String, Long> data = demandes.stream()
                .collect(Collectors.groupingBy(d -> sdf.format(d.getCreatedAt()), Collectors.counting()));
        data.forEach((month, count) -> series.getData().add(new XYChart.Data<>(month, count)));
        monthlyChart.getData().add(series);
        monthlyChart.setTitle("Requests by Month");
    }
} 