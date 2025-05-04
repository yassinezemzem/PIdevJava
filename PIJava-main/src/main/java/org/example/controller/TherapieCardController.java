package org.example.controller;
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
import org.example.entities.Therapie;
import org.example.utils.QrCodeGenerator;

import java.io.File;

public class TherapieCardController {
    @FXML
    private ImageView img;

    @FXML
    private Label lnom;

    @FXML
    private Label ldesc;

    @FXML
    private Label lobj;

    @FXML
    private Label lduree;

    private Therapie therapie;
    @FXML
    private Label ltype;
    @FXML private ImageView qrCode;
    public void updateTexts(String nom, String desc, String obj, String duree, String type) {
        lnom.setText(nom);
        ldesc.setText(desc);
        lobj.setText(obj);
        lduree.setText(duree);
        ltype.setText(type);
    }
    public void fillCard(Therapie therapie){
        this.therapie=therapie;
        lnom.setText(therapie.getNom());
        ldesc.setText(therapie.getDescription());
        ltype.setText(therapie.getType());
        lobj.setText(therapie.getObjectif());
        lduree.setText(therapie.getDuree());
        try{
            Image image=new Image("file:"+therapie.getImage(),true);
            img.setImage(image);
        }catch (Exception e){

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
            qrCode.setImage(qrImage);
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
//////////////////////////////////////
            StackPane contentArea=(StackPane) ((Node)event.getSource()).getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
