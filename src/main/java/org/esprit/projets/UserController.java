package org.esprit.projets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;
import org.esprit.projets.entity.Commentaire;
import org.esprit.projets.entity.Course;
import org.esprit.projets.services.CommentaireService;
import org.esprit.projets.services.CoursService;
import org.esprit.projets.services.LikesService;
import org.esprit.projets.services.CourseService;
import java.sql.SQLException;
import java.util.Optional;

public class UserController extends MainController {


    @FXML private TableView<Course> tableCourses;
    @FXML private TableColumn<Course, String> idColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, String> descriptionColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> pdfPathColumn;
    @FXML private TableColumn<Course, String> likesColumn;
    @FXML private TableColumn<Course, Void> actionColumn;

    @FXML private TableView<Commentaire> tableComments;
    @FXML private TableColumn<Commentaire, String> colUser;
    @FXML private TableColumn<Commentaire, String> colComment;
    @FXML private TableColumn<Commentaire, Void> colActions;

    @FXML private TextArea commentTextArea;
    @FXML private Button submitCommentButton;

    private final CoursService coursService = new CoursService();
    private final CourseService courseService = new CourseService();
    private final LikesService likeService = new LikesService();
    private final CommentaireService commentService = new CommentaireService();

    private final int currentUserId = 2;
    private final ObservableList<Course> coursesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        titleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle() != null ? cellData.getValue().getTitle() : ""));
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription() != null ? cellData.getValue().getDescription() : ""));
        instructorColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getInstructor() != null ? cellData.getValue().getInstructor() : ""));
        pdfPathColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPdfPath() != null ? cellData.getValue().getPdfPath() : ""));
        likesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getLikes())));
        tableCourses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        actionColumn.setCellFactory(new Callback<TableColumn<Course, Void>, TableCell<Course, Void>>() {
            @Override
            public TableCell<Course, Void> call(TableColumn<Course, Void> param) {
                return new TableCell<Course, Void>() {
                    private final Button btn = new Button();
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            Course course = getTableRow().getItem();
                            btn.setText(likeService.hasLiked(currentUserId, course.getId()) ? "Unlike" : "Like");
                            btn.setOnAction(event -> {
                                try {
                                    if (likeService.hasLiked(currentUserId, course.getId())) {
                                        likeService.removeLike(currentUserId, course.getId());
                                        coursService.updateLikeCount(course.getId(), -1);
                                    } else {
                                        likeService.addLike(currentUserId, course.getId());
                                        coursService.updateLikeCount(course.getId(), 1);
                                    }
                                    loadCourses();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            setGraphic(btn);
                        }
                    }
                };
            }
        });

        // --- Setup Comments Table Columns ---
        colUser.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        tableComments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Setup the Actions Column (Edit/Delete for Comments) ---
        colActions.setCellFactory(param -> new TableCell<Commentaire, Void>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);
            {
                btnEdit.setOnAction(event -> {
                    Commentaire comment = getTableView().getItems().get(getIndex());
                    editComment(comment);
                });
                btnDelete.setOnAction(event -> {
                    Commentaire comment = getTableView().getItems().get(getIndex());
                    deleteComment(comment);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().size() <= getIndex()) {
                    setGraphic(null);
                } else {
                    Commentaire comment = getTableView().getItems().get(getIndex());
                    if (comment.getId_utilisateur() == currentUserId) {
                        btnEdit.setDisable(false);
                        btnDelete.setDisable(false);
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // --- Load Courses and Comments ---
        try {
            loadCourses();
            loadAllComments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // --- Load comments for selected course ---
        tableCourses.getSelectionModel().selectedItemProperty().addListener((obs, oldCourse, newCourse) -> {
            try {
                if (newCourse != null) {
                    loadCommentsByCourse(newCourse.getId());
                } else {
                    loadAllComments();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadCourses() throws SQLException {
        ObservableList<Course> courses = FXCollections.observableArrayList(courseService.getAllCourses());
        tableCourses.setItems(courses);
    }

    private void loadAllComments() throws SQLException {
        ObservableList<Commentaire> comments = FXCollections.observableArrayList(commentService.getAllComments());
        tableComments.setItems(comments);
    }

    private void loadCommentsByCourse(int courseId) throws SQLException {
        ObservableList<Commentaire> comments = FXCollections.observableArrayList(
                commentService.getCommentairesByCours(courseId));
        tableComments.setItems(comments);
    }

    private void likeCourse(Course course) throws SQLException {
        if (likeService.hasLiked(currentUserId, course.getId())) {
            likeService.removeLike(currentUserId, course.getId());
            coursService.updateLikeCount(course.getId(), -1);
        } else {
            likeService.addLike(currentUserId, course.getId());
            coursService.updateLikeCount(course.getId(), 1);
        }
        loadCourses();
    }

    @FXML
    private void submitComment() throws SQLException {
        String commentText = commentTextArea.getText().trim();
        if (!commentText.isEmpty() && tableCourses.getSelectionModel().getSelectedItem() != null) {
            Course selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
            Commentaire comm = new Commentaire(0, currentUserId, selectedCourse.getId(), commentText, null);
            commentService.addCommentaire(comm);
            commentTextArea.clear();
            loadCommentsByCourse(selectedCourse.getId());
        } else if (!commentText.isEmpty()) {
            showAlert("No Course Selected", "Please select a course to comment on.");
            commentTextArea.clear();
        }
    }

    private void editComment(Commentaire comment) {
        TextInputDialog dialog = new TextInputDialog(comment.getContenu());
        dialog.setTitle("Edit Comment");
        dialog.setHeaderText("Edit your comment:");
        dialog.setContentText("Comment:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newContent -> {
            if (!newContent.trim().isEmpty()) {
                comment.setContenu(newContent.trim());
                try {
                    commentService.updateCommentaire(comment);
                    Course selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
                    if (selectedCourse != null) {
                        loadCommentsByCourse(selectedCourse.getId());
                    } else {
                        loadAllComments();
                    }
                } catch (SQLException e) {
                    showAlert("Error", "Could not update comment: " + e.getMessage());
                }
            }
        });
    }

    private void deleteComment(Commentaire comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Comment");
        alert.setHeaderText("Are you sure you want to delete this comment?");
        alert.setContentText(comment.getContenu());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                commentService.deleteCommentaire(comment.getId());
                Course selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
                if (selectedCourse != null) {
                    loadCommentsByCourse(selectedCourse.getId());
                } else {
                    loadAllComments();
                }
            } catch (SQLException e) {
                showAlert("Error", "Could not delete comment: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
