package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private Button btnAddCategory;

    @FXML
    private Button btnDisplayCategories;

    @FXML
    private Button btnAddProduct;

    @FXML
    private Button btnDisplayProducts;
    
    @FXML
    private Button btnStatistics;

    @FXML
    private Button btnBackToHome;

    @FXML
    private StackPane contentArea;

    @FXML
    private void initialize() {
        // Configuration des événements pour les boutons
        btnAddCategory.setOnAction(this::navigateToAddCategory);
        btnDisplayCategories.setOnAction(this::navigateToDisplayCategories);
        btnAddProduct.setOnAction(this::navigateToAddProduct);
        btnDisplayProducts.setOnAction(this::navigateToDisplayProducts);
        btnStatistics.setOnAction(this::navigateToStatistics);
        btnBackToHome.setOnAction(this::backToHome);
        
        // Charger par défaut la gestion des produits
        // Ne pas charger navigateToDisplayProducts() ici pour éviter une redirection immédiate
    }

    @FXML
    void navigateToAddCategory(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterCategorie.fxml"));
            contentArea.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de catégorie.");
        }
    }

    @FXML
    void navigateToDisplayCategories(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherCategorie.fxml"));
            contentArea.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des catégories.");
        }
    }

    @FXML
    void navigateToAddProduct(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterProduit.fxml"));
            contentArea.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de produit.");
        }
    }

    @FXML
    void navigateToDisplayProducts(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherProduit.fxml"));
            contentArea.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des produits.");
        }
    }
    
    @FXML
    void navigateToStatistics(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminStatistics.fxml"));
            contentArea.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page des statistiques.");
        }
    }

    @FXML
    void backToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/selectionPage.fxml"));
            contentArea.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du retour à la page d'accueil.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 