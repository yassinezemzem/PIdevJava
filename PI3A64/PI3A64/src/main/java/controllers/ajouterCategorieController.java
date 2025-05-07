package controllers;

import entities.Categorie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.CategorieService;

import java.io.IOException;
import java.sql.SQLException;

public class ajouterCategorieController {

    @FXML
    private TextField tfnom;  // Champ pour le nom de la catégorie

    @FXML
    private TextField tfdescription;  // Champ pour la description de la catégorie

    @FXML
    void AjouterCategorie(ActionEvent event) {
        CategorieService categorieService = new CategorieService();
        try {
            // Validation du nom de catégorie
            String nomCategorie = tfnom.getText().trim();
            String descriptionCategorie = tfdescription.getText().trim();
            
            if (nomCategorie.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setContentText("Le nom de la catégorie ne peut pas être vide.");
                alert.show();
                return;
            }
            
            if (nomCategorie.length() < 2 || nomCategorie.length() > 30) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setContentText("Le nom de la catégorie doit contenir entre 2 et 30 caractères.");
                alert.show();
                return;
            }
            
            if (descriptionCategorie.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setContentText("La description de la catégorie ne peut pas être vide.");
                alert.show();
                return;
            }
            
            // Vérifier que la description n'est pas uniquement des chiffres
            if (descriptionCategorie.matches("^[0-9]+$")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setContentText("La description ne peut pas contenir uniquement des chiffres.");
                alert.show();
                return;
            }
            
            // Vérifier si la catégorie existe déjà
            if (categorieService.categorieExiste(nomCategorie)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setContentText("Une catégorie avec ce nom existe déjà.");
                alert.show();
                return;
            }
            
            // Ajouter la catégorie
            categorieService.ajouter(new Categorie(0, nomCategorie, descriptionCategorie));
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout catégorie");
            alert.setContentText("Catégorie ajoutée avec succès");
            alert.show();
            
            // Réinitialiser le champ
            tfnom.clear();
            tfdescription.clear();
            
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur s'est produite lors de l'ajout de la catégorie.");
            alert.show();
        }
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            // Charger l'écran pour afficher les catégories
            Parent root = FXMLLoader.load(getClass().getResource("/afficherCategorie.fxml"));
            tfnom.getScene().setRoot(root);  // Redirige vers l'écran d'affichage des catégories
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur s'est produite lors de l'affichage des catégories.");
            alert.show();
        }
    }
    
    @FXML
    void naviguerAjouterProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterProduit.fxml"));
            tfnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page d'ajout de produit.");
            alert.show();
        }
    }
    
    @FXML
    void naviguerAfficherProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherProduit.fxml"));
            tfnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page d'affichage des produits.");
            alert.show();
        }
    }
}
