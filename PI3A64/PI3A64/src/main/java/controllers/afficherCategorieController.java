package controllers;

import entities.Categorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import services.CategorieService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class afficherCategorieController {

    @FXML
    private FlowPane categoryContainer;
    
    @FXML
    private Button btnAjouter;

    private CategorieService categorieService = new CategorieService();

    @FXML
    void initialize() {
        try {
            System.out.println("Initializing category display view...");
            loadCategories();
        } catch (SQLException e) {
            System.out.println("ERROR loading categories: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories: " + e.getMessage());
        }
    }
    
    private void loadCategories() throws SQLException {
        System.out.println("Loading categories from database...");
        ObservableList<Categorie> observableList = FXCollections.observableArrayList(categorieService.recuperer());
        System.out.println("Loaded " + observableList.size() + " categories");
        
        categoryContainer.getChildren().clear();
        
        for (Categorie categorie : observableList) {
            System.out.println("Creating card for category: " + categorie.getNom());
            VBox categoryCard = createCategoryCard(categorie);
            categoryContainer.getChildren().add(categoryCard);
        }
    }
    
    private VBox createCategoryCard(Categorie categorie) {
        // Main card container
        VBox card = new VBox(10); // 10px spacing between elements
        card.setPadding(new Insets(15));
        card.setPrefWidth(680);  // Much wider card to fill most of the page width
        card.setMinHeight(130);  // Shorter height for horizontal layout
        card.setMaxHeight(150);
        card.setStyle("-fx-background-color: white;" +
                     "-fx-background-radius: 8;" + 
                     "-fx-border-radius: 8;" +
                     "-fx-border-color: #E0E0E0;" +
                     "-fx-border-width: 1;");
        
        // Apply shadow effect
        card.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.rgb(0, 0, 0, 0.2)));
        
        // Create a horizontal layout container
        javafx.scene.layout.HBox contentBox = new javafx.scene.layout.HBox(30); // Increased spacing between elements
        
        // Create left side for category info
        VBox infoBox = new VBox(8); // Increased spacing
        infoBox.setPrefWidth(450); // Wider info section
        
        // Category name
        Label nameLabel = new Label(categorie.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2C3E50;"); // Larger font
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(450);
        
        // Description
        Label descLabel = new Label(categorie.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(450);
        descLabel.setMaxHeight(60);
        
        infoBox.getChildren().addAll(nameLabel, descLabel);
        
        // Create right side for actions
        VBox actionBox = new VBox(8); // Increased spacing
        actionBox.setPrefWidth(150); // Wider action section
        actionBox.setAlignment(Pos.TOP_RIGHT);
        
        // Action buttons
        javafx.scene.layout.HBox buttonsBox = new javafx.scene.layout.HBox(10); // Increased spacing
        buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);
        
        Button editBtn = new Button("Modifier");
        editBtn.setPrefWidth(90); // Wider buttons
        editBtn.setPrefHeight(30); // Taller buttons
        editBtn.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-background-radius: 4;");
        editBtn.setOnAction(e -> navigateToModifierCategorie());
        
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setPrefWidth(90); // Wider buttons
        deleteBtn.setPrefHeight(30); // Taller buttons
        deleteBtn.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-background-radius: 4;");
        deleteBtn.setOnAction(e -> confirmDeleteCategory(categorie));
        
        // Add flexible space to push buttons to the bottom
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        spacer.setPrefHeight(10);
        javafx.scene.layout.VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        buttonsBox.getChildren().addAll(editBtn, deleteBtn);
        actionBox.getChildren().addAll(spacer, buttonsBox);
        
        // Add infoBox and actionBox to horizontal layout
        contentBox.getChildren().addAll(infoBox, actionBox);
        
        // Add horizontal layout to card
        card.getChildren().add(contentBox);
        
        return card;
    }
    
    private void navigateToModifierCategorie() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/modifierCategorie.fxml"));
            categoryContainer.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page de modification.");
        }
    }
    
    private void confirmDeleteCategory(Categorie categorie) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la catégorie");
        confirmation.setContentText("Voulez-vous vraiment supprimer la catégorie " + categorie.getNom() + " ?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categorieService.supprimer(categorie);
                showAlert(Alert.AlertType.INFORMATION, "Suppression catégorie", "Catégorie supprimée avec succès");
                loadCategories(); // Recharger les catégories
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la suppression de la catégorie.");
            }
        }
    }
    
    @FXML
    void retourAjouter(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterCategorie.fxml"));
            categoryContainer.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout.");
        }
    }
    
    @FXML
    void naviguerAjouterProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterProduit.fxml"));
            categoryContainer.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de produit.");
        }
    }
    
    @FXML
    void naviguerAfficherProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherProduit.fxml"));
            categoryContainer.getScene().setRoot(root);
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
