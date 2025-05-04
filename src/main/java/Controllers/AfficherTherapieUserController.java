package Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import Entities.Therapie;
import Services.ServiceTherapie;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AfficherTherapieUserController {
    @FXML
    private GridPane grid;
    @FXML
    private ComboBox<String> languageCombo;

    private List<Therapie> therapies;
    private List<TherapieCardController> cardControllers = new ArrayList<>();
    private ServiceTherapie serviceTherapie = new ServiceTherapie();
    @FXML private ComboBox<String> cbtri;
    @FXML private TextField tfrecherche;
    private List<Node> therapyCards = new ArrayList<>();
    
    public void initialize() {
        therapies = serviceTherapie.afficher();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(10));
        loadCards();
        setupFilters();
    }
    
    private void loadCards() {
        int col = 0, row = 0;
        for (Therapie t : therapies) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/therapie-card.fxml"));
                Node card = loader.load();
                TherapieCardController ctrl = loader.getController();
                ctrl.fillCard(t);
                therapyCards.add(card);
                cardControllers.add(ctrl);
                grid.add(card, col, row);
                Insets margin = new Insets(10);
                GridPane.setMargin(card, margin);
                if (++col == 2) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void translateAll(String targetLang) {
        try {
            List<String> toTranslate = new ArrayList<>();
            for (Therapie t : therapies) {
                toTranslate.add(t.getNom());
                toTranslate.add(t.getDescription());
                toTranslate.add(t.getObjectif());
                toTranslate.add(t.getDuree());
                toTranslate.add(t.getType());
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = Map.of(
                    "texts", toTranslate,
                    "target_language", targetLang
            );
            String json = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/translate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Translate API error: " + response.body());
            }

            JsonNode arr = mapper.readTree(response.body()).get("translated_texts");
            for (int i = 0; i < therapies.size(); i++) {
                int base = i * 5;
                String nom   = arr.get(base    ).asText();
                String desc  = arr.get(base + 1).asText();
                String obj   = arr.get(base + 2).asText();
                String duree = arr.get(base + 3).asText();
                String type  = arr.get(base + 4).asText();
                cardControllers.get(i).updateTexts(nom, desc, obj, duree, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupFilters(){
        tfrecherche.textProperty().addListener((obs, o, n) -> updateGrid());
        cbtri.getItems().addAll(
                "Nom A→Z", "Nom Z→A",
                "Durée ↑", "Durée ↓",
                "Type A→Z", "Type Z→A"
        );
        cbtri.valueProperty().addListener((obs, o, n) -> updateGrid());
        languageCombo.getItems().addAll("Default", "en", "fr", "es", "de", "ar", "zh");
        languageCombo.setValue("Default");
        languageCombo.valueProperty().addListener((obs, oldLang, newLang) -> {
            if ("Default".equals(newLang)) {
                for (int i = 0; i < therapies.size(); i++) {
                    cardControllers.get(i).fillCard(therapies.get(i));
                }
            } else {
                translateAll(newLang);
            }
        });
    }
    
    private void updateGrid() {
        String filter = tfrecherche.getText().trim().toLowerCase();

        // ---- 1. filter ----
        List<Therapie> shown = therapies.stream()
                .filter(t -> filter.isEmpty()
                        || t.getNom().toLowerCase().contains(filter)
                        || t.getDescription().toLowerCase().contains(filter)
                        || t.getObjectif().toLowerCase().contains(filter)
                        || t.getDuree().toLowerCase().contains(filter)
                        || t.getType().toLowerCase().contains(filter))
                .collect(Collectors.toList());

        // ---- 2. sort ----
        switch (String.valueOf(cbtri.getValue())) {
            case "Nom A→Z"  -> shown.sort(Comparator.comparing(Therapie::getNom));
            case "Nom Z→A"  -> shown.sort(Comparator.comparing(Therapie::getNom).reversed());
            case "Durée ↑"   -> shown.sort(Comparator.comparing(Therapie::getDuree));
            case "Durée ↓"   -> shown.sort(Comparator.comparing(Therapie::getDuree).reversed());
            case "Type A→Z" -> shown.sort(Comparator.comparing(Therapie::getType));
            case "Type Z→A" -> shown.sort(Comparator.comparing(Therapie::getType).reversed());
        }

        // ---- 3. rebuild grid with brand-new cards ----
        grid.getChildren().clear();
        int col = 0, row = 0;

        for (Therapie t : shown) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/therapie-card.fxml"));
                Node card = loader.load();
                loader.<TherapieCardController>getController().fillCard(t);

                grid.add(card, col, row);
                GridPane.setMargin(card, new Insets(10));

                if (++col == 2) { col = 0; row++; }

            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    @FXML
    void onBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontPage.fxml"));
            Parent frontPage = loader.load();
            // Find the parent BorderPane and set the center to frontPage's center
            Node source = (Node) event.getSource();
            javafx.scene.Scene scene = source.getScene();
            javafx.scene.layout.BorderPane borderPane = (javafx.scene.layout.BorderPane) scene.getRoot();
            // Get the content area from the loaded front page
            StackPane newContent = (StackPane) frontPage.lookup("#contentArea");
            borderPane.setCenter(newContent);
        } catch (IOException e) {
            System.err.println("Error loading front page: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 