package services;

import entities.Categorie;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements Service<Categorie> {

    private Connection cnx;

    public CategorieService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Categorie categorie) throws SQLException {
        String sql = "INSERT INTO categorie (id, nom, description) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, categorie.getId()); // Make sure categorie.getId() returns a valid unique ID
        ps.setString(2, categorie.getNom());
        ps.setString(3, categorie.getDescription());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Categorie categorie) throws SQLException {
        String sql = "UPDATE categorie SET nom = ?, description = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, categorie.getNom());
        ps.setString(2, categorie.getDescription());
        ps.setInt(3, categorie.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Categorie categorie) throws SQLException {
        String sql = "DELETE FROM categorie WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, categorie.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Categorie> recuperer() throws SQLException {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            String description = rs.getString("description");
            Categorie c = new Categorie(id, nom, description);
            categories.add(c);
        }
        return categories;
    }

    public boolean categorieExiste(String nom) throws SQLException {
        String req = "SELECT COUNT(*) FROM categorie WHERE nom = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, nom);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        return count > 0;
    }
}
