package org.example.entities;

import org.example.service.ServiceTherapie;

import java.util.Objects;

public class ExerciceMental {
    private int id;
    private int therapieId;
    private String videoUrl;
    private String titre;
    private String description;
    private int dureeMinutes;
    private String objectif;

    // Constructors
    public ExerciceMental() {}

    public ExerciceMental(int id, int therapieId, String videoUrl, String titre, String description, int dureeMinutes, String objectif) {
        this.id = id;
        this.therapieId = therapieId;
        this.videoUrl = videoUrl;
        this.titre = titre;
        this.description = description;
        this.dureeMinutes = dureeMinutes;
        this.objectif = objectif;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTherapieId() { return therapieId; }
    public void setTherapieId(int therapieId) { this.therapieId = therapieId; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciceMental that = (ExerciceMental) o;
        return therapieId == that.therapieId && dureeMinutes == that.dureeMinutes && Objects.equals(videoUrl, that.videoUrl) && Objects.equals(titre, that.titre) && Objects.equals(description, that.description) && Objects.equals(objectif, that.objectif);
    }

    @Override
    public int hashCode() {
        return Objects.hash(therapieId, videoUrl, titre, description, dureeMinutes, objectif);
    }

    // toString
    @Override
    public String toString() {
        ServiceTherapie serviceTherapie=new ServiceTherapie();

        return String.format(
                "Exercice Mental:\n" +
                        "  Therapy    : %s\n" +
                        "  Video URL  : %s\n" +
                        "  Titre      : %s\n" +
                        "  Description: %s\n" +
                        "  Duration   : %d minutes\n" +
                        "  Objectif   : %s",
                serviceTherapie.getNomById(therapieId),
                videoUrl,
                titre,
                description,
                dureeMinutes,
                objectif
        );
    }
}