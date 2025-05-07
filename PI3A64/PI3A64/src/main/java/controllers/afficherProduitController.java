package controllers;

import entities.Produit;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import services.ProduitService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class afficherProduitController {

    @FXML
    private FlowPane productContainer;

    @FXML
    private Button btnAjouter;

    private ProduitService produitService = new ProduitService();

    @FXML
    void initialize() {
        try {
            System.out.println("Initializing product display view...");
            loadProducts();
        } catch (SQLException e) {
            System.out.println("ERROR loading products: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les produits: " + e.getMessage());
        }
    }

    private void loadProducts() throws SQLException {
        System.out.println("Loading products from database...");
        ObservableList<Produit> observableList = FXCollections.observableArrayList(produitService.recuperer());
        System.out.println("Loaded " + observableList.size() + " products");

        productContainer.getChildren().clear();

        for (Produit produit : observableList) {
            System.out.println("Creating card for product: " + produit.getNom());
            VBox productCard = createProductCard(produit);
            productContainer.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Produit produit) {
        // Main card container
        VBox card = new VBox(10); // 10px spacing between elements
        card.setPadding(new Insets(15));
        card.setPrefWidth(680);  // Much wider card to fill most of the page width
        card.setMinHeight(150);  // Adjusted for image
        card.setMaxHeight(200);
        card.setStyle("-fx-background-color: white;" +
                      "-fx-background-radius: 8;" +
                      "-fx-border-radius: 8;" +
                      "-fx-border-color: #E0E0E0;" +
                      "-fx-border-width: 1;");

        // Apply shadow effect
        card.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.rgb(0, 0, 0, 0.2)));

        // Create a horizontal layout container
        HBox contentBox = new HBox(20); // Reduced spacing

        // Create image view (if available)
        ImageView productImage = new ImageView();
        productImage.setFitHeight(100);
        productImage.setFitWidth(100);
        productImage.setPreserveRatio(true);

        if (produit.getImageUrl() != null && !produit.getImageUrl().isEmpty()) {
            try {
                File imageFile = new File("src/main/resources/" + produit.getImageUrl());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    productImage.setImage(image);
                } else {
                    // Default image if file doesn't exist
                    productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Default image on error
                try {
                    productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
                } catch (Exception ex) {
                    // Silently ignore if default image is also not available
                }
            }
        } else {
            // Default image if no image URL
            try {
                productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
            } catch (Exception e) {
                // Silently ignore if default image is also not available
            }
        }

        // Create left side for product info
        VBox infoBox = new VBox(8); // Increased spacing
        infoBox.setPrefWidth(350); // Reduced width to accommodate image

        // Product name
        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2C3E50;"); // Larger font
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(350);

        // Description
        Label descLabel = new Label(produit.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(350);
        descLabel.setMaxHeight(60);

        infoBox.getChildren().addAll(nameLabel, descLabel);

        // Create right side for metadata and actions
        VBox metaBox = new VBox(8); // Increased spacing
        metaBox.setAlignment(Pos.TOP_RIGHT);
        metaBox.setPrefWidth(150); // Wider metadata section

        // Quantity
        Label quantityLabel = new Label("Quantité: " + produit.getQuantite());
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");

        // Add category information if available
        Label categoryLabel = null;
        if (produit.getCategorie() != null) {
            categoryLabel = new Label("Catégorie: " + produit.getCategorie().getNom());
            categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
            categoryLabel.setWrapText(true);
            categoryLabel.setMaxWidth(150);
        }

        metaBox.getChildren().add(quantityLabel);
        if (categoryLabel != null) {
            metaBox.getChildren().add(categoryLabel);
        }

        // Add button container at the bottom of metaBox
        HBox buttonsBox = new HBox(10); // Increased spacing
        buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);

        // Add buttons with improved styling
        Button editBtn = new Button("Modifier");
        editBtn.setPrefWidth(90); // Wider buttons
        editBtn.setPrefHeight(30); // Taller buttons
        editBtn.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-background-radius: 4;");
        editBtn.setOnAction(e -> navigateToModifierProduit());

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setPrefWidth(90); // Wider buttons
        deleteBtn.setPrefHeight(30); // Taller buttons
        deleteBtn.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-background-radius: 4;");
        deleteBtn.setOnAction(e -> confirmDeleteProduct(produit));

        // Add flexible space to push buttons to the bottom
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        spacer.setPrefHeight(10);
        spacer.setStyle("-fx-use-pref-size: true;");
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        buttonsBox.getChildren().addAll(editBtn, deleteBtn);
        metaBox.getChildren().addAll(spacer, buttonsBox);

        // Add image and content to the horizontal layout
        contentBox.getChildren().addAll(productImage, infoBox, metaBox);

        // Add the horizontal layout to the card
        card.getChildren().add(contentBox);

        return card;
    }

    private void confirmDeleteProduct(Produit produit) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer le produit");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer " + produit.getNom() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                produitService.supprimer(produit);
                loadProducts(); // Refresh the product list
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le produit: " + e.getMessage());
            }
        }
    }

    @FXML
    void ajouterProduit() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterProduit.fxml"));
            btnAjouter.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de produit.");
        }
    }

    @FXML
    void retourAjouter(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterProduit.fxml"));
            productContainer.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de produit.");
        }
    }

    @FXML
    void naviguerAjouterCategorie(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterCategorie.fxml"));
            productContainer.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de catégorie.");
        }
    }

    @FXML
    void naviguerAfficherCategorie(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherCategorie.fxml"));
            productContainer.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des catégories.");
        }
    }

    private void navigateToModifierProduit() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/modifierProduit.fxml"));
            btnAjouter.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page de modification de produit.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



