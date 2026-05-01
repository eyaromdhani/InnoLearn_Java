package org.example.Entities;

import java.time.LocalDateTime;

public class InscriptionEvent {
    private int id;
    private String name;
    private String email;
    private LocalDateTime dateInscrit;
    private String status;
    private int eventId;
    private Integer userId;

    public InscriptionEvent() {
    }

    public InscriptionEvent(int id, String name, String email, LocalDateTime dateInscrit, String status, int eventId, Integer userId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dateInscrit = dateInscrit;
        this.status = status;
        this.eventId = eventId;
        this.userId = userId;
    }
    public InscriptionEvent( String name, String email, LocalDateTime dateInscrit, String status, int eventId, Integer userId) {

        this.name = name;
        this.email = email;
        this.dateInscrit = dateInscrit;
        this.status = status;
        this.eventId = eventId;
        this.userId = userId;
    }

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

    public LocalDateTime getDateInscrit() {
        return dateInscrit;
    }

    public void setDateInscrit(LocalDateTime dateInscrit) {
        this.dateInscrit = dateInscrit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "👤 " + name +
                " | 📧 " + email +
                " | 📅 " + (dateInscrit != null ? dateInscrit.toLocalDate() : "N/A") +
                " | 🎯 Event ID: " + eventId +
                " | 🔖 " + status;
    }
}
