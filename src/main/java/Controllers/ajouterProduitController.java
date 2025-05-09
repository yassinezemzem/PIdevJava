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

public class ajouterProduitController implements Initializable {

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField tfnom;

    @FXML
    private TextField tfdescription;

    @FXML
    private TextField tfquantite;

    @FXML
    private ComboBox<Categorie> cbCategorie;

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

    private File selectedImageFile;
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

        // Highlight the "Ajouter Produit" button
        highlightActiveButton(addProductButton);

        createUploadDirectory();
        loadCategories();
    }

    private void createUploadDirectory() {
        File directory = new File(UPLOAD_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void loadCategories() {
        CategorieService categorieService = new CategorieService();
        List<Categorie> categories = categorieService.afficher();
        ObservableList<Categorie> categorieList = FXCollections.observableArrayList(categories);
        cbCategorie.setItems(categorieList);
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
        if (selectedImageFile == null) return null;
        try {
            String uniqueFileName = UUID.randomUUID().toString() + "_" + selectedImageFile.getName();
            Path targetPath = Paths.get(UPLOAD_DIRECTORY + uniqueFileName);
            Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "uploads/" + uniqueFileName;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'enregistrement de l'image: " + e.getMessage());
            return null;
        }
    }

    @FXML
    void AjouterProduit(ActionEvent event) {
        ProduitService produitService = new ProduitService();

        String nomProduit = tfnom.getText().trim();
        String descriptionText = tfdescription.getText().trim();
        String quantiteText = tfquantite.getText().trim();
        Categorie selectedCategorie = cbCategorie.getValue();

        if (nomProduit.isEmpty() || descriptionText.isEmpty() || quantiteText.isEmpty() || selectedCategorie == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        int quantiteValue;
        try {
            quantiteValue = Integer.parseInt(quantiteText);
            if (quantiteValue < 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Quantité invalide.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être un entier.");
            return;
        }

        if (nomProduit.length() < 2 || nomProduit.length() > 50) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du produit doit contenir entre 2 et 50 caractères.");
            return;
        }

        String imageUrl = saveImage();

        Produit p = new Produit(0, nomProduit, descriptionText, imageUrl, quantiteValue, selectedCategorie);
        produitService.ajouter(p);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté avec succès.");

        // Reset form
        tfnom.clear();
        tfdescription.clear();
        tfquantite.clear();
        cbCategorie.setValue(null);
        lblImagePath.setText("Aucune image sélectionnée");
        imagePreview.setImage(null);
        selectedImageFile = null;
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherProduit.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page des produits.");
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
        // Already on this page, no action needed
    }

    @FXML
    void onProductList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherProduit.fxml"));
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