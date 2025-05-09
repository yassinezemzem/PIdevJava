package Services;

import Entities.Categorie;
import Entities.Produit;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements IService<Produit> {

    private Connection cnx;

    public ProduitService() {
        cnx = MyDatabase.getInstance().getCnx();
        if (cnx == null) {
            System.err.println("ERREUR: La connexion à la base de données est nulle dans ProduitService!");
            MyDatabase.getInstance();
            cnx = MyDatabase.getInstance().getCnx();
        }
    }

    @Override
    public void create(Produit produit) {
        ajouter(produit); // Reuse the ajouter method
    }

    @Override
    public void update(Produit produit) {
        modifier(produit); // Reuse the modifier method
    }

    @Override
    public void delete(Produit produit) {
        try {
            String sql = "DELETE FROM produit WHERE id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, produit.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting produit: " + e.getMessage());
        }
    }

    @Override
    public List<Produit> readAll() {
        return afficher(); // Reuse the afficher method
    }

    @Override
    public void ajouter(Produit produit) {
        try {
            String sql = "INSERT INTO produit (nom, description, image_url, quantite, categorie_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setString(1, produit.getNom());
                ps.setString(2, produit.getDescription());
                ps.setString(3, produit.getImageUrl());
                ps.setInt(4, produit.getQuantite());
                ps.setInt(5, produit.getCategorie().getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error adding produit: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Produit produit) {
        try {
            String sql = "UPDATE produit SET nom = ?, description = ?, image_url = ?, quantite = ?, categorie_id = ? WHERE id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setString(1, produit.getNom());
                ps.setString(2, produit.getDescription());
                ps.setString(3, produit.getImageUrl());
                ps.setInt(4, produit.getQuantite());
                ps.setInt(5, produit.getCategorie().getId());
                ps.setInt(6, produit.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating produit: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        try {
            String sql = "DELETE FROM produit WHERE id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting produit by ID: " + e.getMessage());
        }
    }

    @Override
    public List<Produit> afficher() {
        List<Produit> produits = new ArrayList<>();
        try {
            String sql = "SELECT * FROM produit";
            try (Statement st = cnx.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    String description = rs.getString("description");
                    String imageUrl = rs.getString("image_url");
                    int quantite = rs.getInt("quantite");
                    int categorieId = rs.getInt("categorie_id");
                    Categorie categorie = getCategorieById(categorieId);
                    Produit p = new Produit(id, nom, description, imageUrl, quantite, categorie);
                    produits.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving produits: " + e.getMessage());
        }
        return produits;
    }

    public Produit getProduitById(int id) {
        try {
            String sql = "SELECT * FROM produit WHERE id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String nom = rs.getString("nom");
                        String description = rs.getString("description");
                        String imageUrl = rs.getString("image_url");
                        int quantite = rs.getInt("quantite");
                        int categorieId = rs.getInt("categorie_id");
                        Categorie categorie = getCategorieById(categorieId);
                        return new Produit(id, nom, description, imageUrl, quantite, categorie);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving produit by ID: " + e.getMessage());
        }
        return null;
    }

    private Categorie getCategorieById(int id) throws SQLException {
        String sql = "SELECT * FROM categorie WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    String description = rs.getString("description");
                    return new Categorie(id, nom, description);
                }
            }
        }
        return null;
    }

    public List<Produit> rechercherProduits(String critere) {
        List<Produit> produits = new ArrayList<>();
        try {
            String sql = "SELECT * FROM produit WHERE nom LIKE ? OR description LIKE ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                String searchTerm = "%" + critere + "%";
                ps.setString(1, searchTerm);
                ps.setString(2, searchTerm);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nom = rs.getString("nom");
                        String description = rs.getString("description");
                        String imageUrl = rs.getString("image_url");
                        int quantite = rs.getInt("quantite");
                        int categorieId = rs.getInt("categorie_id");
                        Categorie categorie = getCategorieById(categorieId);
                        Produit p = new Produit(id, nom, description, imageUrl, quantite, categorie);
                        produits.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching produits: " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> getProduitsByCategorie(int categorieId) {
        List<Produit> produits = new ArrayList<>();
        try {
            String sql = "SELECT * FROM produit WHERE categorie_id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, categorieId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nom = rs.getString("nom");
                        String description = rs.getString("description");
                        String imageUrl = rs.getString("image_url");
                        int quantite = rs.getInt("quantite");
                        Categorie categorie = getCategorieById(categorieId);
                        Produit p = new Produit(id, nom, description, imageUrl, quantite, categorie);
                        produits.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving produits by category: " + e.getMessage());
        }
        return produits;
    }
}