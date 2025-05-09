package Controllers;

import com.google.zxing.WriterException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import Entities.Therapie;
import utils.QrCodeGenerator;

import java.io.File;

public class TherapieCardController {
    @FXML
    private ImageView imageView;

    @FXML
    private Label ltitre;

    @FXML
    private Label ldescription;

    @FXML
    private Label lobjectif;

    @FXML
    private Label lduree;

    @FXML
    private Label ltype;

    private Therapie therapie;
    
    public void updateTexts(String nom, String desc, String obj, String duree, String type) {
        ltitre.setText(nom);
        ldescription.setText(desc);
        lobjectif.setText(obj);
        lduree.setText(duree);
        ltype.setText(type);
    }
    
    public void fillCard(Therapie therapie){
        this.therapie = therapie;
        ltitre.setText(therapie.getNom());
        ldescription.setText(therapie.getDescription());
        ltype.setText(therapie.getType());
        lobjectif.setText(therapie.getObjectif());
        lduree.setText(therapie.getDuree());
        try{
            Image image = new Image("file:" + therapie.getImage(), true);
            imageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Thérapie: ").append(therapie.getNom()).append("\n")
                .append("Description: ").append(therapie.getDescription()).append("\n")
                .append("Objectif: ").append(therapie.getObjectif()).append("\n")
                .append("Durée: ").append(therapie.getDuree()).append("\n")
                .append("Type: ").append(therapie.getType());
        try {
            File qrFile = QrCodeGenerator.generateQrCode(sb.toString(), therapie.getId());
            Image qrImage = new Image(qrFile.toURI().toString(), true);
            // qrCode.setImage(qrImage); // Commented out since qrCode ImageView is not in FXML
        } catch (WriterException | java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void gotoExercice(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-exercice-user.fxml"));
            Parent root = loader.load();
            AfficherExerciceUserController ctrl = loader.getController();
            ctrl.setTherapie(therapie);
            StackPane contentArea = (StackPane) ((Node)event.getSource()).getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 