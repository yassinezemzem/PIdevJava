package Controllers;

import Entities.Categorie;
import Entities.Produit;
import Services.CategorieService;
import Services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class modifierProduitController implements Initializable {

    @FXML
    private BorderPane rootPane;

    @FXML
    private ComboBox<Produit> cbProduits;

    @FXML
    private ComboBox<Categorie> cbCategories;

    @FXML
    private TextField tfnom;

    @FXML
    private TextField tfdescription;

    @FXML
    private TextField tfquantite;

    @FXML
    private Button btnChooseImage;

    @FXML
    private Label lblImagePath;

    @FXML
    private ImageView imagePreview;

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
    private CategorieService categorieService = new CategorieService();

    private File selectedImageFile;
    private String currentImageUrl;
    private final String UPLOAD_DIRECTORY = "src/main/resources/uploads/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            loadLoginScreen();
            return;
        }

        // Set welcome message
        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getFullName());

        // Highlight product list button
        highlightActiveButton(productListButton);

        // Créer le répertoire des uploads s'il n'existe pas
        createUploadDirectory();

        // Charger les produits dans le ComboBox
        List<Produit> produits = produitService.readAll();
        ObservableList<Produit> produitList = FXCollections.observableArrayList(produits);
        cbProduits.setItems(produitList);

        // Charger les catégories dans le ComboBox
        List<Categorie> categories = categorieService.readAll();
        ObservableList<Categorie> categorieList = FXCollections.observableArrayList(categories);
        cbCategories.setItems(categorieList);

        // Listener pour remplir les champs lorsqu'un produit est sélectionné
        cbProduits.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tfnom.setText(newValue.getNom());
                tfdescription.setText(newValue.getDescription());
                tfquantite.setText(String.valueOf(newValue.getQuantite()));

                // Conserver l'URL de l'image actuelle
                currentImageUrl = newValue.getImageUrl();

                // Afficher l'image actuelle si elle existe
                if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                    try {
                        File imageFile = new File("src/main/resources/" + currentImageUrl);
                        if (imageFile.exists()) {
                            Image image = new Image(imageFile.toURI().toString());
                            imagePreview.setImage(image);
                            lblImagePath.setText(currentImageUrl);
                        } else {
                            imagePreview.setImage(null);
                            lblImagePath.setText("Image non trouvée");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        imagePreview.setImage(null);
                        lblImagePath.setText("Erreur de chargement d'image");
                    }
                } else {
                    imagePreview.setImage(null);
                    lblImagePath.setText("Aucune image");
                }

                // Sélectionner la catégorie du produit dans le ComboBox
                Categorie produitCategorie = newValue.getCategorie();
                if (produitCategorie != null) {
                    for (Categorie cat : cbCategories.getItems()) {
                        if (cat.getId() == produitCategorie.getId()) {
                            cbCategories.getSelectionModel().select(cat);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void createUploadDirectory() {
        File directory = new File(UPLOAD_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @FXML
    void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image produit");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedImageFile = fileChooser.showOpenDialog(btnChooseImage.getScene().getWindow());

        if (selectedImageFile != null) {
            lblImagePath.setText(selectedImageFile.getName());

            // Afficher un aperçu de l'image
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                imagePreview.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'aperçu de l'image.");
            }
        }
    }

    private String saveImage() {
        if (selectedImageFile == null) {
            // Si aucune nouvelle image n'est sélectionnée, conserver l'image actuelle
            return currentImageUrl;
        }

        try {
            // Générer un nom de fichier unique pour éviter les conflits
            String uniqueFileName = UUID.randomUUID().toString() + "_" + selectedImageFile.getName();
            Path targetPath = Paths.get(UPLOAD_DIRECTORY + uniqueFileName);

            // Copier le fichier dans le répertoire des uploads
            Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "uploads/" + uniqueFileName;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'enregistrement de l'image: " + e.getMessage());
            return currentImageUrl; // Conserver l'ancienne image en cas d'erreur
        }
    }

    @FXML
    void modifierProduit(ActionEvent event) {
        Produit selectedProduit = cbProduits.getSelectionModel().getSelectedItem();
        Categorie selectedCategorie = cbCategories.getSelectionModel().getSelectedItem();

        // Validation du produit sélectionné
        if (selectedProduit == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à modifier.");
            return;
        }

        // Validation du nom (obligatoire et longueur)
        String nom = tfnom.getText().trim();
        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Le nom du produit est obligatoire.");
            tfnom.requestFocus();
            return;
        }

        if (nom.length() < 2 || nom.length() > 50) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Le nom du produit doit contenir entre 2 et 50 caractères.");
            tfnom.requestFocus();
            return;
        }

        // Validation de la description (obligatoire)
        String description = tfdescription.getText().trim();
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "La description du produit est obligatoire.");
            tfdescription.requestFocus();
            return;
        }

        // Validation de la quantité (obligatoire et numérique)
        String quantiteStr = tfquantite.getText().trim();
        if (quantiteStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "La quantité est obligatoire.");
            tfquantite.requestFocus();
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(quantiteStr);
            if (quantite < 0) {
                showAlert(Alert.AlertType.WARNING, "Attention", "La quantité ne peut pas être négative.");
                tfquantite.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Attention", "La quantité doit être un nombre entier.");
            tfquantite.requestFocus();
            return;
        }

        // Validation de la catégorie
        if (selectedCategorie == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une catégorie.");
            cbCategories.requestFocus();
            return;
        }

        // Sauvegarder l'image si une nouvelle a été sélectionnée
        String imageUrl = saveImage();

        // Mettre à jour les propriétés du produit
        selectedProduit.setNom(nom);
        selectedProduit.setDescription(description);
        selectedProduit.setQuantite(quantite);
        selectedProduit.setCategorie(selectedCategorie);
        selectedProduit.setImageUrl(imageUrl);

        // Mettre à jour le produit dans la base de données
        produitService.modifier(selectedProduit);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Le produit a été modifié avec succès.");

        // Rafraîchir la liste des produits
        List<Produit> produits = produitService.readAll();
        ObservableList<Produit> produitList = FXCollections.observableArrayList(produits);
        cbProduits.setItems(produitList);

        // Réinitialiser la sélection
        cbProduits.getSelectionModel().select(null);
        cbCategories.getSelectionModel().select(null);
        tfnom.clear();
        tfdescription.clear();
        tfquantite.clear();
        imagePreview.setImage(null);
        lblImagePath.setText("Aucune image");
        selectedImageFile = null;
        currentImageUrl = null;
    }

    @FXML
    void retourAffichage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherProduit.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des produits.");
        }
    }

    @FXML
    void naviguerAjouterCategorie(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ajouterCategorie.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de catégorie.");
        }
    }

    @FXML
    void naviguerAfficherCategorie(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherCategorie.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des catégories.");
        }
    }

    @FXML
    void naviguerAjouterProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ajouterProduit.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de produit.");
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