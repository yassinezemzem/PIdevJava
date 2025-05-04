package Entities;

import java.sql.Date;
import java.time.LocalDate;

public class Evenement {
    private int id;
    private int categorieId;
    private String nom;
    private String description;
    private Date datedebut;
    private Date datefin;
    private double prix;
    private String lieu;
    private int placesDisponibles;
    private String image;

    public Evenement() {}
    public Evenement(int id, int categorieId, String nom, String description, Date datedebut, Date datefin, double prix, String lieu, int placesDisponibles, String image) {
        this.id = id;
        this.categorieId = categorieId;
        this.nom = nom;
        this.description = description;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.prix = prix;
        this.lieu = lieu;
        this.placesDisponibles = placesDisponibles;
        this.image = image;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCategorieId() { return categorieId; }
    public void setCategorieId(int categorieId) { this.categorieId = categorieId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDatedebut() { return datedebut; }
    public void setDatedebut(Date datedebut) { this.datedebut = datedebut; }
    public LocalDate getDateDebut() { return datedebut.toLocalDate(); }
    public void setDateDebut(LocalDate dateDebut) { this.datedebut = Date.valueOf(dateDebut); }

    public Date getDatefin() { return datefin; }
    public void setDatefin(Date datefin) { this.datefin = datefin; }
    public LocalDate getDateFin() { return datefin.toLocalDate(); }
    public void setDateFin(LocalDate dateFin) { this.datefin = Date.valueOf(dateFin); }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public int getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", categorieId=" + categorieId +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", datedebut=" + datedebut +
                ", datefin=" + datefin +
                ", prix=" + prix +
                ", lieu='" + lieu + '\'' +
                ", placesDisponibles=" + placesDisponibles +
                ", image='" + image + '\'' +
                '}';
    }
} 