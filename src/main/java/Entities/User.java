package Entities;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private List<String> roles;
    private String password;
    private Date createdAt;

    // Default constructor
    public User() {
        this.roles = new ArrayList<>();
    }

    // Constructor with all fields except ID
    public User(String fullName, String email, String phone, List<String> roles, String password, Date createdAt) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roles = roles != null ? roles : new ArrayList<>();
        this.password = password;
        this.createdAt = createdAt;
    }

    // Constructor with all fields including ID
    public User(int id, String fullName, String email, String phone, List<String> roles, String password, Date createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roles = roles != null ? roles : new ArrayList<>();
        this.password = password;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", roles=" + roles +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 