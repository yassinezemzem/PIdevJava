package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class FrontPageController {

    @FXML
    private Button accueilButton;

    @FXML
    private Button centreDeDonButton;

    @FXML
    private Button therapieButton;

    @FXML
    private StackPane contentArea;

    @FXML
    void initialize() {
    }

    @FXML
    void gotoAccueil(ActionEvent event) {
        loadUI("/afficher-therapie-user.fxml");
    }

    @FXML
    void gotoCentreDeDon(ActionEvent event) {
        loadUI("/centre-de-don.fxml");
    }

    @FXML
    void gotoTherapie(ActionEvent event) {
        loadUI("/afficher-therapie-user.fxml");
    }

    private void loadUI(String uiPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(uiPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            System.err.println("Error loading UI " + uiPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
