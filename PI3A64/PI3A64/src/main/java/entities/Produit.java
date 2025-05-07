package entities;
public class Produit {
    private int id;
    private String nom;
    private String description;
    private String imageUrl;
    private int quantite;
    private Categorie categorie;

    public Produit(int id, String nom, String description, String imageUrl, int quantite, Categorie categorie) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.imageUrl = imageUrl;
        this.quantite = quantite;
        this.categorie = categorie;
    }

    // Constructor without image for backward compatibility
    public Produit(int id, String nom, String description, int quantite, Categorie categorie) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.imageUrl = null;
        this.quantite = quantite;
        this.categorie = categorie;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return nom + " - " + description + " (Quantité : " + quantite + ")";
    }
}
