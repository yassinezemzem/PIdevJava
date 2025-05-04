package Controllers;

import Entities.Evenement;
import Entities.Participation;
import Services.EvenementService;
import Services.ParticipantService;
import utils.PDFGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class EventClientController implements Initializable {

    @FXML private TextField txtRecherche;
    @FXML private DatePicker dateDebutFiltre;
    @FXML private DatePicker dateFinFiltre;
    @FXML private Button btnFiltrer;
    @FXML private Button btnStats;
    @FXML private Button btnCalendrier;
    @FXML private ComboBox<String> comboBoxStatut;
    @FXML private Pagination pagination;
    @FXML private HBox weatherBox;
    @FXML private Label weatherLocation;
    @FXML private ImageView weatherIcon;
    @FXML private Label weatherTemp;
    @FXML private Label weatherDesc;
    @FXML private Button btnBack;

    private final EvenementService evenementService = new EvenementService();
    private final ParticipantService participantService = new ParticipantService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String WEATHER_API_KEY = "cb8573f65bacabd1c58aff1bb5c18b92"; // Replace with your API key
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    private List<Evenement> evenementsOriginaux;
    private static final int ITEMS_PER_PAGE = 4;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evenementsOriginaux = evenementService.getAllEvenements();
        initPagination(evenementsOriginaux);

        txtRecherche.textProperty().addListener((obs, o, n) -> appliquerFiltrage());
        btnFiltrer.setOnAction(e -> appliquerFiltrage());

        comboBoxStatut.getItems().setAll("Tous", "À venir", "Passés");
        comboBoxStatut.getSelectionModel().select("Tous");
        comboBoxStatut.setOnAction(e -> appliquerFiltrage());

        btnStats.setOnAction(e -> ouvrirStatistiques());
    }

    private void initPagination(List<Evenement> list) {
        int pages = (int) Math.ceil((double) list.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pages == 0 ? 1 : pages);
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        int from = pageIndex * ITEMS_PER_PAGE;
        int to = Math.min(from + ITEMS_PER_PAGE, evenementsOriginaux.size());

        FlowPane pane = new FlowPane(20, 20);
        pane.setPrefWrapLength(760);
        pane.setPadding(new Insets(10));
        evenementsOriginaux.subList(from, to)
                .forEach(ev -> pane.getChildren().add(createEventCard(ev)));

        ScrollPane sp = new ScrollPane(pane);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        return sp;
    }

    private VBox createEventCard(Evenement ev) {
        VBox card = new VBox(12);
        card.getStyleClass().add("event-card");
        card.setPrefWidth(260);

        Label nom = new Label(ev.getNom());
        nom.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        Label lieu = new Label(ev.getLieu());
        lieu.setWrapText(true);
        Label dates = new Label(String.format("Du %s au %s",
                ev.getDateDebut().format(DATE_FORMATTER),
                ev.getDateFin().format(DATE_FORMATTER)));
        dates.setStyle("-fx-font-style:italic;");
        Label prix = new Label("Prix: " + ev.getPrix() + " DT");
        Label places = new Label("Places: " + ev.getPlacesDisponibles());

        Button btn = new Button("Participer");
        btn.getStyleClass().add("primary-button");
        btn.setOnAction(e -> gererParticipation(ev));

        // Add click event to fetch weather data when card is clicked
        card.setOnMouseClicked(e -> fetchWeatherData(ev.getLieu()));

        card.getChildren().addAll(nom, lieu, dates, prix, places, btn);
        return card;
    }

    private void gererParticipation(Evenement ev) {
        Participation p = new Participation();
        p.setDateParticipation(LocalDateTime.now());
        p.setNombrePersonnes(1);
        p.setUserId(1);
        p.setEvenementId(ev.getId());

        boolean ok = participantService.addParticipant(p);
        Alert a = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(ok
                ? "Participation enregistrée et PDF généré !"
                : "Erreur de participation.");
        if (ok) PDFGenerator.generateConfirmationPDF(ev, p);
        a.showAndWait();
    }

    private void appliquerFiltrage() {
        String txt = txtRecherche.getText().trim().toLowerCase();
        LocalDate d1 = dateDebutFiltre.getValue();
        LocalDate d2 = dateFinFiltre.getValue();
        String stat = comboBoxStatut.getValue();

        evenementsOriginaux = evenementService.getAllEvenements().stream()
                .filter(ev -> txt.isEmpty()
                        || ev.getNom().toLowerCase().contains(txt)
                        || ev.getLieu().toLowerCase().contains(txt))
                .filter(ev -> d1 == null || !ev.getDateDebut().isBefore(d1))
                .filter(ev -> d2 == null || !ev.getDateFin().isAfter(d2))
                .filter(ev -> {
                    if ("À venir".equals(stat)) return ev.getDateDebut().isAfter(LocalDate.now());
                    if ("Passés".equals(stat)) return ev.getDateFin().isBefore(LocalDate.now());
                    return true;
                })
                .collect(Collectors.toList());

        initPagination(evenementsOriginaux);
    }

    private void ouvrirStatistiques() {
        List<Evenement> all = evenementService.getAllEvenements();
        int total = all.size();
        Map<String, Long> counts = all.stream()
                .collect(Collectors.groupingBy(Evenement::getLieu, Collectors.counting()));

        PieChart chart = new PieChart();
        counts.forEach((lieu, nb) -> {
            double percent = nb * 100.0 / total;
            String label = String.format("%s (%.1f%%)", lieu, percent);
            chart.getData().add(new PieChart.Data(label, nb));
        });
        chart.setLegendVisible(true);
        chart.setLabelsVisible(true);

        Stage stage = new Stage();
        stage.setTitle("Stats : événements par lieu");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(chart, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void ouvrirCalendrier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/calendar.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Calendrier des Événements");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir le calendrier");
            alert.setContentText("Une erreur s'est produite lors du chargement de la vue calendrier.");
            alert.showAndWait();
        }
    }

    private void fetchWeatherData(String location) {
        try {
            // Encode the location to handle special characters and spaces
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = String.format(WEATHER_API_URL, encodedLocation, WEATHER_API_KEY);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            // Check for API error response
            if (json.has("cod") && json.getInt("cod") != 200) {
                String message = json.optString("message", "Unknown error");
                updateWeatherUIError("Erreur: " + message);
                return;
            }

            // Extract weather data
            String city = json.optString("name", location);
            JSONObject main = json.getJSONObject("main");
            double temp = main.getDouble("temp");
            String desc = "";
            String iconCode = "";

            if (json.has("weather") && json.getJSONArray("weather").length() > 0) {
                JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
                desc = weather.optString("description", "No description");
                iconCode = weather.optString("icon", "");
            }

            // Update UI
            weatherLocation.setText("Météo: " + city);
            weatherTemp.setText(String.format("%.1f°C", temp));
            weatherDesc.setText(desc.isEmpty() ? "No description" : desc);

            if (!iconCode.isEmpty()) {
                String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                weatherIcon.setImage(new Image(iconUrl));
            } else {
                weatherIcon.setImage(null);
            }

        } catch (Exception e) {
            updateWeatherUIError("Impossible de récupérer la météo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateWeatherUIError(String message) {
        weatherLocation.setText("Météo: Erreur");
        weatherTemp.setText("");
        weatherDesc.setText(message);
        weatherIcon.setImage(null);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventsPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Events");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de revenir");
            alert.setContentText("Une erreur s'est produite lors du retour à la page précédente.");
            alert.showAndWait();
        }
    }
} 