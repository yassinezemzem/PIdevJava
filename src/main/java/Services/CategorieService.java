package Services;

import Entities.Categorie;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements IService<Categorie> {

    private final Connection cnx; // Marked as final since it's initialized once

    public CategorieService() {
        cnx = MyDatabase.getInstance().getCnx();
        if (cnx == null) {
            System.err.println("ERREUR: La connexion à la base de données est nulle dans CategorieService!");
            // Optionally reattempt connection if needed
        }
    }

    @Override
    public void create(Categorie categorie) {
        ajouter(categorie); // Reuse ajouter method
    }

    @Override
    public void update(Categorie categorie) {
        modifier(categorie); // Reuse modifier method
    }

    @Override
    public void delete(Categorie categorie) {
        try {
            String sql = "DELETE FROM categorie WHERE id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, categorie.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting categorie: " + e.getMessage());
        }
    }

    @Override
    public List<Categorie> readAll() {
        return afficher(); // Reuse afficher method
    }

    @Override
    public void ajouter(Categorie categorie) {
        try {
            String sql = "INSERT INTO categorie (id, nom, description) VALUES (?, ?, ?)";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, categorie.getId()); // Ensure getId() provides a valid unique ID
                ps.setString(2, categorie.getNom());
                ps.setString(3, categorie.getDescription());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error adding categorie: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Categorie categorie) {
        try {
            String sql = "UPDATE categorie SET nom = ?, description = ? WHERE id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setString(1, categorie.getNom());
                ps.setString(2, categorie.getDescription());
                ps.setInt(3, categorie.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating categorie: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        try {
            String sql = "DELETE FROM categorie WHERE id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting categorie by ID: " + e.getMessage());
        }
    }

    @Override
    public List<Categorie> afficher() {
        List<Categorie> categories = new ArrayList<>();
        try {
            String sql = "SELECT * FROM categorie";
            try (Statement st = cnx.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    String description = rs.getString("description");
                    Categorie c = new Categorie(id, nom, description);
                    categories.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
        }
        return categories;
    }

    public boolean categorieExiste(String nom) {
        try {
            String req = "SELECT COUNT(*) FROM categorie WHERE nom = ?";
            try (PreparedStatement ps = cnx.prepareStatement(req)) {
                ps.setString(1, nom);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking categorie existence: " + e.getMessage());
            return false; // Default to false on error
        }
    }
}