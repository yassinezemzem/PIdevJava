package services;

import entities.Categorie;
import entities.Produit;
import utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements Service<Produit> {

    private Connection cnx;

    public ProduitService() {
        cnx = MyDatabase.getInstance().getCnx();
        // Vérifier si la connexion est établie
        if (cnx == null) {
            System.err.println("ERREUR: La connexion à la base de données est nulle dans ProduitService!");
            // Réessayer d'établir la connexion
            MyDatabase.getInstance(); // Forcer une nouvelle instance
            cnx = MyDatabase.getInstance().getCnx();
        }
    }

    @Override
    public void ajouter(Produit produit) throws SQLException {
        String sql = "INSERT INTO produit (nom, description, image_url, quantite, categorie_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setString(3, produit.getImageUrl());
            ps.setInt(4, produit.getQuantite());
            ps.setInt(5, produit.getCategorie().getId()); // L'ID de la catégorie
            ps.executeUpdate();
        }
    }

    @Override
    public void modifier(Produit produit) throws SQLException {
        String sql = "UPDATE produit SET nom = ?, description = ?, image_url = ?, quantite = ?, categorie_id = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setString(3, produit.getImageUrl());
            ps.setInt(4, produit.getQuantite());
            ps.setInt(5, produit.getCategorie().getId()); // L'ID de la catégorie
            ps.setInt(6, produit.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(Produit produit) throws SQLException {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, produit.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Produit> recuperer() throws SQLException {
        List<Produit> produits = new ArrayList<>();
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

                // Récupérer la catégorie via son ID
                Categorie categorie = getCategorieById(categorieId);

                Produit p = new Produit(id, nom, description, imageUrl, quantite, categorie);
                produits.add(p);
            }
        }

        return produits;
    }

    public Produit getProduitById(int id) throws SQLException {
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

    public List<Produit> rechercherProduits(String critere) throws SQLException {
        List<Produit> produits = new ArrayList<>();
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

        return produits;
    }

    public List<Produit> getProduitsByCategorie(int categorieId) throws SQLException {
        List<Produit> produits = new ArrayList<>();
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

        return produits;
    }
}