package Entities;

import java.time.LocalDateTime;

public class Participation {
    private int id;
    private LocalDateTime dateParticipation;
    private int nombrePersonnes;
    private int userId;
    private int evenementId;

    public Participation() {
    }

    public Participation(int id, LocalDateTime dateParticipation, int nombrePersonnes, int userId, int evenementId) {
        this.id = id;
        this.dateParticipation = dateParticipation;
        this.nombrePersonnes = nombrePersonnes;
        this.userId = userId;
        this.evenementId = evenementId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateParticipation() {
        return dateParticipation;
    }

    public void setDateParticipation(LocalDateTime dateParticipation) {
        this.dateParticipation = dateParticipation;
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", dateParticipation=" + dateParticipation +
                ", nombrePersonnes=" + nombrePersonnes +
                ", userId=" + userId +
                ", evenementId=" + evenementId +
                '}';
    }
} 