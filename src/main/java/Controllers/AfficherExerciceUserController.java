package Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import Entities.ExerciceMental;
import Entities.Therapie;
import Services.ServiceExerciceMental;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AfficherExerciceUserController {
    @FXML private Label therapyTitle;
    @FXML private GridPane gridEx;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button backButton;

    private Therapie therapie;
    private List<ExerciceMental> exercices;
    private List<ExerciceCardController> cardControllers = new ArrayList<>();
    private ServiceExerciceMental serviceEx = new ServiceExerciceMental();

    @FXML
    public void initialize() {
        languageCombo.getItems().addAll("English", "Français", "العربية");
        languageCombo.setValue("English");
        languageCombo.setOnAction(event -> updateLanguage());
    }

    public void setTherapie(Therapie therapie) {
        this.therapie = therapie;
        therapyTitle.setText("Mental Exercises");
        loadGrid();
    }

    private void loadGrid() {
        exercices = serviceEx.findByTherapieId(therapie.getId());
        gridEx.setHgap(20);
        gridEx.setVgap(20);
        gridEx.setPadding(new Insets(10));

        int col = 0, row = 0;
        for (ExerciceMental ex : exercices) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/exercice-card.fxml"));
                Node card = loader.load();
                ExerciceCardController ctrl = loader.getController();
                ctrl.fillCard(ex);
                cardControllers.add(ctrl);
                gridEx.add(card, col, row);
                GridPane.setMargin(card, new Insets(10));
                col++;
                if (col == 2) { col = 0; row++; }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void translateAll(String targetLang) {
        try {
            List<String> toTranslate = new ArrayList<>();
            for (ExerciceMental ex : exercices) {
                toTranslate.add(ex.getTitre());
                toTranslate.add(ex.getDescription());
                toTranslate.add(ex.getObjectif());
            }
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(Map.of(
                    "texts", toTranslate,
                    "target_language", targetLang
            ));
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/translate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> resp = HttpClient.newHttpClient()
                    .send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode arr = mapper.readTree(resp.body()).get("translated_texts");
            for (int i = 0; i < exercices.size(); i++) {
                int base = i * 3;
                String t = arr.get(base).asText();
                String d = arr.get(base+1).asText();
                String o = arr.get(base+2).asText();
                cardControllers.get(i).updateTexts(t, d, o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLanguage() {
        String selectedLanguage = languageCombo.getValue();
        switch (selectedLanguage) {
            case "English":
                therapyTitle.setText("Mental Exercises");
                break;
            case "Français":
                therapyTitle.setText("Exercices Mentaux");
                break;
            case "العربية":
                therapyTitle.setText("تمارين ذهنية");
                break;
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