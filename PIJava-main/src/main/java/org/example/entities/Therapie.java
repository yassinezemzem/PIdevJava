package org.example.entities;

import java.util.Objects;

public class Therapie {
    private int id;
    private String image;
    private String nom;
    private String description;
    private String objectif;
    private String duree;
    private String type;

    // Constructors
    public Therapie() {}

    public Therapie(int id, String image, String nom, String description, String objectif, String duree, String type) {
        this.id = id;
        this.image = image;
        this.nom = nom;
        this.description = description;
        this.objectif = objectif;
        this.duree = duree;
        this.type = type;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public String getDuree() { return duree; }
    public void setDuree(String duree) { this.duree = duree; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Therapie therapie = (Therapie) o;
        return Objects.equals(image, therapie.image) && Objects.equals(nom, therapie.nom) && Objects.equals(description, therapie.description) && Objects.equals(objectif, therapie.objectif) && Objects.equals(duree, therapie.duree) && Objects.equals(type, therapie.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, nom, description, objectif, duree, type);
    }

    // toString
    @Override
    public String toString() {
        return String.format(
                "Therapie:\n" +
                        "  Name       : %s\n" +
                        "  Image      : %s\n" +
                        "  Description: %s\n" +
                        "  Objectif   : %s\n" +
                        "  Duration   : %s\n" +
                        "  Type       : %s",
                nom, image, description, objectif, duree, type
        );
    }

}