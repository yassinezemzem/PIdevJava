package Controllers;

import Entities.Categorie;
import Services.CategorieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class modifierCategorieController implements Initializable {

    @FXML
    private BorderPane rootPane;

    @FXML
    private ComboBox<Categorie> cbCategories;

    @FXML
    private TextField tfnom;

    @FXML
    private TextField tfdescription;

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

    private CategorieService categorieService = new CategorieService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            loadLoginScreen();
            return;
        }

        // Set welcome message
        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getFullName());

        // Highlight category button
        highlightActiveButton(categoryListButton);

        // Charger les catégories dans le ComboBox
        List<Categorie> categories = categorieService.readAll();
        ObservableList<Categorie> observableList = FXCollections.observableArrayList(categories);
        cbCategories.setItems(observableList);

        // Listener pour remplir les champs lorsqu'une catégorie est sélectionnée
        cbCategories.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tfnom.setText(newValue.getNom());
                tfdescription.setText(newValue.getDescription());
            }
        });
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
        List<Categorie> categories = categorieService.readAll();
        for (Categorie cat : categories) {
            if (cat.getId() != selectedCategorie.getId() && cat.getNom().equalsIgnoreCase(nom)) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Une catégorie avec ce nom existe déjà.");
                tfnom.requestFocus();
                return;
            }
        }

        selectedCategorie.setNom(nom);
        selectedCategorie.setDescription(description);

        categorieService.modifier(selectedCategorie);

        showAlert(Alert.AlertType.INFORMATION, "Modification catégorie", "Catégorie modifiée avec succès");


    }

    @FXML
    void retourAffichage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherCategorie.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage.");
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
    void naviguerAfficherProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherProduit.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des produits.");
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}