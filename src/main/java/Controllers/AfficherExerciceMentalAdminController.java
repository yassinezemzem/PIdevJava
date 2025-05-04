package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import Entities.ExerciceMental;
import Services.ServiceExerciceMental;

import java.io.IOException;
import java.util.Optional;

public class AfficherExerciceMentalAdminController {

    @FXML
    private ListView<ExerciceMental> lvExercice;

    ObservableList<ExerciceMental> data = FXCollections.observableArrayList();
    ServiceExerciceMental serviceExercice = new ServiceExerciceMental();

    void refresh() {
        data.clear();
        data.addAll(serviceExercice.afficher());
        System.out.println(serviceExercice.afficher());
        lvExercice.setItems(data);
    }

    public void initialize() {
        refresh();
    }

    @FXML
    void modifier(ActionEvent event) {
        ExerciceMental selected = lvExercice.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-exercicemntal-admin.fxml"));
                Scene scene = new Scene(loader.load());
                AjouterModifierExerciceMentalAdminController controller = loader.getController();
                controller.setExercice(selected);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Update Exercice Mental");
                stage.setOnHidden(e -> refresh());
                stage.show();
            } catch (IOException e) {
                System.out.println("loading error : " + e.getMessage());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("nothing selected fam");
            alert.setHeaderText("No exercise is selected");
            alert.setContentText("select an exercise to modify fam !! ");
            alert.showAndWait();
        }
    }

    @FXML
    void supprimer(ActionEvent event) {
        ExerciceMental exercice = lvExercice.getSelectionModel().getSelectedItem();
        if (exercice != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("supression confirmed");
            confirmAlert.setHeaderText("delete exercice mental");
            confirmAlert.setContentText("Are you sure u want to delete it forever :) ?");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                serviceExercice.supprimer(exercice.getId());
                refresh();

                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("delete done");
                infoAlert.setHeaderText("exercise deleted");
                infoAlert.setContentText("Your exercise is deleted successfully , HANG ON !!");
                infoAlert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No element selected ");
            alert.setHeaderText("No exercise is selected");
            alert.setContentText("Select your exercise to delete please ! :) ");
            alert.showAndWait();
        }
    }
} 