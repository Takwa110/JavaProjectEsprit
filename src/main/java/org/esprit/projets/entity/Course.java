package org.esprit.projets.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Course {
    private int id;
    private StringProperty title;
    private StringProperty description;
    private StringProperty instructor;
    private StringProperty pdfPath;
    private IntegerProperty likes;

    public Course(int id, String title, String description, String instructor, String pdfPath, int likes) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.instructor = new SimpleStringProperty(instructor);
        this.pdfPath = new SimpleStringProperty(pdfPath);
        this.likes = new SimpleIntegerProperty(likes);
    }

    public Course(String title, String description, String instructor, String pdfPath, int likes) {
        this(0, title, description, instructor, pdfPath, likes);
    }

    // Constructeur modifié pour initialiser les propriétés
    public Course(int id, String title, String description, String pdfPath, int likes) {
        this(id, title, description, "", pdfPath, likes);
    }

    public int getId() { return this.id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return this.description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getInstructor() {
        return instructor.get();
    }

    public void setInstructor(String instructor) {
        this.instructor.set(instructor);
    }

    public String getPdfPath() {
        return this.pdfPath.get();
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath.set(pdfPath);
    }

    public ObservableValue<String> courseNameProperty() {
        return this.title;
    }

    public ObservableValue<String> descriptionProperty() {
        return this.description;
    }

    public ObservableValue<String> pdfPathProperty() {
        return this.pdfPath;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty instructorProperty() {
        return instructor;
    }

    public int getLikes() {
        return likes.get();
    }

    public IntegerProperty likesProperty() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes.set(likes);
    }
}
