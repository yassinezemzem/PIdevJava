package Services;

import Entities.ServiceProvider;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProviderService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pidevjava";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Get database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // CREATE
    public void saveServiceProvider(ServiceProvider serviceProvider) {
        String query = "INSERT INTO service_provider (name, last_name, phone, service, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, serviceProvider.getName());
            stmt.setString(2, serviceProvider.getLastName());
            stmt.setString(3, serviceProvider.getPhone());
            stmt.setString(4, serviceProvider.getService());
            stmt.setDouble(5, serviceProvider.getLatitude());
            stmt.setDouble(6, serviceProvider.getLongitude());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ
    public List<ServiceProvider> getAllServiceProviders() {
        List<ServiceProvider> serviceProviders = new ArrayList<>();
        String query = "SELECT * FROM service_provider";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ServiceProvider serviceProvider = new ServiceProvider(
                        rs.getString("name"),
                        rs.getString("last_name"),
                        rs.getString("phone"),
                        rs.getString("service"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                );
                serviceProvider.setId(rs.getInt("id"));
                serviceProviders.add(serviceProvider);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serviceProviders;
    }

    // UPDATE
    public void updateServiceProvider(ServiceProvider serviceProvider) {
        String query = "UPDATE service_provider SET name = ?, last_name = ?, phone = ?, service = ?, latitude = ?, longitude = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, serviceProvider.getName());
            stmt.setString(2, serviceProvider.getLastName());
            stmt.setString(3, serviceProvider.getPhone());
            stmt.setString(4, serviceProvider.getService());
            stmt.setDouble(5, serviceProvider.getLatitude());
            stmt.setDouble(6, serviceProvider.getLongitude());
            stmt.setInt(7, serviceProvider.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void deleteServiceProvider(int id) {
        String query = "DELETE FROM service_provider WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 