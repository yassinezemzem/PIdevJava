
package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.example.entities.ExerciceMental;

import java.awt.Desktop;
import java.net.URI;

public class ExerciceCardController {

    @FXML private Label ltitre;
    @FXML private Label ldescription;
    @FXML private Label lduree;
    @FXML private Label lobjectif;
    @FXML private Button btnViewVideo;
    private String videoUrl;

    public void fillCard(ExerciceMental ex) {
        ltitre.setText(ex.getTitre());
        ldescription.setText(ex.getDescription());
        lduree.setText("Durée: " + ex.getDureeMinutes() + " min");
        lobjectif.setText(ex.getObjectif());
        videoUrl = ex.getVideoUrl();
    }

    public void updateTexts(String titre, String description, String objectif) {
        ltitre.setText(titre);
        ldescription.setText(description);
        lobjectif.setText(objectif);
    }

    @FXML
    void handleViewVideo(ActionEvent event) {
        if (videoUrl != null && !videoUrl.isEmpty()) {
            try {
                Desktop.getDesktop().browse(new URI(videoUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}