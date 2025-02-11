package org.esprit.projets.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class GetAllCoursesController {

    // FXML injected components
    @FXML
    private Button getCoursesButton;

    @FXML
    private ListView<String> coursesListView;

    // Method to handle the "Get All Courses" button click
    @FXML
    private void handleGetCourses() {
        // Example: Fetch the courses (this could be from a database or a service)
        String[] courses = {"Java Programming", "Data Structures", "Algorithms", "Web Development"};

        // Add courses to the ListView
        coursesListView.getItems().clear(); // Clear any previous courses
        for (String course : courses) {
            coursesListView.getItems().add(course); // Add each course
        }

        // Optional: Show a success message
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Courses Loaded");
        alert.setHeaderText(null);
        alert.setContentText("Courses have been successfully loaded!");
        alert.showAndWait();
    }
}
