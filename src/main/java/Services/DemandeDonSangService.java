package Services;

import Entities.CentreDeDon;
import Entities.DemandeDonSang;
import Entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import utils.MyConnection;

public class DemandeDonSangService {
    private final Connection connection;
    
    private UserService userService;
    private CentreDeDonService centreService;
    
    // Constructor with dependencies
    public DemandeDonSangService() {
        this.connection = MyConnection.getInstance().getConnection();
        this.userService = new UserService();
        this.centreService = new CentreDeDonService();
    }
    
    // Constructor with explicit dependencies for testing or DI
    public DemandeDonSangService(UserService userService, CentreDeDonService centreService) {
        this.connection = MyConnection.getInstance().getConnection();
        this.userService = userService;
        this.centreService = centreService;
    }

    // CRUD operations
    public DemandeDonSang createDemande(int userId, int centreId, String groupeSanguin, double quantite) {
        // Validate input
        if (groupeSanguin == null || groupeSanguin.trim().isEmpty()) {
            throw new IllegalArgumentException("Blood group cannot be empty");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        
        // Get user (if userId is 0, set user to null)
        User user = null;
        if (userId > 0) {
            user = userService.getUserById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
        }
        
        CentreDeDon centre = centreService.getCentreById(centreId);
        if (centre == null) {
            throw new IllegalArgumentException("Centre not found");
        }
        
        // Set default status to 'pending'
        String status = "pending";
        
        String query = "INSERT INTO demande_don_sang (user_id, centre_id, groupe_sanguin, quantite, status) VALUES (?, ?, ?, ?, 'pending')";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, centreId);
            stmt.setString(3, groupeSanguin);
            stmt.setDouble(4, quantite);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        return getDemandeById(id);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DemandeDonSang> getAllDemandes() {
        List<DemandeDonSang> demandes = new ArrayList<>();
        String query = "SELECT * FROM demande_don_sang ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemande(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return demandes;
    }

    public DemandeDonSang getDemandeById(int id) {
        String query = "SELECT * FROM demande_don_sang WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDemande(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DemandeDonSang> getDemandesByUserId(int userId) {
        List<DemandeDonSang> demandes = new ArrayList<>();
        String query = "SELECT * FROM demande_don_sang WHERE user_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemande(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return demandes;
    }

    public List<DemandeDonSang> getDemandesByStatus(String status) {
        List<DemandeDonSang> demandes = new ArrayList<>();
        String query = "SELECT * FROM demande_don_sang WHERE status = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemande(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return demandes;
    }

    public List<DemandeDonSang> getDemandesByUserIdAndStatus(int userId, String status) {
        List<DemandeDonSang> demandes = new ArrayList<>();
        String query = "SELECT * FROM demande_don_sang WHERE user_id = ? AND status = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemande(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return demandes;
    }

    public DemandeDonSang updateDemande(int id, String groupeSanguin, double quantite) {
        // Check if demande exists and is in 'pending' status
        DemandeDonSang demande = getDemandeById(id);
        if (demande == null) {
            return null;
        }
        
        if (!demande.getStatus().equals("pending")) {
            throw new IllegalStateException("Can only update pending donation requests");
        }
        
        String query = "UPDATE demande_don_sang SET groupe_sanguin = ?, quantite = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupeSanguin);
            stmt.setDouble(2, quantite);
            stmt.setInt(3, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Return the updated demande
                return getDemandeById(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public DemandeDonSang updateDemandeStatus(int id, String status) {
        // Check if demande exists
        DemandeDonSang demande = getDemandeById(id);
        if (demande == null) {
            return null;
        }
        
        // Validate status
        if (!status.equals("pending") && !status.equals("accepted") && !status.equals("refused")) {
            throw new IllegalArgumentException("Invalid status. Must be 'pending', 'accepted', or 'refused'");
        }
        
        String query = "UPDATE demande_don_sang SET status = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Return the updated demande
                return getDemandeById(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean deleteDemande(int id) {
        // Check if demande exists and is in 'pending' status
        DemandeDonSang demande = getDemandeById(id);
        if (demande == null) {
            return false;
        }
        
        if (!demande.getStatus().equals("pending")) {
            throw new IllegalStateException("Can only delete pending donation requests");
        }
        
        String query = "DELETE FROM demande_don_sang WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Statistics
    public int countDemandesByStatus(String status) {
        String query = "SELECT COUNT(*) as count FROM demande_don_sang WHERE status = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    public double getTotalDonationQuantity() {
        String query = "SELECT SUM(quantite) as total FROM demande_don_sang WHERE status = 'accepted'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    public double getTotalDonationQuantityByBloodGroup(String groupeSanguin) {
        String query = "SELECT SUM(quantite) as total FROM demande_don_sang WHERE status = 'accepted' AND groupe_sanguin = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupeSanguin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public List<DemandeDonSang> getDemandesByCentre(int centreId) {
        List<DemandeDonSang> demandes = new ArrayList<>();
        String query = "SELECT * FROM demande_don_sang WHERE centre_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, centreId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemande(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return demandes;
    }

    public List<DemandeDonSang> getDemandesByBloodGroup(String groupeSanguin) {
        List<DemandeDonSang> demandes = new ArrayList<>();
        String query = "SELECT * FROM demande_don_sang WHERE groupe_sanguin = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupeSanguin);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemande(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return demandes;
    }
    
    private DemandeDonSang mapResultSetToDemande(ResultSet rs) throws SQLException {
        DemandeDonSang demande = new DemandeDonSang();
        demande.setId(rs.getInt("id"));
        
        // Get user_id and load the User object
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            User user = userService.getUserById(userId);
            demande.setUser(user);
        }
        
        // Get centre_id and load the CentreDeDon object
        int centreId = rs.getInt("centre_id");
        CentreDeDon centre = centreService.getCentreById(centreId);
        demande.setCentreDeDon(centre);
        
        demande.setGroupeSanguin(rs.getString("groupe_sanguin"));
        demande.setQuantite(rs.getDouble("quantite"));
        demande.setStatus(rs.getString("status"));
        demande.setCreatedAt(rs.getTimestamp("created_at"));
        
        return demande;
    }

    
} 