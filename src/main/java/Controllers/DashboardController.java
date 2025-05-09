package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private BorderPane rootPane;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button centreDeDonButton;

    @FXML
    private Button demandeDonSangButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button logoutButton;

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
    private Button addEventAdminButton;

    @FXML
    private Label welcomeLabel;

    private String currentView = "dashboard";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            loadLoginScreen();
            return;
        }

        // Set welcome message
        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getFullName());

        // Initialize with the default dashboard view
        highlightActiveButton(dashboardButton);

        // Update window title
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setTitle("Better Health - Dashboard");
        } catch (Exception e) {
            // This might not be available yet during initialization, which is OK
        }

        // Make buttons visible for all users
        centreDeDonButton.setVisible(true);
        demandeDonSangButton.setVisible(true);
        categoryListButton.setVisible(true);
        addCategoryButton.setVisible(true);
        addProductButton.setVisible(true);
        productListButton.setVisible(true);
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

    @FXML
    void onDashboardHome(ActionEvent event) {
        if (!"dashboard".equals(currentView)) {
            // Clear the content area and show the default home view
            contentArea.getChildren().clear();

            // Create welcome screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardHome.fxml"));
            try {
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "dashboard";
                highlightActiveButton(dashboardButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onCentreDeDonManagement(ActionEvent event) {
        if (!"centredebon".equals(currentView)) {
            try {
                // Clear the content area
                contentArea.getChildren().clear();

                // Load the Centre de Don view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CentreDeDonListView.fxml"));
                Parent view = loader.load();

                // Add the view to our content area
                contentArea.getChildren().add(view);

                // Update the current view tracker
                currentView = "centredebon";
                highlightActiveButton(centreDeDonButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onDemandeDonSangManagement(ActionEvent event) {
        if (!"demandedon".equals(currentView)) {
            try {
                // Clear the content area
                contentArea.getChildren().clear();

                // Load the Demande Don Sang view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemandeDonSangListView.fxml"));
                Parent view = loader.load();

                // Add the view to our content area
                contentArea.getChildren().add(view);

                // Update the current view tracker
                currentView = "demandedon";
                highlightActiveButton(demandeDonSangButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onStatistics(ActionEvent event) {
        if (!"statistics".equals(currentView)) {
            try {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DemandeDonSangStatistics.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "statistics";
                highlightActiveButton(statisticsButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onTherapyList(ActionEvent event) {
        if (!"therapy".equals(currentView)) {
            try {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-therapie-admin.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "therapy";
                highlightActiveButton(therapyListButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onMentalExerciseList(ActionEvent event) {
        if (!"mentalexercise".equals(currentView)) {
            try {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-exercicemntal-admin.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
                currentView = "mentalexercise";
                highlightActiveButton(mentalExerciseListButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onCategoryList(ActionEvent event) {
        try {
            // Load the Afficher Catégorie view and replace the entire scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/afficherCategorie.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Afficher Catégorie");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddCategory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ajouterCategorie.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Ajouter Catégorie");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ajouterProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Ajouter Produit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onProductList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/afficherProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Better Health - Afficher Produits");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddEventAdmin(ActionEvent event) {
        if (!"eventsadmin".equals(currentView)) {
            try {
                // Clear the content area
                contentArea.getChildren().clear();

                // Load the Events Admin view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/eventsadmin.fxml"));
                Parent view = loader.load();

                // Add the view to our content area
                contentArea.getChildren().add(view);

                // Update the current view tracker
                currentView = "eventsadmin";
                highlightActiveButton(addEventAdminButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}