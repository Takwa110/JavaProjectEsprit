package org.esprit.projets.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cours {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final IntegerProperty likes;

    // Primary constructor
    public Cours(int id, String nom, int likes) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.likes = new SimpleIntegerProperty(likes);
    }

    // Getters and setters for id
    public int getId() {
        return id.get();
    }
    public void setId(int id) {
        this.id.set(id);
    }
    public IntegerProperty idProperty() {
        return id;
    }

    // Getters and setters for nom
    public String getNom() {
        return nom.get();
    }
    public void setNom(String nom) {
        this.nom.set(nom);
    }
    public StringProperty nomProperty() {
        return nom;
    }

    // Getters and setters for likes
    public int getLikes() {
        return likes.get();
    }
    public void setLikes(int likes) {
        this.likes.set(likes);
    }
    public IntegerProperty likesProperty() {
        return likes;
    }
}
