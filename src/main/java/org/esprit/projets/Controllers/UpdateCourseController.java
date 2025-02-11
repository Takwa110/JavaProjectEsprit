package org.esprit.projets.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class UpdateCourseController {

    // FXML injected components
    @FXML
    private TextField courseNameField;

    @FXML
    private TextArea courseDescriptionField;

    @FXML
    private Button updateButton;

    // Handle the button click to update the course
    @FXML
    private void handleUpdateCourse() {
        String courseName = courseNameField.getText();
        String courseDescription = courseDescriptionField.getText();

        if (courseName.isEmpty() || courseDescription.isEmpty()) {
            // Show an error message if fields are empty
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Input Validation Error");
            alert.setContentText("Please provide both the course name and description.");
            alert.showAndWait();
        } else {
            // Simulate updating the course (you can integrate this with a database or service)
            // For now, just display a confirmation message
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Course Updated");
            alert.setHeaderText(null);
            alert.setContentText("Course \"" + courseName + "\" has been updated successfully!");
            alert.showAndWait();

            // Optionally, clear the fields after successful update
            courseNameField.clear();
            courseDescriptionField.clear();
        }
    }
}
