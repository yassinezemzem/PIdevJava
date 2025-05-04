package org.example.controller;
import org.example.entities.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.entities.Therapie;
import org.example.service.ServiceTherapie;
import org.example.entities.User;
import java.io.File;

public class AjouterModifierTherapieAdminController {

    @FXML
    private Button btncancel;

    @FXML
    private Label ltitle;

    @FXML
    private TextArea tadesc;

    @FXML
    private TextArea taobjectif;

    @FXML
    private TextField tfduration;

    @FXML
    private TextField tfimage;

    @FXML
    private TextField tfname;

    @FXML
    private TextField tftype;
    ServiceTherapie serviceTherapie=new ServiceTherapie();
    private Therapie currentTherapie=null;
    public void setTherapie(Therapie therapie) {
        this.currentTherapie=therapie;
        populateFields();
        ltitle.setText("Update Therapie");
        btncancel.setVisible(true);
    }
    private void populateFields() {
        if (currentTherapie != null) {
            tfname.setText(currentTherapie.getNom());
            tfimage.setText(currentTherapie.getImage());
            tadesc.setText(currentTherapie.getDescription());
            taobjectif.setText(currentTherapie.getObjectif());
            tfduration.setText(currentTherapie.getDuree());
            tftype.setText(currentTherapie.getType());
        }
    }
    public void initialize() {
        tfimage.setDisable(true);
        if (currentTherapie == null) {
            ltitle.setText("Add Therapie");
            btncancel.setVisible(false);
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) btncancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void submit(ActionEvent event) {
        if (!controleDeSaisie()) {
            return;
        }

        String nom = tfname.getText();
        String image = tfimage.getText();
        String description = tadesc.getText();
        String objectif = taobjectif.getText();
        String duree = tfduration.getText();
        String type = tftype.getText();
        if (currentTherapie == null) {
            Therapie newTherapie = new Therapie();
            newTherapie.setNom(nom);
            newTherapie.setImage(image);
            newTherapie.setDescription(description);
            newTherapie.setObjectif(objectif);
            newTherapie.setDuree(duree);
            newTherapie.setType(type);
            if (serviceTherapie.afficher().stream().anyMatch(t -> t.equals(newTherapie))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Not only one exists, verify error! ");
                alert.setHeaderText("Therapie already exists");
                alert.setContentText("Verify information because another identically Therapy exists");
                alert.showAndWait();
                return;
            }
            serviceTherapie.ajouter(newTherapie);
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Added successfully");
            successAlert.setHeaderText("Thérapie added");
            successAlert.setContentText("Thérapie added successfully !.");
            successAlert.showAndWait();
        }else{
            currentTherapie.setNom(nom);
            currentTherapie.setImage(image);
            currentTherapie.setDescription(description);
            currentTherapie.setObjectif(objectif);
            currentTherapie.setDuree(duree);
            currentTherapie.setType(type);
            serviceTherapie.modifier(currentTherapie);
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Updated successfully");
            successAlert.setHeaderText("Therapy updated");
            successAlert.setContentText("Therapie updated successfully");
            successAlert.showAndWait();
        }
    }

    @FXML
    void upload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Therapie Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            tfimage.setText(file.getAbsolutePath());
        }
    }
    private boolean controleDeSaisie(){
        String errors="";
        if (tfname.getText() == null || tfname.getText().trim().isEmpty()) {
            errors += "Nom empty\n";
        } else if (tfname.getText().trim().length() < 3) {
            errors += "Nom requires 3 caracters \n";
        }
        if (tfimage.getText() == null || tfimage.getText().trim().isEmpty()) {
            errors += "Image empty\n";
        }
        if (tadesc.getText() == null || tadesc.getText().trim().isEmpty()) {
            errors += "Description empty\n";
        } else if (tadesc.getText().trim().length() < 10) {
            errors += "Description requires 10 caracters\n";
        }
        if (taobjectif.getText() == null || taobjectif.getText().trim().isEmpty()) {
            errors += "Objectif vide\n";
        } else if (taobjectif.getText().trim().length() < 5) {
            errors += "Objectif requires 5 caracters\n";
        }
        if (tfduration.getText() == null || tfduration.getText().trim().isEmpty()) {
            errors += "Durée vempty\n";
        }
        if (tftype.getText() == null || tftype.getText().trim().isEmpty()) {
            errors += "Type empty\n";
        }
        if (!errors.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Typing error , BE CAREFULL FAM !! ");
            alert.setHeaderText("Please correct these errors to obtain your Therapy!! ");
            alert.setContentText(errors);
            alert.showAndWait();
            return false;
        }
        return true;

    }

}
