package Controllers;

import Entities.Categorie;
import Services.CategorieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Optional;

public class supprimerCategorieController {

    @FXML
    private TableView<Categorie> tvCategories;

    @FXML
    private TableColumn<Categorie, String> colNom;

    @FXML
    private TableColumn<Categorie, String> colDescription;

    private CategorieService categorieService = new CategorieService();

    @FXML
    void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        ObservableList<Categorie> categories = FXCollections.observableArrayList(categorieService.readAll()); // Changed recuperer to readAll
        tvCategories.setItems(categories);
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    @FXML
    void supprimerCategorie(ActionEvent event) {
        Categorie selectedCategorie = tvCategories.getSelectionModel().getSelectedItem();

        if (selectedCategorie == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une catégorie à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la catégorie");
        confirmation.setContentText("Voulez-vous vraiment supprimer la catégorie " + selectedCategorie.getNom() + " ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            categorieService.supprimer(selectedCategorie.getId()); // Pass the ID instead of the Categorie object
            showAlert(Alert.AlertType.INFORMATION, "Suppression catégorie", "Catégorie supprimée avec succès");
            loadCategories(); // Recharger la liste des catégories
        }
    }

    @FXML
    void retourAffichage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherCategorie.fxml"));
            tvCategories.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}