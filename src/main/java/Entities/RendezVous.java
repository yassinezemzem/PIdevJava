package Entities;

import java.util.Date;

public class RendezVous {
    private int id;
    private int userId;
    private Date date;
    private String type;
    private String status;

    public RendezVous() {}
    public RendezVous(int id, int userId, Date date, String type, String status) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.type = type;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
} 