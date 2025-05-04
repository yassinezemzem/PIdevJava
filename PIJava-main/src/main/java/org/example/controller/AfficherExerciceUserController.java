package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.example.entities.ExerciceMental;
import org.example.entities.Therapie;
import org.example.service.ServiceExerciceMental;

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

    private Therapie therapie;
    private List<ExerciceMental> exercices;
    private List<ExerciceCardController> cardControllers = new ArrayList<>();
    private ServiceExerciceMental serviceEx = new ServiceExerciceMental();

    @FXML
    public void initialize() {
        languageCombo.getItems().addAll("Default", "en", "fr", "es", "de", "ar", "zh");
        languageCombo.setValue("Default");
        languageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Default".equals(newVal)) {
                for (int i = 0; i < exercices.size(); i++) {
                    cardControllers.get(i).fillCard(exercices.get(i));
                }
            } else {
                translateAll(newVal);
            }
        });
    }

    public void setTherapie(Therapie therapie) {
        this.therapie = therapie;
        therapyTitle.setText("Exercices Mentaux pour " + therapie.getNom());
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
}