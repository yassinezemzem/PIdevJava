package org.example.service;

import org.example.entities.Therapie;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceTherapie implements IService<Therapie>{
    private Connection conn;
    public ServiceTherapie() {
        conn= MyDatabase.getInstance().getConn();
    }
    @Override
    public void ajouter(Therapie therapie) {
        String sql="INSERT INTO `therapie`( `image`, `nom`, `description`, `objectif`, `duree`, `type`) VALUES (?,?,?,?,?,?)";
        try(PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setString(1, therapie.getImage());
            pst.setString(2, therapie.getNom());
            pst.setString(3, therapie.getDescription());
            pst.setString(4, therapie.getObjectif());
            pst.setString(5, therapie.getDuree());
            pst.setString(6, therapie.getType());
            pst.executeUpdate();
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Therapie therapie) {
        String sql="UPDATE `therapie` SET `image`=?,`nom`=?,`description`=?,`objectif`=?,`duree`=?,`type`=? WHERE id=?";
        try(PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setString(1, therapie.getImage());
            pst.setString(2, therapie.getNom());
            pst.setString(3, therapie.getDescription());
            pst.setString(4, therapie.getObjectif());
            pst.setString(5, therapie.getDuree());
            pst.setString(6, therapie.getType());
            pst.setInt(7, therapie.getId());
            pst.executeUpdate();
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void supprimer(int id) {
        String sql="DELETE FROM `therapie` WHERE id=?";
        try(PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Therapie> afficher() {
        List<Therapie> therapies= new ArrayList<Therapie>();
        String sql="SELECT * FROM `therapie`";
        try(Statement pst=conn.createStatement()){
            ResultSet rs=pst.executeQuery(sql);
            while(rs.next()){
                Therapie therapie=new Therapie();
                therapie.setId(rs.getInt("id"));
                therapie.setImage(rs.getString("image"));
                therapie.setNom(rs.getString("nom"));
                therapie.setDescription(rs.getString("description"));
                therapie.setObjectif(rs.getString("objectif"));
                therapie.setDuree(rs.getString("duree"));
                therapie.setType(rs.getString("type"));
                therapies.add(therapie);
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return therapies;
    }
    public List<String> getTitlesTherapie(){
        return afficher().stream().map(t->t.getNom()).collect(Collectors.toList());
    }
    public int getIdByNom(String nom){
        return afficher().stream().filter(t->t.getNom().equals(nom)).findFirst().get().getId();
    }
    public String getNomById(int id){
        return afficher().stream().filter(t->t.getId()==id).findFirst().get().getNom();

    }
    public Map<String, Integer> loadCountsByType() {
        String sql = "SELECT type, COUNT(*) AS nbr FROM therapie GROUP BY type";
        Map<String,Integer> map = new LinkedHashMap<>();
        try (Statement pst=conn.createStatement();
             ResultSet rs = pst.executeQuery(sql)) {

            while (rs.next()) {
                map.put(rs.getString("type"), rs.getInt("nbr"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
}
