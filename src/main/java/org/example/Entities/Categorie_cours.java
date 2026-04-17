package org.example.Entities;

import java.time.LocalDate;

public class Categorie_cours {
    private int id;
    private String titre;
    private String description;
    private String niveau;
    private LocalDate datepublication;

    // Constructor
    public Categorie_cours() {}


    public Categorie_cours( String titre, String description, String niveau, LocalDate  datepublication) {
        this.titre = titre;
        this.description = description;
        this.niveau = niveau;
        this.datepublication = datepublication;
    }

    public Categorie_cours(int id, String titre, String description, String niveau, LocalDate  datepublication) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.niveau = niveau;
        this.datepublication = datepublication;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public LocalDate  getDatepublication() {
        return datepublication;
    }

    public void setDatepublication(LocalDate  datepublication) {
        this.datepublication = datepublication;
    }

    @Override
    public String toString() {
        return "Categorie_cours{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", niveau='" + niveau + '\'' +
                ", datepublication='" + datepublication + '\'' +
                '}';
    }
}
