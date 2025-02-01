package org.esprit.projets.entity;

import java.time.LocalDateTime;

public class Likes {
    private int id;
    private int id_utilisateur;
    private int id_cours;
    private LocalDateTime date_creation;

    // Constructor
    public Likes(int id, int id_utilisateur, int id_cours, LocalDateTime date_creation) {
        this.id = id;
        this.id_utilisateur = id_utilisateur;
        this.id_cours = id_cours;
        this.date_creation = date_creation;
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }
    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    public int getId_cours() {
        return id_cours;
    }
    public void setId_cours(int id_cours) {
        this.id_cours = id_cours;
    }

    public LocalDateTime getDate_creation() {
        return date_creation;
    }
    public void setDate_creation(LocalDateTime date_creation) {
        this.date_creation = date_creation;
    }
}
