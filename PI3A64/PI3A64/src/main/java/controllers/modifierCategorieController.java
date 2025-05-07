package controllers;

import entities.Categorie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import services.CategorieService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class modifierCategorieController {

    @FXML
    private ComboBox<Categorie> cbCategories;

    @FXML
    private TextField tfnom;

    @FXML
    private TextField tfdescription;

    private CategorieService categorieService = new CategorieService();

    @FXML
    void initialize() {
        try {
            List<Categorie> categories = categorieService.recuperer();
            ObservableList<Categorie> observableList = FXCollections.observableArrayList(categories);
            cbCategories.setItems(observableList);
            
            // Listener pour remplir les champs lorsqu'une catégorie est sélectionnée
            cbCategories.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    tfnom.setText(newValue.getNom());
                    tfdescription.setText(newValue.getDescription());
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement des catégories.");
        }
    }

    @FXML
    void modifierCategorie(ActionEvent event) {
        Categorie selectedCategorie = cbCategories.getSelectionModel().getSelectedItem();
        
        // Validation de la catégorie sélectionnée
        if (selectedCategorie == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une catégorie à modifier.");
            cbCategories.requestFocus();
            return;
        }
        
        // Validation du nom (obligatoire et longueur)
        String nom = tfnom.getText().trim();
        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Le nom de la catégorie est obligatoire.");
            tfnom.requestFocus();
            return;
        }
        
        if (nom.length() < 2 || nom.length() > 50) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Le nom de la catégorie doit contenir entre 2 et 50 caractères.");
            tfnom.requestFocus();
            return;
        }
        
        // Validation de la description (obligatoire)
        String description = tfdescription.getText().trim();
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "La description de la catégorie est obligatoire.");
            tfdescription.requestFocus();
            return;
        }
        
        // Vérifier si le nom est déjà utilisé par une autre catégorie
        try {
            List<Categorie> categories = categorieService.recuperer();
            for (Categorie cat : categories) {
                if (cat.getId() != selectedCategorie.getId() && cat.getNom().equalsIgnoreCase(nom)) {
                    showAlert(Alert.AlertType.WARNING, "Attention", "Une catégorie avec ce nom existe déjà.");
                    tfnom.requestFocus();
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la vérification du nom de la catégorie.");
            return;
        }
        
        try {
            selectedCategorie.setNom(nom);
            selectedCategorie.setDescription(description);
            
            categorieService.modifier(selectedCategorie);
            showAlert(Alert.AlertType.INFORMATION, "Modification catégorie", "Catégorie modifiée avec succès");
            
            // Recharger la liste des catégories
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la modification de la catégorie.");
        }
    }

    @FXML
    void retourAffichage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherCategorie.fxml"));
            tfnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage.");
        }
    }

    @FXML
    void naviguerAjouterCategorie(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterCategorie.fxml"));
            tfnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de catégorie.");
        }
    }

    @FXML
    void naviguerAjouterProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterProduit.fxml"));
            tfnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de produit.");
        }
    }

    @FXML
    void naviguerAfficherProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherProduit.fxml"));
            tfnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des produits.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 