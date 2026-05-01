package org.example.Entities;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String titre;
    private String description;
    private String typeEvenement;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private int capacite;
    private String statut;

    public Event() {
    }

    public Event(int id, String titre, String description, String typeEvenement, LocalDateTime dateDebut, LocalDateTime dateFin, String lieu, int capacite, String statut) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.capacite = capacite;
        this.statut = statut;
    }

    public Event(String titre, String description, String typeEvenement, LocalDateTime dateDebut, LocalDateTime dateFin, String lieu, int capacite, String statut) {
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.capacite = capacite;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTypeEvenement() { return typeEvenement; }
    public void setTypeEvenement(String typeEvenement) { this.typeEvenement = typeEvenement; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return titre +
                " | 📍 " + lieu +
                " | 👥 " + capacite + " places" +
                " | 📅 " + (dateDebut != null ? dateDebut.toLocalDate() : "N/A") +
                " | 🔖 " + statut;
    }
}
