package Controllers;

import Entities.Produit;
import Services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class afficherProduitController implements Initializable {

    @FXML
    private BorderPane rootPane;

    @FXML
    private FlowPane productContainer;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button centreDeDonButton;

    @FXML
    private Button demandeDonSangButton;

    @FXML
    private Button addEventAdminButton;

    @FXML
    private Button statisticsButton;

    @FXML
    private Button therapyListButton;

    @FXML
    private Button mentalExerciseListButton;

    @FXML
    private Button categoryListButton;

    @FXML
    private Button addCategoryButton;

    @FXML
    private Button addProductButton;

    @FXML
    private Button productListButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label welcomeLabel;

    private ProduitService produitService = new ProduitService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            loadLoginScreen();
            return;
        }

        // Set welcome message
        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getFullName());

        // Highlight the "Afficher Produits" button
        highlightActiveButton(productListButton);

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
        card.setPrefWidth(680);
        card.setMinHeight(150);
        card.setMaxHeight(200);
        card.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #E0E0E0;" +
                "-fx-border-width: 1;");

        card.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.rgb(0, 0, 0, 0.2)));

        HBox contentBox = new HBox(20);

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
                    productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
                } catch (Exception ex) {
                    // Silently ignore if default image is also not available
                }
            }
        } else {
            try {
                productImage.setImage(new Image(getClass().getResourceAsStream("/default-product.png")));
            } catch (Exception e) {
                // Silently ignore if default image is also not available
            }
        }

        VBox infoBox = new VBox(8);
        infoBox.setPrefWidth(350);

        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2C3E50;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(350);

        Label descLabel = new Label(produit.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(350);
        descLabel.setMaxHeight(60);

        infoBox.getChildren().addAll(nameLabel, descLabel);

        VBox metaBox = new VBox(8);
        metaBox.setAlignment(Pos.TOP_RIGHT);
        metaBox.setPrefWidth(150);

        Label quantityLabel = new Label("Quantité: " + produit.getQuantite());
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");

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

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);

        Button editBtn = new Button("Modifier");
        editBtn.setPrefWidth(90);
        editBtn.setPrefHeight(30);
        editBtn.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-background-radius: 4;");
        editBtn.setOnAction(e -> navigateToModifierProduit());

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setPrefWidth(90);
        deleteBtn.setPrefHeight(30);
        deleteBtn.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-background-radius: 4;");
        deleteBtn.setOnAction(e -> confirmDeleteProduct(produit));

        Region spacer = new Region();
        spacer.setPrefHeight(10);
        spacer.setStyle("-fx-use-pref-size: true;");
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        buttonsBox.getChildren().addAll(editBtn, deleteBtn);
        metaBox.getChildren().addAll(spacer, buttonsBox);

        contentBox.getChildren().addAll(productImage, infoBox, metaBox);
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
            produitService.supprimer(produit.getId());
            loadProducts();
        }
    }

    @FXML
    void onDashboardHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onCentreDeDonManagement(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/CentreDeDonListView.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDemandeDonSangManagement(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/DemandeDonSangListView.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onStatistics(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/DemandeDonSangStatistics.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onTherapyList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficher-therapie-admin.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onMentalExerciseList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficher-exercicemntal-admin.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onCategoryList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherCategorie.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddCategory(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ajouterCategorie.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddProduct(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ajouterProduit.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onProductList(ActionEvent event) {
        // Already on this page, no action needed
    }

    @FXML
    void onAddEventAdmin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/tools1.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin - Event Management");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        SessionManager.logout();
        loadLoginScreen();
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToModifierProduit() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/modifierProduit.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page de modification de produit.");
        }
    }

    private void highlightActiveButton(Button activeButton) {
        // Reset all buttons to transparent background
        dashboardButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        centreDeDonButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        demandeDonSangButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        statisticsButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        therapyListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        mentalExerciseListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        categoryListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        addCategoryButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        addProductButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");
        productListButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");

        // Set the active button to have a highlighted background
        activeButton.setStyle("-fx-background-color: #34495E; -fx-background-radius: 5;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}