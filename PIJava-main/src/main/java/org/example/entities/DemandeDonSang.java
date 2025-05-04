package org.example.entities;

import java.util.Date;

public class DemandeDonSang {
    private int id;
    private org.example.entities.User user;
    private CentreDeDon centreDeDon;
    private String groupeSanguin;
    private double quantite;
    private String status;
    private Date createdAt;

    // Default constructor
    public DemandeDonSang() {
    }

    // Constructor with all fields except ID
    public DemandeDonSang(org.example.entities.User user, CentreDeDon centreDeDon, String groupeSanguin, double quantite, String status, Date createdAt) {
        this.user = user;
        this.centreDeDon = centreDeDon;
        this.groupeSanguin = groupeSanguin;
        this.quantite = quantite;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor with all fields including ID
    public DemandeDonSang(int id, org.example.entities.User user, CentreDeDon centreDeDon, String groupeSanguin, double quantite, String status, Date createdAt) {
        this.id = id;
        this.user = user;
        this.centreDeDon = centreDeDon;
        this.groupeSanguin = groupeSanguin;
        this.quantite = quantite;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public org.example.entities.User getUser() {
        return user;
    }

    public void setUser(org.example.entities.User user) {
        this.user = user;
    }

    public CentreDeDon getCentreDeDon() {
        return centreDeDon;
    }

    public void setCentreDeDon(CentreDeDon centreDeDon) {
        this.centreDeDon = centreDeDon;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DemandeDonSang{" +
                "id=" + id +
                ", user=" + user +
                ", centreDeDon=" + centreDeDon +
                ", groupeSanguin='" + groupeSanguin + '\'' +
                ", quantite=" + quantite +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 