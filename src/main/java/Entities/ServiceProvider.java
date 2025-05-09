package Entities;

public class ServiceProvider {
    private int id;
    private String name;
    private String lastName;
    private String phone;
    private String service;
    private double latitude;
    private double longitude;

    // Constructors
    public ServiceProvider() {}

    public ServiceProvider(String name, String lastName, String phone, String service, double latitude, double longitude) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.service = service;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ServiceProvider(int id, String name, String lastName, String phone, String service, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.service = service;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        return "ServiceProvider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", service='" + service + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
} 