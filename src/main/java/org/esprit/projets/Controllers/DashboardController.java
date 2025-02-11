package org.esprit.projets.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.esprit.projets.entity.Course;
import org.esprit.projets.entity.Commentaire;
import org.esprit.projets.services.CoursService;
import org.esprit.projets.services.CourseService;
import org.esprit.projets.services.CommentaireService;
import org.esprit.projets.services.LikesService;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardController {

    private final CoursService coursService = new CoursService();
    private final CourseService courseService = new CourseService();
    private final ObservableList<Course> coursesList = FXCollections.observableArrayList();
    @FXML private TableView<Course> courseTableView;
    @FXML private TableColumn<Course, String> idColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, String> descriptionColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> pdfPathColumn;
    @FXML private TableColumn<Course, String> likesColumn;
    @FXML private TableColumn<Course, Void> actionColumn;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField instructorField;
    @FXML private TextField pdfPathField;
    @FXML private Button addCourseButton;
    @FXML private Button deleteCourseButton;
    @FXML private Button updateCourseButton;
    @FXML private Button chooseFileButton;
    @FXML private Button exportPdfButton;
    private String selectedPdfPath = "";
    private final CommentaireService commentService = new CommentaireService();
    private ObservableList<Commentaire> allComments;
    @FXML private TableView<Commentaire> tableComments;
    @FXML private TableColumn<Commentaire, String> colCourse;
    @FXML private TableColumn<Commentaire, String> colUser;
    @FXML private TableColumn<Commentaire, String> colComment;
    @FXML private TableColumn<Commentaire, String> colDate;
    @FXML private TableColumn<Commentaire, Void> colActions;
    private final LikesService likeService = new LikesService();
    @FXML private TableView<Course> tableLikes;
    @FXML private TableColumn<Course, String> colCourseLikes;
    @FXML private TableColumn<Course, Integer> colLikes;
    @FXML private TableColumn<Course, Void> colActionsLikes;
    @FXML private Button resetFilterButton;
    @FXML private Button filterBadWordsButton;
    private final String[] badWords = {"test", "fza"};

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle() != null ? cellData.getValue().getTitle() : ""));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription() != null ? cellData.getValue().getDescription() : ""));
        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInstructor() != null ? cellData.getValue().getInstructor() : ""));
        pdfPathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPdfPath() != null ? cellData.getValue().getPdfPath() : ""));
        likesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLikes())));
        courseTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        actionColumn.setCellFactory(new Callback<TableColumn<Course, Void>, TableCell<Course, Void>>() {
            @Override
            public TableCell<Course, Void> call(TableColumn<Course, Void> param) {
                return new TableCell<Course, Void>() {
                    private final Button btn = new Button();
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            Course course = getTableRow().getItem();
                            btn.setText(likeService.hasLiked(1, course.getId()) ? "Unlike" : "Like");
                            btn.setOnAction(event -> {
                                try {
                                    if(likeService.hasLiked(1, course.getId())) {
                                        likeService.removeLike(1, course.getId());
                                        coursService.updateLikeCount(course.getId(), -1);
                                    } else {
                                        likeService.addLike(1, course.getId());
                                        coursService.updateLikeCount(course.getId(), 1);
                                    }
                                    loadCourses();
                                } catch(SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
        addCourseButton.setOnAction(e -> addCourse());
        deleteCourseButton.setOnAction(e -> deleteCourse());
        updateCourseButton.setOnAction(e -> updateCourse());
        chooseFileButton.setOnAction(e -> chooseFile());
        exportPdfButton.setOnAction(e -> exportPdf());
        loadCourses();
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("text"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setCellFactory(column -> new TableCell<Commentaire, String>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });
        tableComments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addActionsToComments();
        tableComments.setRowFactory(tv -> new TableRow<Commentaire>() {
            @Override
            protected void updateItem(Commentaire item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setStyle("");
                } else if(containsBadWord(item.getContenu())) {
                    setStyle("-fx-background-color: tomato;");
                } else {
                    setStyle("");
                }
            }
        });
        colCourseLikes.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLikes.setCellValueFactory(new PropertyValueFactory<>("likes"));
        tableLikes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addFilterButtonToLikes();
        resetFilterButton.setOnAction(e -> resetFilter());
        filterBadWordsButton.setOnAction(e -> filterBadWords());
        loadComments();
        loadLikes();
    }

    private void loadCourses() {
        List<Course> courseList = courseService.getAllCourses();
        coursesList.clear();
        coursesList.addAll(courseList);
        courseTableView.setItems(coursesList);
    }

    private void addCourse() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String instructor = instructorField.getText();
        if(title.isEmpty() || description.isEmpty() || instructor.isEmpty() || selectedPdfPath.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }
        Course newCourse = new Course(title, description, instructor, selectedPdfPath, 0);
        courseService.addCourse(newCourse);
        loadCourses();
        showAlert("Success", "Course added successfully!");
    }

    private void deleteCourse() {
        Course selectedCourse = courseTableView.getSelectionModel().getSelectedItem();
        if(selectedCourse != null) {
            courseService.deleteCourse(selectedCourse.getId());
            loadCourses();
            showAlert("Success", "Course deleted successfully!");
        } else {
            showAlert("Error", "Please select a course to delete!");
        }
    }

    private void updateCourse() {
        Course selectedCourse = courseTableView.getSelectionModel().getSelectedItem();
        if(selectedCourse != null) {
            selectedCourse.setTitle(titleField.getText());
            selectedCourse.setDescription(descriptionField.getText());
            selectedCourse.setInstructor(instructorField.getText());
            selectedCourse.setPdfPath(selectedPdfPath);
            courseService.updateCourse(selectedCourse);
            loadCourses();
            showAlert("Success", "Course updated successfully!");
        } else {
            showAlert("Error", "Please select a course to update!");
        }
    }

    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null) {
            selectedPdfPath = selectedFile.getAbsolutePath();
            pdfPathField.setText(selectedPdfPath);
        }
    }

    private void loadComments() {
        try {
            List<Commentaire> comments = commentService.getAllComments();
            allComments = FXCollections.observableArrayList(comments);
            tableComments.setItems(allComments);
        } catch(SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load comments: " + e.getMessage());
        }
    }

    private void addActionsToComments() {
        Callback<TableColumn<Commentaire, Void>, TableCell<Commentaire, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Commentaire, Void> call(final TableColumn<Commentaire, Void> param) {
                return new TableCell<Commentaire, Void>() {
                    private final Button btnDelete = new Button("Supprimer");
                    { btnDelete.setOnAction(event -> {
                        Commentaire comment = getTableView().getItems().get(getIndex());
                        deleteComment(comment);
                    }); }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btnDelete);
                    }
                };
            }
        };
        colActions.setCellFactory(cellFactory);
    }

    private void deleteComment(Commentaire comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer le commentaire");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");
        alert.setContentText(comment.getContenu());
        alert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK) {
                try {
                    commentService.deleteCommentaire(comment.getId());
                    loadComments();
                } catch(SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer le commentaire : " + e.getMessage());
                }
            }
        });
    }

    private boolean containsBadWord(String text) {
        if(text == null) return false;
        String lowerText = text.toLowerCase();
        for(String bad : badWords) {
            if(lowerText.contains(bad)) return true;
        }
        return false;
    }

    private void filterCommentsByCourse(String courseName) {
        if(allComments == null) return;
        ObservableList<Commentaire> filtered = allComments.stream()
                .filter(c -> courseName.equals(c.getCourseName()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tableComments.setItems(filtered);
    }

    @FXML private void resetFilter() {
        if(allComments != null) tableComments.setItems(allComments);
    }

    @FXML private void filterBadWords() {
        if(allComments == null) return;
        ObservableList<Commentaire> filtered = allComments.stream()
                .filter(c -> containsBadWord(c.getContenu()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tableComments.setItems(filtered);
    }

    private void loadLikes() {
        List<Course> courses = likeService.getCoursesWithLikes();
        ObservableList<Course> observableList = FXCollections.observableArrayList(courses);
        tableLikes.setItems(observableList);
    }

    private void addFilterButtonToLikes() {
        Callback<TableColumn<Course, Void>, TableCell<Course, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Course, Void> call(final TableColumn<Course, Void> param) {
                return new TableCell<Course, Void>() {
                    private final Button btnFilter = new Button("Filtrer");
                    { btnFilter.setOnAction(event -> {
                        Course course = getTableView().getItems().get(getIndex());
                        filterCommentsByCourse(course.getTitle());
                    }); }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btnFilter);
                    }
                };
            }
        };
        colActionsLikes.setCellFactory(cellFactory);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void exportPdf() {
        PDDocument document = new PDDocument();
        float margin = 50, yStart = 750, lineHeight = 20;
        try {
            for(Course course : coursesList) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                float y = yStart;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset(margin, y);
                String courseHeader = "Course: " + course.getTitle() + " | Likes: " + course.getLikes();
                contentStream.showText(courseHeader);
                contentStream.endText();
                y -= lineHeight * 2;
                String courseInfo = "Description: " + course.getDescription() + " | Instructor: " + course.getInstructor() != "" ? course.getInstructor() : " ";
                y -= lineHeight * 2;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(margin, y);
                contentStream.showText(courseInfo);
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(margin, y);
                contentStream.showText("Comments:");
                contentStream.endText();
                y -= lineHeight;
                List<Commentaire> comments = commentService.getCommentairesByCours(course.getId());
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                if(comments.isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, y);
                    contentStream.showText("No comments.");
                    contentStream.endText();
                    y -= lineHeight;
                } else {
                    // Write a header row for the comments table
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(margin, y);
                    String headerRow = String.format("%-25s %-50s %-20s", "User", "Comment", "Date");
                    contentStream.showText(headerRow);
                    contentStream.endText();
                    y -= lineHeight;
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    for(Commentaire c : comments) {
                        if(y < margin) {
                            contentStream.close();
                            PDPage newPage = new PDPage();
                            document.addPage(newPage);
                            contentStream = new PDPageContentStream(document, newPage);
                            y = yStart;
                        }
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, y);
                        String commentLine = String.format("%-25s %-50s %-20s",
                                c.getUserEmail(), c.getContenu(), c.getDate());
                        contentStream.showText(commentLine);
                        contentStream.endText();
                        y -= lineHeight;
                    }
                }
                contentStream.close();
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);
            if(file != null) {
                document.save(file);
                showAlert("Success", "PDF exported successfully!");
            }
            document.close();
        } catch(IOException | SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Error exporting PDF: " + ex.getMessage());
        }
    }
}
