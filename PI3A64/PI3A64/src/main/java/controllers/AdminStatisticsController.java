package controllers;

import entities.Categorie;
import entities.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import services.CategorieService;
import services.ProduitService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminStatisticsController {

    @FXML
    private Button btnAddCategory;

    @FXML
    private Button btnDisplayCategories;

    @FXML
    private Button btnAddProduct;

    @FXML
    private Button btnDisplayProducts;

    @FXML
    private Button btnStatistics;

    @FXML
    private Button btnBackToHome;

    @FXML
    private Button btnRefresh;

    @FXML
    private Label lblTotalProducts;

    @FXML
    private Label lblTotalCategories;

    @FXML
    private Label lblTotalStock;

    @FXML
    private PieChart pieChartCategories;

    @FXML
    private BarChart<String, Number> barChartProducts;

    private ProduitService produitService;
    private CategorieService categorieService;

    @FXML
    void initialize() {
        try {
            produitService = new ProduitService();
            categorieService = new CategorieService();
            
            // Charger les données statistiques
            loadStatistics();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les statistiques: " + e.getMessage());
        }
    }

    @FXML
    void refreshData(ActionEvent event) {
        try {
            loadStatistics();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Les statistiques ont été actualisées.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'actualiser les statistiques: " + e.getMessage());
        }
    }

    private void loadStatistics() throws SQLException {
        // Récupérer les données
        List<Produit> produits = produitService.recuperer();
        List<Categorie> categories = categorieService.recuperer();
        
        // Calculer les statistiques
        int totalProducts = produits.size();
        int totalCategories = categories.size();
        int totalStock = produits.stream().mapToInt(Produit::getQuantite).sum();
        
        // Mettre à jour les labels
        lblTotalProducts.setText(String.valueOf(totalProducts));
        lblTotalCategories.setText(String.valueOf(totalCategories));
        lblTotalStock.setText(String.valueOf(totalStock));
        
        // Charger le graphique en secteurs (produits par catégorie)
        loadPieChart(produits);
        
        // Charger le graphique en barres (stock par produit)
        loadBarChart(produits);
    }
    
    private void loadPieChart(List<Produit> produits) {
        // Créer un map pour compter les produits par catégorie
        Map<String, Integer> productsByCategory = new HashMap<>();
        
        for (Produit produit : produits) {
            if (produit.getCategorie() != null) {
                String categorieName = produit.getCategorie().getNom();
                productsByCategory.put(categorieName, productsByCategory.getOrDefault(categorieName, 0) + 1);
            }
        }
        
        // Créer les données du graphique
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : productsByCategory.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }
        
        // Mettre à jour le graphique
        pieChartCategories.setData(pieChartData);
    }
    
    private void loadBarChart(List<Produit> produits) {
        // Trier les produits par quantité en stock (décroissant)
        List<Produit> topProducts = produits.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getQuantite(), p1.getQuantite()))
                .limit(5) // Prendre les 5 premiers produits
                .collect(Collectors.toList());
        
        // Créer la série de données
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantité en stock");
        
        for (Produit produit : topProducts) {
            series.getData().add(new XYChart.Data<>(produit.getNom(), produit.getQuantite()));
        }
        
        // Effacer les données précédentes et ajouter la nouvelle série
        barChartProducts.getData().clear();
        barChartProducts.getData().add(series);
    }

    @FXML
    void navigateToAddCategory(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterCategorie.fxml"));
            btnBackToHome.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de catégorie.");
        }
    }

    @FXML
    void navigateToDisplayCategories(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherCategorie.fxml"));
            btnBackToHome.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des catégories.");
        }
    }

    @FXML
    void navigateToAddProduct(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterProduit.fxml"));
            btnBackToHome.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'ajout de produit.");
        }
    }

    @FXML
    void navigateToDisplayProducts(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherProduit.fxml"));
            btnBackToHome.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la page d'affichage des produits.");
        }
    }

    @FXML
    void backToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/selectionPage.fxml"));
            btnBackToHome.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du retour à la page d'accueil.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 