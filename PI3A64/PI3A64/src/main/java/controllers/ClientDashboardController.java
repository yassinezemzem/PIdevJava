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
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
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

public class ClientDashboardController {

    @FXML
    private Button btnBrowseProducts;
    
    @FXML
    private Button btnBackToHome;

    @FXML
    private FlowPane productContainer;
    
    private ProduitService produitService;
    private EmailService emailService;

    @FXML
    void initialize() {
        try {
            produitService = new ProduitService();
            emailService = new EmailService();
            
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les produits: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur système", 
                    "Une erreur système s'est produite: " + e.getMessage() + 
                    "\nVérifiez la connexion à la base de données.");
        }
    }
    
    @FXML
    void browseProducts(ActionEvent event) {
        try {
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les produits: " + e.getMessage());
        }
    }
    
    @FXML
    void backToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/selectionPage.fxml"));
            productContainer.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du retour à la page d'accueil.");
        }
    }
    
    private void loadProducts() throws SQLException {
        ObservableList<Produit> observableList = FXCollections.observableArrayList(produitService.recuperer());
        
        productContainer.getChildren().clear();
        
        for (Produit produit : observableList) {
            VBox productCard = createProductCard(produit);
            productContainer.getChildren().add(productCard);
        }
    }
    
    private VBox createProductCard(Produit produit) {
        // Main card container
        VBox card = new VBox(10); // 10px spacing between elements
        card.setPadding(new Insets(15));
        card.setPrefWidth(320);  // Adjusted width for the card
        card.setMinHeight(300);  // Increased height to accommodate image
        card.setMaxHeight(350);
        card.setStyle("-fx-background-color: white;" +
                      "-fx-background-radius: 8;" + 
                      "-fx-border-radius: 8;" +
                      "-fx-border-color: #E0E0E0;" +
                      "-fx-border-width: 1;");
        
        // Apply shadow effect
        card.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.rgb(0, 0, 0, 0.2)));
        
        // Create product image view
        ImageView productImage = new ImageView();
        productImage.setFitHeight(150);
        productImage.setFitWidth(280);
        productImage.setPreserveRatio(true);
        
        // Load image based on product's imageUrl
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
        
        // Center the image
        VBox imageContainer = new VBox(productImage);
        imageContainer.setAlignment(Pos.CENTER);
        
        // Create product info section
        VBox infoBox = new VBox(8); // Increased spacing
        
        // Product name
        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2C3E50;"); // Larger font
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(280);
        
        // Description
        Label descLabel = new Label(produit.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(280);
        descLabel.setMaxHeight(60);
        
        // Metadata section
        HBox metaBox = new HBox(15);
        metaBox.setAlignment(Pos.CENTER_LEFT);
        
        // Quantity
        Label quantityLabel = new Label("Quantité: " + produit.getQuantite());
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        
        // Category
        Label categoryLabel = null;
        if (produit.getCategorie() != null) {
            categoryLabel = new Label("Catégorie: " + produit.getCategorie().getNom());
            categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
            categoryLabel.setWrapText(true);
        }
        
        VBox metaDataBox = new VBox(5);
        metaDataBox.getChildren().add(quantityLabel);
        if (categoryLabel != null) {
            metaDataBox.getChildren().add(categoryLabel);
        }
        
        metaBox.getChildren().add(metaDataBox);
        
        // Purchase button
        Button purchaseBtn = new Button("Acheter");
        purchaseBtn.setPrefWidth(280);
        purchaseBtn.setPrefHeight(35);
        purchaseBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 4;");
        purchaseBtn.setOnAction(e -> handlePurchase(produit));
        
        // Add everything to info box
        infoBox.getChildren().addAll(nameLabel, descLabel, metaBox, purchaseBtn);
        
        // Add image and info to card
        card.getChildren().addAll(imageContainer, infoBox);
        
        return card;
    }
    
    private void handlePurchase(Produit produit) {
        if (produit.getQuantite() <= 0) {
            showAlert(Alert.AlertType.WARNING, "Stock épuisé", "Ce produit n'est plus disponible en stock.");
            return;
        }
        
        // Ask for email
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Information d'achat");
        emailDialog.setHeaderText("Veuillez entrer votre adresse email");
        emailDialog.setContentText("Email:");
        
        Optional<String> emailResult = emailDialog.showAndWait();
        
        if (emailResult.isPresent() && !emailResult.get().trim().isEmpty()) {
            String clientEmail = emailResult.get().trim();
            
            // Ask for quantity
            TextInputDialog quantityDialog = new TextInputDialog("1");
            quantityDialog.setTitle("Information d'achat");
            quantityDialog.setHeaderText("Combien d'unités souhaitez-vous acheter?");
            quantityDialog.setContentText("Quantité (max " + produit.getQuantite() + "):");
            
            Optional<String> quantityResult = quantityDialog.showAndWait();
            
            if (quantityResult.isPresent()) {
                try {
                    int quantity = Integer.parseInt(quantityResult.get().trim());
                    
                    if (quantity <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être supérieure à 0.");
                        return;
                    }
                    
                    if (quantity > produit.getQuantite()) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité demandée dépasse le stock disponible.");
                        return;
                    }
                    
                    // Process purchase
                    try {
                        // Update product quantity in database
                        produit.setQuantite(produit.getQuantite() - quantity);
                        produitService.modifier(produit);
                        
                        // Send email notification
                        emailService.sendPurchaseNotification(produit.getNom(), quantity, clientEmail);
                        
                        showAlert(Alert.AlertType.INFORMATION, "Achat effectué", 
                                "Votre achat de " + quantity + " unité(s) de " + produit.getNom() + " a été effectué avec succès.");
                        
                        // Refresh product display
                        loadProducts();
                        
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'achat: " + e.getMessage());
                    }
                    
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre entier valide pour la quantité.");
                }
            }
        }
    }
    
    @FXML
    void navigateToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminLogin.fxml"));
            productContainer.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page de connexion.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 