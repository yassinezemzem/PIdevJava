package Controllers;

import Entities.Produit;
import Services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientDashboardController implements Initializable {

    @FXML
    private FlowPane productContainer;

    private ProduitService produitService;
    private EmailService emailService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produitService = new ProduitService();
        emailService = new EmailService();
        loadProducts();
    }

    private void loadProducts() {
        ObservableList<Produit> observableList = FXCollections.observableArrayList(produitService.readAll());

        productContainer.getChildren().clear();

        for (Produit produit : observableList) {
            VBox productCard = createProductCard(produit);
            productContainer.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Produit produit) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(320);
        card.setMinHeight(300);
        card.setMaxHeight(350);
        card.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #E0E0E0;" +
                "-fx-border-width: 1;");

        card.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.rgb(0, 0, 0, 0.2)));

        ImageView productImage = new ImageView();
        productImage.setFitHeight(150);
        productImage.setFitWidth(280);
        productImage.setPreserveRatio(true);

        if (produit.getImageUrl() != null && !produit.getImageUrl().isEmpty()) {
            try {
                File imageFile = new File("src/main/resources/" + produit.getImageUrl());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    productImage.setImage(image);
                } else {
                    productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
                } catch (Exception ex) {}
            }
        } else {
            try {
                productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
            } catch (Exception e) {}
        }

        VBox imageContainer = new VBox(productImage);
        imageContainer.setAlignment(Pos.CENTER);

        VBox infoBox = new VBox(8);

        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2C3E50;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(280);

        Label descLabel = new Label(produit.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(280);
        descLabel.setMaxHeight(60);

        HBox metaBox = new HBox(15);
        metaBox.setAlignment(Pos.CENTER_LEFT);

        Label quantityLabel = new Label("Quantité: " + produit.getQuantite());
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");

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

        Button purchaseBtn = new Button("Acheter");
        purchaseBtn.setPrefWidth(280);
        purchaseBtn.setPrefHeight(35);
        purchaseBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 4;");
        purchaseBtn.setOnAction(e -> handlePurchase(produit));

        infoBox.getChildren().addAll(nameLabel, descLabel, metaBox, purchaseBtn);

        card.getChildren().addAll(imageContainer, infoBox);

        return card;
    }

    private void handlePurchase(Produit produit) {
        if (produit.getQuantite() <= 0) {
            showAlert(Alert.AlertType.WARNING, "Stock épuisé", "Ce produit n'est plus disponible en stock.");
            return;
        }

        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Information d'achat");
        emailDialog.setHeaderText("Veuillez entrer votre adresse email");
        emailDialog.setContentText("Email:");

        Optional<String> emailResult = emailDialog.showAndWait();

        if (emailResult.isPresent() && !emailResult.get().trim().isEmpty()) {
            String clientEmail = emailResult.get().trim();

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

                    produit.setQuantite(produit.getQuantite() - quantity);
                    produitService.modifier(produit);

                    emailService.sendPurchaseNotification(produit.getNom(), quantity, clientEmail);

                    showAlert(Alert.AlertType.INFORMATION, "Achat effectué",
                            "Votre achat de " + quantity + " unité(s) de " + produit.getNom() + " a été effectué avec succès.");

                    loadProducts();

                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre entier valide pour la quantité.");
                }
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}