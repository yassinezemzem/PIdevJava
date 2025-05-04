package Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Services.ServiceTherapie;
import utils.MyConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class TherapieStatController {
    @FXML private PieChart pie;
    @FXML private Button   close;
    private final ServiceTherapie service = new ServiceTherapie();

    public void initialize() {
        buildChart();
    }
    
    @FXML private void refreshChart() { buildChart(); }
    
    @FXML private void close() {
        ((Stage) pie.getScene().getWindow()).close();
    }
    
    private void buildChart() {
        Map<String, Integer> counts = service.loadCountsByType();
        var data = new ArrayList<PieChart.Data>();
        counts.forEach((type, n) -> data.add(new PieChart.Data(type + " (" + n + ")", n)));
        pie.setData(FXCollections.observableArrayList(data));
    }
} 