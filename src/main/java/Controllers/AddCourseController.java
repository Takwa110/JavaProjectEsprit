package Controllers;

import Entite.Course;
import Services.CourseService;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddCourseController {
    private final CourseService courseService = new CourseService();
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField instructorField; // Added instructor input field
    @FXML
    private Label pdfPathLabel;
    @FXML
    private Button addCourseButton;
    @FXML
    private Button selectPdfButton;
    private String pdfPath;

    public AddCourseController() {
    }

    @FXML
    public void handleAddCourse() {
        String title = this.titleField.getText().trim();
        String description = this.descriptionField.getText().trim();
        String instructor = this.instructorField.getText().trim();  // Getting the instructor name

        if (title.isEmpty()) {
            this.pdfPathLabel.setText("Title cannot be empty.");
        } else if (description.isEmpty()) {
            this.pdfPathLabel.setText("Description cannot be empty.");
        } else if (instructor.isEmpty()) {
            this.pdfPathLabel.setText("Instructor cannot be empty.");
        } else if (this.pdfPath != null && !this.pdfPath.isEmpty()) {
            Course course = new Course(0, title, description, instructor, this.pdfPath);

            try {
                this.courseService.addCourse(course);
                this.pdfPathLabel.setText("Course added successfully!");
            } catch (Exception e) {
                this.pdfPathLabel.setText("Failed to add course: " + e.getMessage());
            }

        } else {
            this.pdfPathLabel.setText("No PDF file selected.");
        }
    }

    @FXML
    public void handleSelectPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", new String[]{"*.pdf"}));
        File pdfFile = fileChooser.showOpenDialog(new Stage());
        if (pdfFile != null) {
            this.pdfPath = pdfFile.getAbsolutePath();
            this.pdfPathLabel.setText("Selected: " + this.pdfPath);
        } else {
            this.pdfPathLabel.setText("No PDF selected.");
        }
    }
}
