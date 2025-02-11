package org.esprit.projets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import org.esprit.projets.entity.Commentaire;
import org.esprit.projets.entity.Course;
import org.esprit.projets.services.CommentaireService;
import org.esprit.projets.services.LikesService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AdminController {

    // Comments Table
    @FXML private TableView<Commentaire> tableComments;
    @FXML private TableColumn<Commentaire, String> colCourse;
    @FXML private TableColumn<Commentaire, String> colUser;
    @FXML private TableColumn<Commentaire, String> colComment;
    @FXML private TableColumn<Commentaire, String> colDate;
    @FXML private TableColumn<Commentaire, Void> colActions;

    // Likes Table
    @FXML private TableView<Course> tableLikes;
    @FXML private TableColumn<Course, String> colCourseLikes;
    @FXML private TableColumn<Course, Integer> colLikes;
    @FXML private TableColumn<Course, Void> colActionsLikes;

    // Filter Buttons
    @FXML private Button resetFilterButton;
    @FXML private Button filterBadWordsButton;

    private final CommentaireService commentService = new CommentaireService();
    private final LikesService likeService = new LikesService();

    // Store all comments to restore after filtering.
    private ObservableList<Commentaire> allComments;

    // Define a simple list of bad words.
    private final String[] badWords = {"test","fza"};

    @FXML
    public void initialize() {
        // Setup comments table columns
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("text"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Format the date column
        colDate.setCellFactory(column -> new TableCell<Commentaire, String>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });

        // Constrained resize for tableComments
        tableComments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add Delete button column for comments.
        addActionsToComments();

        // Highlight rows containing bad words.
        tableComments.setRowFactory(tv -> new TableRow<Commentaire>() {
            @Override
            protected void updateItem(Commentaire item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (containsBadWord(item.getContenu())) {
                    setStyle("-fx-background-color: tomato;");
                } else {
                    setStyle("");
                }
            }
        });

        // Setup likes table columns
        colCourseLikes.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLikes.setCellValueFactory(new PropertyValueFactory<>("likes"));

        // Constrained resize for tableLikes
        tableLikes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add a button column for filtering comments by course.
        addFilterButtonToLikes();

        // Load data into both tables.
        loadComments();
        loadLikes();
    }

    private void addActionsToComments() {
        Callback<TableColumn<Commentaire, Void>, TableCell<Commentaire, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Commentaire, Void> call(final TableColumn<Commentaire, Void> param) {
                return new TableCell<Commentaire, Void>() {
                    private final Button btnDelete = new Button("Supprimer");
                    {
                        btnDelete.setOnAction(event -> {
                            Commentaire comment = getTableView().getItems().get(getIndex());
                            deleteComment(comment);
                        });
                    }
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

    private void addFilterButtonToLikes() {
        Callback<TableColumn<Course, Void>, TableCell<Course, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Course, Void> call(final TableColumn<Course, Void> param) {
                return new TableCell<Course, Void>() {
                    private final Button btnFilter = new Button("Filtrer");
                    {
                        btnFilter.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            filterCommentsByCourse(course.getTitle());
                        });
                    }
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

    private boolean containsBadWord(String text) {
        if (text == null) return false;
        String lowerText = text.toLowerCase();
        for (String bad : badWords) {
            if (lowerText.contains(bad)) {
                return true;
            }
        }
        return false;
    }

    private void loadComments() {
        try {
            List<Commentaire> comments = commentService.getAllComments();
            allComments = FXCollections.observableArrayList(comments);
            tableComments.setItems(allComments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadLikes() {

            List<Course> courses = likeService.getCoursesWithLikes();
            ObservableList<Course> observableList = FXCollections.observableArrayList(courses);
            tableLikes.setItems(observableList);
    }

    private void deleteComment(Commentaire comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer le commentaire");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");
        alert.setContentText(comment.getContenu());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    commentService.deleteCommentaire(comment.getId());
                    loadComments();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer le commentaire : " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Filters the comments table to only include comments from the specified course.
     */
    private void filterCommentsByCourse(String courseName) {
        if (allComments == null) return;
        ObservableList<Commentaire> filtered = allComments.stream()
                .filter(c -> courseName.equals(c.getCourseName()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tableComments.setItems(filtered);
    }

    /**
     * Resets the comments table to display all comments.
     */
    @FXML
    private void resetFilter() {
        if (allComments != null) {
            tableComments.setItems(allComments);
        }
    }

    /**
     * Filters the comments table to display only comments containing bad words.
     */
    @FXML
    private void filterBadWords() {
        if (allComments == null) return;
        ObservableList<Commentaire> filtered = allComments.stream()
                .filter(c -> containsBadWord(c.getContenu()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tableComments.setItems(filtered);
    }
}
