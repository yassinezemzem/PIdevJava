package Services;

import Entities.CentreDeDon;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CentreDeDonService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pidevjava";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Get database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    // CRUD operations with database
    public CentreDeDon createCentre(String name, String address, String telephone, String email, double latitude, double longitude) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        if (telephone == null || telephone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telephone cannot be empty");
        }
        
        // Check if a centre with the same name and address already exists
        if (findCentreByNameAndAddress(name, address) != null) {
            throw new IllegalArgumentException("Centre with the same name and address already exists");
        }
        
        String sql = "INSERT INTO centre_de_don (name, address, telephone, email, created_at, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, telephone);
            stmt.setString(4, email);
            stmt.setTimestamp(5, new Timestamp(new Date().getTime()));
            stmt.setDouble(6, latitude);
            stmt.setDouble(7, longitude);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating centre failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new CentreDeDon(id, name, address, telephone, email, new Date(), latitude, longitude);
                } else {
                    throw new SQLException("Creating centre failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    public List<CentreDeDon> getAllCentres() {
        List<CentreDeDon> centres = new ArrayList<>();
        String sql = "SELECT * FROM centre_de_don";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                CentreDeDon centre = extractCentreFromResultSet(rs);
                centres.add(centre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving centres: " + e.getMessage());
        }
        
        return centres;
    }

    public CentreDeDon getCentreById(int id) {
        String sql = "SELECT * FROM centre_de_don WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCentreFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public CentreDeDon findCentreByNameAndAddress(String name, String address) {
        String sql = "SELECT * FROM centre_de_don WHERE name = ? AND address = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, address);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCentreFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<CentreDeDon> findCentresByName(String name) {
        List<CentreDeDon> centres = new ArrayList<>();
        String sql = "SELECT * FROM centre_de_don WHERE name LIKE ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    centres.add(extractCentreFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return centres;
    }

    public CentreDeDon updateCentre(int id, String name, String address, String telephone, String email, double latitude, double longitude) {
        // Check if centre exists
        CentreDeDon existingCentre = getCentreById(id);
        if (existingCentre == null) {
            return null;
        }
        
        // Check if name and address are being changed and already exist for another centre
        CentreDeDon duplicateCentre = findCentreByNameAndAddress(name, address);
        if (duplicateCentre != null && duplicateCentre.getId() != id) {
            throw new IllegalArgumentException("Centre with the same name and address already exists");
        }
        
        String sql = "UPDATE centre_de_don SET name = ?, address = ?, telephone = ?, email = ?, latitude = ?, longitude = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, telephone);
            stmt.setString(4, email);
            stmt.setDouble(5, latitude);
            stmt.setDouble(6, longitude);
            stmt.setInt(7, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Return the updated centre
                return getCentreById(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean deleteCentre(int id) {
        String sql = "DELETE FROM centre_de_don WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Geographic operations
    public List<CentreDeDon> findNearestCentres(double latitude, double longitude, int limit) {
        // Get all centres and calculate distances in memory
        // This could be optimized with a spatial database query in the future
        List<CentreDeDon> allCentres = getAllCentres();
        
        return allCentres.stream()
                .sorted((c1, c2) -> Double.compare(
                    calculateDistance(latitude, longitude, c1.getLatitude(), c1.getLongitude()),
                    calculateDistance(latitude, longitude, c2.getLatitude(), c2.getLongitude())
                ))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    // Helper method to extract a CentreDeDon from a ResultSet
    private CentreDeDon extractCentreFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String address = rs.getString("address");
        String telephone = rs.getString("telephone");
        String email = rs.getString("email");
        Date createdAt = new Date(rs.getTimestamp("created_at").getTime());
        double latitude = rs.getDouble("latitude");
        double longitude = rs.getDouble("longitude");
        
        return new CentreDeDon(id, name, address, telephone, email, createdAt, latitude, longitude);
    }
    
    // Helper method to calculate distance using Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
} 