package Entities;

public class Therapie {
    private int id;
    private String titre;
    private String description;
    private String type;

    public Therapie() {}
    public Therapie(int id, String titre, String description, String type) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "Therapie{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
} 