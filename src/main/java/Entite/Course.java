package Entite;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Course {
    private int id;
    private StringProperty title;
    private StringProperty description;
    private StringProperty instructor;
    private StringProperty pdfPath;

    public Course(int id, String title, String description, String instructor,  String pdfPath) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.pdfPath = new SimpleStringProperty(pdfPath);
    }

    public Course(String title, String description, String instructor,  String pdfPath) {
        this(0, title, description, instructor, pdfPath);
    }

    public Course(int id, String title, String description, String pdfPath) {
    }

    public int getId() { return this.id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return (String)this.title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return (String)this.description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getInstructor() { return instructor.get(); }

    public void setInstructor(String instructor) { this.instructor.set(instructor);}

    public String getPdfPath() {
        return (String)this.pdfPath.get();
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

}
