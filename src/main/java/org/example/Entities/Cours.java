package org.example.Entities;

import java.time.LocalDateTime;

public class Cours {
    private int id;
    private String nom;
    private String description;
    private String slug;
    private String typeMedia;
    private String mediaUrl;
    private int duree;
    private String niveau;
    private LocalDateTime dateCreation;
    private int enseignantId;
    private int categorieCourId;

    // Constructor
    public Cours() {
    }

    public Cours(int id, String nom, String description, String slug, String typeMedia, String mediaUrl, int duree, String niveau, LocalDateTime dateCreation, int enseignantId, int categorieCourId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.slug = slug;
        this.typeMedia = typeMedia;
        this.mediaUrl = mediaUrl;
        this.duree = duree;
        this.niveau = niveau;
        this.dateCreation = dateCreation;
        this.enseignantId = enseignantId;
        this.categorieCourId = categorieCourId;
    }

    public Cours(String nom, String description, String slug, String typeMedia, String mediaUrl, int duree, String niveau, LocalDateTime dateCreation, int enseignantId, int categorieCourId) {
        this.nom = nom;
        this.description = description;
        this.slug = slug;
        this.typeMedia = typeMedia;
        this.mediaUrl = mediaUrl;
        this.duree = duree;
        this.niveau = niveau;
        this.dateCreation = dateCreation;
        this.enseignantId = enseignantId;
        this.categorieCourId = categorieCourId;
    }

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTypeMedia() {
        return typeMedia;
    }

    public void setTypeMedia(String typeMedia) {
        this.typeMedia = typeMedia;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(int enseignantId) {
        this.enseignantId = enseignantId;
    }

    public int getCategorieCourId() {
        return categorieCourId;
    }

    public void setCategorieCourId(int categorieCourId) {
        this.categorieCourId = categorieCourId;
    }

    @Override
    public String toString() {
        return "Cours{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", slug='" + slug + '\'' +
                ", typeMedia='" + typeMedia + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", duree=" + duree +
                ", niveau='" + niveau + '\'' +
                ", dateCreation=" + dateCreation +
                ", enseignantId=" + enseignantId +
                ", categorieCourId=" + categorieCourId +
                '}';
    }
}
