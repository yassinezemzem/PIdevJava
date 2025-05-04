package org.example.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {


            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Better Health - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(500);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }


    }

