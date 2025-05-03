package Entities;

import java.util.Date;

public class CentreDeDon {
    private int id;
    private String name;
    private String address;
    private String telephone;
    private String email;
    private Date createdAt;
    private double latitude;
    private double longitude;

    // Default constructor
    public CentreDeDon() {
    }

    // Constructor with all fields except ID
    public CentreDeDon(String name, String address, String telephone, String email, Date createdAt, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructor with all fields including ID
    public CentreDeDon(int id, String name, String address, String telephone, String email, Date createdAt, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "CentreDeDon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
} 