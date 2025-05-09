package Controllers;

import javafx.beans.Observable;
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
import Entities.Therapie;
import Services.ServiceTherapie;

import java.io.IOException;
import java.util.Optional;

public class AfficherTherapieAdminController {

    @FXML
    private ListView<Therapie> lvtherapie;
    ObservableList<Therapie> data= FXCollections.observableArrayList();
    ServiceTherapie serviceTherapie=new ServiceTherapie();
    
    void refresh(){
        data.clear();
        data.addAll(serviceTherapie.afficher());
        lvtherapie.setItems(data);
    }
    
    public void initialize(){
        refresh();
    }

    @FXML
    void modifier(ActionEvent event) {
        Therapie selected = lvtherapie.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-therapie-admin.fxml"));
                Scene scene = new Scene(loader.load());
                AjouterModifierTherapieAdminController controller = loader.getController();
                controller.setTherapie(selected);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Update Therapie");
                stage.setOnHidden(e->refresh());
                stage.show();
            }catch (IOException e){
                System.out.printf(e.getMessage());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun élément sélectionné");
            alert.setHeaderText("Aucune thérapie sélectionnée");
            alert.setContentText("Veuillez sélectionner une thérapie à modifier.");
            alert.showAndWait();
        }
    }

    @FXML
    void supprimer(ActionEvent event) {
        Therapie t = lvtherapie.getSelectionModel().getSelectedItem();
        if (t != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText("Supprimer la thérapie");
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette thérapie ?");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                serviceTherapie.supprimer(t.getId());
                refresh();

                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Suppression réussie");
                infoAlert.setHeaderText("Thérapie supprimée");
                infoAlert.setContentText("La thérapie a été supprimée avec succès.");
                infoAlert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun élément sélectionné");
            alert.setHeaderText("Aucune thérapie sélectionnée");
            alert.setContentText("Veuillez sélectionner une thérapie à supprimer.");
            alert.showAndWait();
        }
    }
    
    @FXML
    void stat(ActionEvent event) {
        FXMLLoader fxml = new FXMLLoader(
                getClass().getResource("/fxml/therapie-stat-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxml.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        stage.setTitle("Statistiques Thérapies");
        stage.setScene(scene);
        stage.show();
    }
} 