package Services;

import Entities.Evenement;
import utils.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService {

    private final Connection connection;

    public EvenementService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    public List<Evenement> getAllEvenements() {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM evenement";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setCategorieId(rs.getInt("categorie_id"));
                e.setNom(rs.getString("nom"));
                e.setDescription(rs.getString("description"));
                e.setDateDebut(rs.getDate("date_debut").toLocalDate());
                e.setDateFin(rs.getDate("date_fin").toLocalDate());
                e.setPrix(rs.getDouble("prix"));
                e.setLieu(rs.getString("lieu"));
                e.setPlacesDisponibles(rs.getInt("places_disponibles"));
                e.setImage(rs.getString("image"));
                evenements.add(e);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des événements : " + e.getMessage());
        }

        return evenements;
    }

    public boolean ajouterEvenement(Evenement evenement) {
        String sql = "INSERT INTO evenement (categorie_id, nom, description, date_debut, date_fin, prix, lieu, places_disponibles, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, evenement.getCategorieId());
            ps.setString(2, evenement.getNom());
            ps.setString(3, evenement.getDescription());
            ps.setDate(4, Date.valueOf(evenement.getDateDebut()));
            ps.setDate(5, Date.valueOf(evenement.getDateFin()));
            ps.setDouble(6, evenement.getPrix());
            ps.setString(7, evenement.getLieu());
            ps.setInt(8, evenement.getPlacesDisponibles());
            ps.setString(9, evenement.getImage());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
            return false;
        }
    }

    public boolean modifierEvenement(Evenement evenement) {
        String sql = "UPDATE evenement  SET "
                + "categorie_id = ?, "
                + "nom = ?, "
                + "description = ?, "
                + "date_debut = ?, "
                + "date_fin = ?, "
                + "prix = ?, "
                + "lieu = ?, "
                + "places_disponibles = ?, "
                + "image = ? "
                + "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, evenement.getCategorieId());
            ps.setString(2, evenement.getNom());
            ps.setString(3, evenement.getDescription());
            ps.setDate(4, Date.valueOf(evenement.getDateDebut()));
            ps.setDate(5, Date.valueOf(evenement.getDateFin()));
            ps.setDouble(6, evenement.getPrix());
            ps.setString(7, evenement.getLieu());
            ps.setInt(8, evenement.getPlacesDisponibles());
            ps.setString(9, evenement.getImage());
            ps.setInt(10, evenement.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerEvenement(int id) {
        String sql = "DELETE FROM evenement WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }
}