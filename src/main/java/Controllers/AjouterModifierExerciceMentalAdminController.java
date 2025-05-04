package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Entities.ExerciceMental;
import Services.ServiceExerciceMental;
import Services.ServiceTherapie;

public class AjouterModifierExerciceMentalAdminController {

    @FXML
    private Button btncancel;

    @FXML
    private Label ltitle;

    @FXML
    private TextField tfvideo;

    @FXML
    private TextField tftitre;

    @FXML
    private TextArea tadesc;

    @FXML
    private TextField tfduree;

    @FXML
    private TextArea tfobjectif;
    
    @FXML
    private ComboBox<String> cbTherapie;

    ServiceExerciceMental serviceExercice = new ServiceExerciceMental();
    ServiceTherapie serviceTherapie = new ServiceTherapie();
    private ExerciceMental currentExercice = null;

    public void setExercice(ExerciceMental exercice) {
        this.currentExercice = exercice;
        populateFields();
        ltitle.setText("Update Exercice Mental");
        btncancel.setVisible(true);
    }

    private void populateFields() {
        if (currentExercice != null) {
            tfvideo.setText(currentExercice.getVideoUrl());
            tftitre.setText(currentExercice.getTitre());
            tadesc.setText(currentExercice.getDescription());
            tfduree.setText(String.valueOf(currentExercice.getDureeMinutes()));
            tfobjectif.setText(currentExercice.getObjectif());
            if (cbTherapie.getItems().isEmpty()) {
                cbTherapie.getItems().addAll(serviceTherapie.getTitlesTherapie());
            }
            String therapyName = serviceTherapie.getNomById(currentExercice.getTherapieId());
            cbTherapie.setValue(therapyName);
        }
    }

    public void initialize() {
        cbTherapie.getItems().clear();
        cbTherapie.getItems().addAll(serviceTherapie.getTitlesTherapie());
        if (currentExercice == null) {
            ltitle.setText("Add Exercice Mental");
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
        String videoUrl = tfvideo.getText().trim();
        String titre = tftitre.getText().trim();
        String description = tadesc.getText().trim();
        String dureeStr = tfduree.getText().trim();
        String objectif = tfobjectif.getText().trim();
        String selectedTherapie = cbTherapie.getValue();
        int dureeMinutes;
        try {
            dureeMinutes = Integer.parseInt(dureeStr);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Typing error !! ");
            alert.setHeaderText("duration invalid , cant be , change it to appropriate form");
            alert.setContentText("change it to approriate form , use numbers fam ");
            alert.showAndWait();
            return;
        }
        int therapieId = serviceTherapie.getIdByNom(selectedTherapie);
        if (currentExercice == null) {
            ExerciceMental newExercice = new ExerciceMental();
            newExercice.setTherapieId(therapieId);
            newExercice.setVideoUrl(videoUrl);
            newExercice.setTitre(titre);
            newExercice.setDescription(description);
            newExercice.setDureeMinutes(dureeMinutes);
            newExercice.setObjectif(objectif);

            if (serviceExercice.afficher().stream().anyMatch(e -> e.equals(newExercice))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("unicity FAILED ! ");
                alert.setHeaderText("The exercise already exists");
                alert.setContentText("verify existance please ");
                alert.showAndWait();
                return;
            }
            serviceExercice.ajouter(newExercice);
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("ASuccessfully added !! yey ");
            successAlert.setHeaderText("Exercice mental added");
            successAlert.setContentText("Exercise added successfully.");
            successAlert.showAndWait();
        } else {
            currentExercice.setTherapieId(therapieId);
            currentExercice.setVideoUrl(videoUrl);
            currentExercice.setTitre(titre);
            currentExercice.setDescription(description);
            currentExercice.setDureeMinutes(dureeMinutes);
            currentExercice.setObjectif(objectif);
            serviceExercice.modifier(currentExercice);
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Updated successflly");
            successAlert.setHeaderText("Exercise Updated thanks");
            successAlert.setContentText("Exercise is upodated successfully hang on !");
            successAlert.showAndWait();
        }
    }

    private boolean controleDeSaisie() {
        String errors = "";
        if (tfvideo.getText() == null || tfvideo.getText().trim().isEmpty()) {
            errors += "Video URL empty\n";
        }
        if (tftitre.getText() == null || tftitre.getText().trim().isEmpty()) {
            errors += "Titre vempty\n";
        } else if (tftitre.getText().trim().length() < 3) {
            errors += "Title requires 3 caracters fam\n";
        }
        if (tadesc.getText() == null || tadesc.getText().trim().isEmpty()) {
            errors += "Description empty\n";
        } else if (tadesc.getText().trim().length() < 10) {
            errors += "Description drequires 10 caracters\n";
        }
        if (tfduree.getText() == null || tfduree.getText().trim().isEmpty()) {
            errors += "Durée empty\n";
        }
        if (tfobjectif.getText() == null || tfobjectif.getText().trim().isEmpty()) {
            errors += "Objectif empty\n";
        } else if (tfobjectif.getText().trim().length() < 5) {
            errors += "objectif requires at least ( caracters\n";
        }
        if (cbTherapie.getValue() == null || cbTherapie.getValue().trim().isEmpty()) {
            errors += "Thérapie not selected\n";
        }
        if (!errors.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("typing error");
            alert.setHeaderText("Verify all these errors and change input !! ");
            alert.setContentText(errors);
            alert.showAndWait();
            return false;
        }
        return true;
    }
} 