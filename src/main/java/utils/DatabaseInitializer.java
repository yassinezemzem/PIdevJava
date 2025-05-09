package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "test1";
    private static final String DB_URL_WITH_DB = DB_URL + DB_NAME;
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    public static void initializeDatabase() {
        createDatabaseIfNotExists();
        createTablesIfNotExist();
    }
    
    private static void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(sql);
            System.out.println("Database checked/created successfully");
            
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTablesIfNotExist() {
        try (Connection conn = DriverManager.getConnection(DB_URL_WITH_DB, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create centre_de_don table if it doesn't exist
            String createCentreTable = "CREATE TABLE IF NOT EXISTS centre_de_don (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "address VARCHAR(255) NOT NULL," +
                    "telephone VARCHAR(20) NOT NULL," +
                    "email VARCHAR(100)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "latitude DOUBLE DEFAULT 0," +
                    "longitude DOUBLE DEFAULT 0," +
                    "UNIQUE KEY name_address (name, address)" +
                    ")";
            
            stmt.executeUpdate(createCentreTable);
            System.out.println("Table centre_de_don checked/created successfully");
            
            // Create user table if it doesn't exist
            String createUserTable = "CREATE TABLE IF NOT EXISTS user (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) NOT NULL," +
                    "role VARCHAR(20) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            
            stmt.executeUpdate(createUserTable);
            System.out.println("Table user checked/created successfully");
            
            // Create demande_don_sang table if it doesn't exist
            String createDemandeTable = "CREATE TABLE IF NOT EXISTS demande_don_sang (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NULL," +
                    "centre_id INT NOT NULL," +
                    "groupe_sanguin VARCHAR(5) NOT NULL," +
                    "quantite DOUBLE NOT NULL," +
                    "status VARCHAR(20) NOT NULL DEFAULT 'pending'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL," +
                    "FOREIGN KEY (centre_id) REFERENCES centre_de_don(id)" +
                    ")";
            
            stmt.executeUpdate(createDemandeTable);
            System.out.println("Table demande_don_sang checked/created successfully");
            
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to add some sample data if needed
    public static void addSampleData() {
        try (Connection conn = DriverManager.getConnection(DB_URL_WITH_DB, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Check if centre_de_don table is empty
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM centre_de_don");
            rs.next();
            int count = rs.getInt("count");
            
            if (count == 0) {
                // Add sample data
                String insertSample1 = "INSERT INTO centre_de_don (name, address, telephone, email, latitude, longitude) " +
                        "VALUES ('Centre de Don Central', '123 Main Street', '+216 71 123 456', 'central@example.com', 36.8065, 10.1815)";
                
                String insertSample2 = "INSERT INTO centre_de_don (name, address, telephone, email, latitude, longitude) " +
                        "VALUES ('Centre de Don Nord', '456 North Avenue', '+216 71 789 123', 'nord@example.com', 36.8189, 10.1657)";
                
                String insertSample3 = "INSERT INTO centre_de_don (name, address, telephone, email, latitude, longitude) " +
                        "VALUES ('Centre de Don Sud', '789 South Street', '+216 71 456 789', 'sud@example.com', 36.7951, 10.1859)";
                
                stmt.executeUpdate(insertSample1);
                stmt.executeUpdate(insertSample2);
                stmt.executeUpdate(insertSample3);
                
                System.out.println("Sample centre_de_don data added successfully");
            }
            
            // Add sample user if table is empty
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM user");
            rs.next();
            count = rs.getInt("count");
            
            if (count == 0) {
                String insertUser = "INSERT INTO user (username, password, email, role) " +
                        "VALUES ('admin', 'admin123', 'admin@example.com', 'ADMIN')";
                
                stmt.executeUpdate(insertUser);
                System.out.println("Sample user data added successfully");
            }
            
            // Add sample demande_don_sang if table is empty
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM demande_don_sang");
            rs.next();
            count = rs.getInt("count");
            
            if (count == 0) {
                // Get first centre id
                rs = stmt.executeQuery("SELECT id FROM centre_de_don LIMIT 1");
                int centreId = 0;
                if (rs.next()) {
                    centreId = rs.getInt("id");
                    
                    // Insert sample demandes
                    String insertDemande1 = "INSERT INTO demande_don_sang (user_id, centre_id, groupe_sanguin, quantite, status) " +
                            "VALUES (NULL, " + centreId + ", 'A+', 0.5, 'pending')";
                    
                    String insertDemande2 = "INSERT INTO demande_don_sang (user_id, centre_id, groupe_sanguin, quantite, status) " +
                            "VALUES (NULL, " + centreId + ", 'O-', 0.75, 'accepted')";
                    
                    // Get second centre id
                    rs = stmt.executeQuery("SELECT id FROM centre_de_don LIMIT 1 OFFSET 1");
                    if (rs.next()) {
                        int centreId2 = rs.getInt("id");
                        
                        String insertDemande3 = "INSERT INTO demande_don_sang (user_id, centre_id, groupe_sanguin, quantite, status) " +
                                "VALUES (NULL, " + centreId2 + ", 'B-', 1.0, 'refused')";
                        
                        stmt.executeUpdate(insertDemande1);
                        stmt.executeUpdate(insertDemande2);
                        stmt.executeUpdate(insertDemande3);
                        
                        System.out.println("Sample demande_don_sang data added successfully");
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 