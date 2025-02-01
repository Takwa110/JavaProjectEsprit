package org.esprit.projets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.esprit.projets.entity.Commentaire;
import org.esprit.projets.entity.Cours;
import org.esprit.projets.services.CommentaireService;
import org.esprit.projets.services.CoursService;
import org.esprit.projets.services.LikesService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class UserController extends MainController {

    @FXML private TableView<Cours> tableCourses;
    @FXML private TableColumn<Cours, String> colCourse;
    @FXML private TableColumn<Cours, Integer> colLikes;
    @FXML private TableColumn<Cours, Button> colAction;

    @FXML private TableView<Commentaire> tableComments;
    @FXML private TableColumn<Commentaire, String> colUser;
    @FXML private TableColumn<Commentaire, String> colComment;
    @FXML private TableColumn<Commentaire, Void> colActions;

    @FXML private TextArea commentTextArea;
    @FXML private Button submitCommentButton;

    private final CoursService courseService = new CoursService();
    private final LikesService likeService = new LikesService();
    private final CommentaireService commentService = new CommentaireService();

    // For demonstration, we assume the current user has ID 1.
    private final int currentUserId = 1;

    @FXML
    public void initialize() {
        // Setup course table columns
        colCourse.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colLikes.setCellValueFactory(cellData -> cellData.getValue().likesProperty().asObject());

        // Make table columns fill available width
        tableCourses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Setup the Like/Unlike button column
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button();
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Cours course = getTableRow() != null ? getTableRow().getItem() : null;
                    if (course != null) {
                        try {
                            button.setText(likeService.hasLiked(currentUserId, course.getId()) ? "Unlike" : "Like");
                        } catch (SQLException e) {
                            button.setText("Like");
                        }
                        button.setOnAction(event -> {
                            try {
                                likeCourse(course);
                                button.setText(likeService.hasLiked(currentUserId, course.getId()) ? "Unlike" : "Like");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        setGraphic(button);
                    }
                }
            }
        });

        // Setup comment table columns
        colUser.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("contenu"));

        // Ensure comments table resizes columns evenly
        tableComments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Setup the Actions column with Edit and Delete buttons.
        colActions.setCellFactory(param -> new TableCell<>() {
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
                if (empty) {
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

        // Load courses and comments
        try {
            loadCourses();
            loadAllComments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Listen for course selection changes:
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
        ObservableList<Cours> courses = FXCollections.observableArrayList(courseService.getAllCours());
        tableCourses.setItems(courses);
    }

    private void loadAllComments() throws SQLException {
        ObservableList<Commentaire> comments = FXCollections.observableArrayList(commentService.getAllComments());
        tableComments.setItems(comments);
    }

    private void loadCommentsByCourse(int courseId) throws SQLException {
        ObservableList<Commentaire> comments = FXCollections.observableArrayList(commentService.getCommentairesByCours(courseId));
        tableComments.setItems(comments);
    }

    private void likeCourse(Cours course) throws SQLException {
        if (likeService.hasLiked(currentUserId, course.getId())) {
            likeService.removeLike(currentUserId, course.getId());
            courseService.updateLikeCount(course.getId(), -1);
        } else {
            likeService.addLike(currentUserId, course.getId());
            courseService.updateLikeCount(course.getId(), 1);
        }
        loadCourses();
    }

    @FXML
    private void submitComment() throws SQLException {
        String commentText = commentTextArea.getText().trim();

        if (!commentText.isEmpty() && tableCourses.getSelectionModel().getSelectedItem() != null) {
            Cours selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
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
                    Cours selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
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
                Cours selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
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

    public void goBack(ActionEvent event) throws IOException {
        switchScene(event, "MainView.fxml");
    }
}
