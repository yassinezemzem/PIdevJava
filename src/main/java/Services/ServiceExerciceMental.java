package Services;

import Entities.ExerciceMental;
import utils.MyConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExerciceMental  {
    private Connection conn;

    public ServiceExerciceMental() {
        conn = MyConnection.getInstance().getConnection();
    }


    public void ajouter(ExerciceMental exercice) {
        String sql = "INSERT INTO `exercice_mental`(`therapie_id`, `video_url`, `titre`, `description`, `duree_minutes`, `objectif`) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, exercice.getTherapieId());
            pst.setString(2, exercice.getVideoUrl());
            pst.setString(3, exercice.getTitre());
            pst.setString(4, exercice.getDescription());
            pst.setInt(5, exercice.getDureeMinutes());
            pst.setString(6, exercice.getObjectif());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'exercice mental : " + e.getMessage());
        }
    }


    public void modifier(ExerciceMental exercice) {
        String sql = "UPDATE `exercice_mental` SET `therapie_id`=?, `video_url`=?, `titre`=?, `description`=?, `duree_minutes`=?, `objectif`=? WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, exercice.getTherapieId());
            pst.setString(2, exercice.getVideoUrl());
            pst.setString(3, exercice.getTitre());
            pst.setString(4, exercice.getDescription());
            pst.setInt(5, exercice.getDureeMinutes());
            pst.setString(6, exercice.getObjectif());
            pst.setInt(7, exercice.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'exercice mental : " + e.getMessage());
        }
    }


    public void supprimer(int id) {
        String sql = "DELETE FROM `exercice_mental` WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'exercice mental : " + e.getMessage());
        }
    }


    public List<ExerciceMental> afficher() {
        List<ExerciceMental> exercices = new ArrayList<>();
        String sql = "SELECT * FROM `exercice_mental`";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ExerciceMental exercice = new ExerciceMental();
                exercice.setId(rs.getInt("id"));
                exercice.setTherapieId(rs.getInt("therapie_id"));
                exercice.setVideoUrl(rs.getString("video_url"));
                exercice.setTitre(rs.getString("titre"));
                exercice.setDescription(rs.getString("description"));
                exercice.setDureeMinutes(rs.getInt("duree_minutes"));
                exercice.setObjectif(rs.getString("objectif"));
                exercices.add(exercice);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des exercices mentaux : " + e.getMessage());
        }
        return exercices;
    }

    public List<ExerciceMental> findByTherapieId(int id) {
        return afficher().stream().filter(ex -> ex.getTherapieId() == id).toList();
    }
} 