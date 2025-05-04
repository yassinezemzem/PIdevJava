package Entities;

public class Produit {
    private int id;
    private String nom;
    private String description;
    private int quantite;
    private String type;

    public Produit() {}
    public Produit(int id, String nom, String description, int quantite, String type) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.quantite = quantite;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", quantite=" + quantite +
                ", type='" + type + '\'' +
                '}';
    }
} 