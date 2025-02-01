package org.esprit.projets.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Commentaire {
    private int id;
    private int id_utilisateur;
    private int id_cours;
    private String contenu;
    private LocalDateTime date_creation;

    // Additional properties for admin view
    private String courseName; // Name of the course
    private String userEmail;  // Email of the user

    // Default constructor
    public Commentaire() {
    }

    // Primary constructor (for normal use)
    public Commentaire(int id, int id_utilisateur, int id_cours, String contenu, LocalDateTime date_creation) {
        this.id = id;
        this.id_utilisateur = id_utilisateur;
        this.id_cours = id_cours;
        this.contenu = contenu;
        this.date_creation = date_creation;
    }

    // Extended constructor for admin view (with extra fields)
    public Commentaire(int id, int id_utilisateur, int id_cours, String contenu, LocalDateTime date_creation,
                       String courseName, String userEmail) {
        this(id, id_utilisateur, id_cours, contenu, date_creation);
        this.courseName = courseName;
        this.userEmail = userEmail;
    }

    // Standard getters and setters
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

    public String getContenu() {
        return contenu;
    }
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDate_creation() {
        return date_creation;
    }
    public void setDate_creation(LocalDateTime date_creation) {
        this.date_creation = date_creation;
    }

    // --- New getters and setters for admin view ---

    /**
     * Returns the name of the course associated with this comment.
     */
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Returns the email of the user who made the comment.
     */
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Returns the comment text.
     * This aliases the 'contenu' field to work with PropertyValueFactory("text").
     */
    public String getText() {
        return contenu;
    }
    public void setText(String text) {
        this.contenu = text;
    }

    /**
     * Returns a formatted date string (yyyy-MM-dd HH:mm) based on date_creation.
     * This method is used by PropertyValueFactory("date").
     */
    public String getDate() {
        if (date_creation == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return date_creation.format(formatter);
    }

    // Optional setter for 'date' if needed (usually not required)
    public void setDate(String date) {
        // This setter is provided only to satisfy the PropertyValueFactory if it attempts to set the date.
        // You could implement parsing logic here if necessary.
    }
}
