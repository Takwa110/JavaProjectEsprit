package Controllers;

import Entite.Course;
import Services.CourseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class DashboardController {

    private final CourseService courseService = new CourseService();
    private final ObservableList<Course> coursesList = FXCollections.observableArrayList();

    @FXML
    private TableView<Course> courseTableView;

    @FXML
    private TableColumn<Course, String> titleColumn;
    @FXML
    private TableColumn<Course, String> descriptionColumn;
    @FXML
    private TableColumn<Course, String> pdfPathColumn;

    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField pdfPathField;

    @FXML
    private Button addCourseButton;
    @FXML
    private Button deleteCourseButton;
    @FXML
    private Button updateCourseButton;
    @FXML
    private Button chooseFileButton;

    private String selectedPdfPath = "";

    @FXML
    public void initialize() {
        // Initialize TableView columns
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        pdfPathColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPdfPath()));

        // Load courses from the database
        loadCourses();

        // Set button actions
        addCourseButton.setOnAction(e -> addCourse());
        deleteCourseButton.setOnAction(e -> deleteCourse());
        updateCourseButton.setOnAction(e -> updateCourse());
        chooseFileButton.setOnAction(e -> chooseFile());
    }

    // Load courses from the database and display them in the table
    private void loadCourses() {
        List<Course> courseList = courseService.getAllCourses();
        coursesList.clear();
        coursesList.addAll(courseList);
        courseTableView.setItems(coursesList);
    }

    // Add a new course to the database
    private void addCourse() {
        String title = titleField.getText();
        String description = descriptionField.getText();

        if (title.isEmpty() || description.isEmpty() || selectedPdfPath.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }

        Course newCourse = new Course(title, description, "", selectedPdfPath);
        courseService.addCourse(newCourse);
        loadCourses();  // Reload the course list
        showAlert("Success", "Course added successfully!");
    }

    // Delete a selected course
    private void deleteCourse() {
        Course selectedCourse = courseTableView.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            courseService.deleteCourse(selectedCourse.getId());
            loadCourses();  // Reload the course list
            showAlert("Success", "Course deleted successfully!");
        } else {
            showAlert("Error", "Please select a course to delete!");
        }
    }

    // Update the selected course
    private void updateCourse() {
        Course selectedCourse = courseTableView.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            selectedCourse.setTitle(titleField.getText());
            selectedCourse.setDescription(descriptionField.getText());
            selectedCourse.setPdfPath(selectedPdfPath);
            courseService.updateCourse(selectedCourse);
            loadCourses();  // Reload the course list
            showAlert("Success", "Course updated successfully!");
        } else {
            showAlert("Error", "Please select a course to update!");
        }
    }

    // Open a file chooser to select a PDF file
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            selectedPdfPath = selectedFile.getAbsolutePath();
            pdfPathField.setText(selectedPdfPath);
        }
    }

    // Show alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
