package controllers;

import entities.Produit;
import entities.Categorie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import services.ProduitService;
import services.CategorieService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.UUID;

public class ajouterProduitController {

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

    private File selectedImageFile;
    private String savedImagePath;
    private final String UPLOAD_DIRECTORY = "src/main/resources/uploads/";

    @FXML
    void initialize() {
        // Créer le répertoire des uploads s'il n'existe pas
        createUploadDirectory();

        // Charger les catégories dans le ComboBox
        loadCategories();
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
            return null;
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
            return null;
        }
    }

    private void loadCategories() {
        CategorieService categorieService = new CategorieService();
        try {
            List<Categorie> categories = categorieService.recuperer();
            ObservableList<Categorie> categorieList = FXCollections.observableArrayList(categories);
            cbCategorie.setItems(categorieList);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur s'est produite lors du chargement des catégories.");
            alert.show();
        }
    }

    @FXML
    void AjouterProduit(ActionEvent event) {
        try {
            ProduitService produitService = new ProduitService();

            String nomProduit = tfnom.getText().trim();
            String descriptionText = tfdescription.getText().trim();
            String quantiteText = tfquantite.getText().trim();
            Categorie selectedCategorie = cbCategorie.getValue();

            // Validation
            if (nomProduit.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Validation erreur");
                alert.setContentText("Le nom du produit ne peut pas être vide.");
                alert.showAndWait();
                return;
            }

            if (descriptionText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Validation erreur");
                alert.setContentText("La description ne peut pas être vide.");
                alert.showAndWait();
                return;
            }

            if (quantiteText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Validation erreur");
                alert.setContentText("La quantité ne peut pas être vide.");
                alert.showAndWait();
                return;
            }

            if (selectedCategorie == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Validation erreur");
                alert.setContentText("Veuillez sélectionner une catégorie.");
                alert.showAndWait();
                return;
            }

            // Validate numeric values for quantity
            int quantiteValue;
            try {
                quantiteValue = Integer.parseInt(quantiteText);
                if (quantiteValue < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Validation erreur");
                    alert.setContentText("La quantité ne peut pas être négative.");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Validation erreur");
                alert.setContentText("La quantité doit être un nombre entier.");
                alert.showAndWait();
                return;
            }

            // Name length validation
            if (nomProduit.length() < 2 || nomProduit.length() > 50) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Validation erreur");
                alert.setContentText("Le nom du produit doit contenir entre 2 et 50 caractères.");
                alert.showAndWait();
                return;
            }

            // Save the image if selected
            String imageUrl = saveImage();

            // All validations passed, add the product
            Produit p = new Produit(0,
                    nomProduit,
                    descriptionText,
                    imageUrl,
                    quantiteValue,
                    selectedCategorie);
            produitService.ajouter(p);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Produit ajouté");
            alert.setContentText("Le produit a été ajouté avec succès.");
            alert.showAndWait();

            // Clear fields
            tfnom.clear();
            tfdescription.clear();
            tfquantite.clear();
            cbCategorie.setValue(null);
            lblImagePath.setText("Aucune image sélectionnée");
            imagePreview.setImage(null);
            selectedImageFile = null;
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    // Nouvelle méthode pour le bouton Enregistrer
    @FXML
    void enregistrerProduit(ActionEvent event) {
        try {
            ProduitService produitService = new ProduitService();

            String nomProduit = tfnom.getText().trim();
            String descriptionText = tfdescription.getText().trim();
            String quantiteText = tfquantite.getText().trim();
            Categorie selectedCategorie = cbCategorie.getValue();

            // Validation
            if (nomProduit.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le nom du produit ne peut pas être vide.");
                return;
            }

            if (descriptionText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "La description ne peut pas être vide.");
                return;
            }

            if (quantiteText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "La quantité ne peut pas être vide.");
                return;
            }

            if (selectedCategorie == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez sélectionner une catégorie.");
                return;
            }

            // Validate numeric values for quantity
            int quantiteValue;
            try {
                quantiteValue = Integer.parseInt(quantiteText);
                if (quantiteValue < 0) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de validation", "La quantité ne peut pas être négative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "La quantité doit être un nombre entier.");
                return;
            }

            // Name length validation
            if (nomProduit.length() < 2 || nomProduit.length() > 50) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le nom du produit doit contenir entre 2 et 50 caractères.");
                return;
            }

            // Save the image if selected
            String imageUrl = saveImage();

            // All validations passed, add the product
            Produit p = new Produit(0,
                    nomProduit,
                    descriptionText,
                    imageUrl,
                    quantiteValue,
                    selectedCategorie);
            produitService.ajouter(p);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le produit a été enregistré avec succès!");

            // Clear fields
            tfnom.clear();
            tfdescription.clear();
            tfquantite.clear();
            cbCategorie.setValue(null);
            lblImagePath.setText("Aucune image sélectionnée");
            imagePreview.setImage(null);
            selectedImageFile = null;
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", ex.getMessage());
        }
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherProduit.fxml"));
            tfnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des produits.");
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
    void naviguerAfficherCategorie(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherCategorie.fxml"));
            tfnom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des catégories.");
        }
    }

    @FXML
    void retourAffichage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherProduit.fxml"));
            Parent root = loader.load();
            tfnom.getScene().setRoot(root);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de navigation");
            alert.setContentText("Impossible de retourner à l'affichage: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}