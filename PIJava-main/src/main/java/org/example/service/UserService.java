package org.example.service;


import org.example.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class UserService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Get database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // CRUD operations
    public User createUser(String fullName, String email, String phone, List<String> roles, String password) {
        // Validate input
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Check if email already exists
        if (findUserByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Format roles as a JSON array
        String rolesJson = "[\"" + String.join("\",\"", roles) + "\"]";
        
        String sql = "INSERT INTO users (full_name, email, phone, roles, password, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
                     
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, rolesJson);
            stmt.setString(5, password);
            stmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new User(id, fullName, email, phone, roles, password, new Date());
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        
        return users;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public User findUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<User> findUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE roles LIKE ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + role + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = extractUserFromResultSet(rs);
                    if (user.hasRole(role)) {
                        users.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }

    public User updateUser(int id, String fullName, String email, String phone, List<String> roles, String password) {
        User user = getUserById(id);
        if (user == null) {
            return null;
        }
        
        // Check if email is being changed and already exists
        if (!user.getEmail().equals(email)) {
            User existingUser = findUserByEmail(email);
            if (existingUser != null && existingUser.getId() != id) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        
        // Convert roles list to comma-separated string
        String rolesStr = String.join(",", roles);
        
        String sql;
        PreparedStatement stmt = null;
        
        try (Connection conn = getConnection()) {
            // If password is provided, update all fields including password
            if (password != null && !password.trim().isEmpty()) {
                sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, roles = ?, password = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, fullName);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                stmt.setString(4, rolesStr);
                stmt.setString(5, password);
                stmt.setInt(6, id);
            } else {
                // Otherwise, update all fields except password
                sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, roles = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, fullName);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                stmt.setString(4, rolesStr);
                stmt.setInt(5, id);
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Return the updated user
                return getUserById(id);
            }
            
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
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

    // Authentication
    public User authenticateUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Helper method to extract a User from a ResultSet
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String rolesStr = rs.getString("roles");
        List<String> roles = new ArrayList<>();
        
        // Parse JSON array of roles
        if (rolesStr != null && !rolesStr.isEmpty()) {
            // Remove the square brackets and split by comma
            rolesStr = rolesStr.substring(1, rolesStr.length() - 1);
            if (!rolesStr.isEmpty()) {
                String[] roleArray = rolesStr.split(",");
                for (String role : roleArray) {
                    // Remove quotes and trim
                    role = role.replace("\"", "").trim();
                    roles.add(role);
                }
            }
        }
        
        String password = rs.getString("password");
        Date createdAt = new Date(rs.getTimestamp("created_at").getTime());
        
        return new User(id, fullName, email, phone, roles, password, createdAt);
    }
} 