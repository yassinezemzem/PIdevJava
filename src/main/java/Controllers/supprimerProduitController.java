package Controllers;

import Entities.Produit;
import Services.ProduitService;
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

public class supprimerProduitController {

    @FXML
    private TableView<Produit> tvProduits;

    @FXML
    private TableColumn<Produit, String> colNom;

    @FXML
    private TableColumn<Produit, String> colDescription;

    @FXML
    private TableColumn<Produit, Integer> colQuantite;

    @FXML
    private TableColumn<Produit, String> colCategorie;

    private ProduitService produitService = new ProduitService();

    @FXML
    void initialize() {
        loadProduits();
    }

    private void loadProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.readAll()); // Changed recuperer to readAll
        tvProduits.setItems(produits);
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        // Adjust colCategorie to display the category name
        colCategorie.setCellValueFactory(cellData -> {
            Produit produit = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(produit.getCategorie() != null ? produit.getCategorie().getNom() : "N/A");
        });
    }

    @FXML
    void supprimerProduit(ActionEvent event) {
        Produit selectedProduit = tvProduits.getSelectionModel().getSelectedItem();

        if (selectedProduit == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le produit");
        confirmation.setContentText("Voulez-vous vraiment supprimer le produit " + selectedProduit.getNom() + " ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            produitService.supprimer(selectedProduit.getId()); // Pass the ID instead of the Produit object
            showAlert(Alert.AlertType.INFORMATION, "Suppression produit", "Produit supprimé avec succès");
            loadProduits(); // Recharger la liste des produits
        }
    }

    @FXML
    void retourAffichage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/afficherProduit.fxml"));
            tvProduits.getScene().setRoot(root);
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