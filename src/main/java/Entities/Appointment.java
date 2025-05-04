package Entities;

import java.sql.Date;

public class Appointment {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String service;
    private Date date;
    private double latitude;
    private double longitude;

    // Constructors
    public Appointment() {}

    public Appointment(String name, String email, String phone, String service, Date date, double latitude, double longitude) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.service = service;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Appointment(int id, String name, String email, String phone, String service, Date date, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.service = service;
        this.date = date;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        return "Appointment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", service='" + service + '\'' +
                ", date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
} 