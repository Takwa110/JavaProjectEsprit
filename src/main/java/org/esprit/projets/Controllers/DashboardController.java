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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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
        try {
            int totalCours = coursesList.size();
            int totalLikes = coursesList.stream().mapToInt(c -> c.getLikes()).sum();
            List<Commentaire> tousCommentaires = commentService.getAllComments();
            int totalCommentaires = tousCommentaires.size();
            PDPage pageTitre = new PDPage();
            document.addPage(pageTitre);
            PDRectangle boxTitre = pageTitre.getMediaBox();
            float largeurPage = boxTitre.getWidth();
            float hauteurPage = boxTitre.getHeight();
            PDPageContentStream contenuTitre = new PDPageContentStream(document, pageTitre);
            String texteTitre = "                             Rapport sur les cours et commentaires";
            contenuTitre.beginText();
            contenuTitre.setFont(PDType1Font.HELVETICA_BOLD, 22);
            float largeurTexteTitre = PDType1Font.HELVETICA_BOLD.getStringWidth(texteTitre) / 1000 * 36;
            float debutTitreX = (largeurPage - largeurTexteTitre) / 2;
            float debutTitreY = hauteurPage - 100;
            contenuTitre.newLineAtOffset(debutTitreX, debutTitreY);
            contenuTitre.showText(texteTitre);
            contenuTitre.endText();
            String dateTexte = "Généré le : " + java.time.LocalDate.now().toString();
            contenuTitre.beginText();
            contenuTitre.setFont(PDType1Font.HELVETICA, 14);
            float largeurDate = PDType1Font.HELVETICA.getStringWidth(dateTexte) / 1000 * 14;
            float debutDateX = (largeurPage - largeurDate) / 2;
            contenuTitre.newLineAtOffset(debutDateX, debutTitreY - 50);
            contenuTitre.showText(dateTexte);
            contenuTitre.endText();
            String totalCoursTexte = "Nombre total de cours : " + totalCours;
            contenuTitre.beginText();
            contenuTitre.setFont(PDType1Font.HELVETICA, 14);
            float largeurTotalCours = PDType1Font.HELVETICA.getStringWidth(totalCoursTexte) / 1000 * 14;
            float debutTotalCoursX = (largeurPage - largeurTotalCours) / 2;
            contenuTitre.newLineAtOffset(debutTotalCoursX, debutTitreY - 80);
            contenuTitre.showText(totalCoursTexte);
            contenuTitre.endText();
            String totalCommentairesTexte = "Nombre total de commentaires : " + totalCommentaires;
            contenuTitre.beginText();
            contenuTitre.setFont(PDType1Font.HELVETICA, 14);
            float largeurTotalCommentaires = PDType1Font.HELVETICA.getStringWidth(totalCommentairesTexte) / 1000 * 14;
            float debutTotalCommentairesX = (largeurPage - largeurTotalCommentaires) / 2;
            contenuTitre.newLineAtOffset(debutTotalCommentairesX, debutTitreY - 100);
            contenuTitre.showText(totalCommentairesTexte);
            contenuTitre.endText();
            String totalLikesTexte = "Nombre total de likes : " + totalLikes;
            contenuTitre.beginText();
            contenuTitre.setFont(PDType1Font.HELVETICA, 14);
            float largeurTotalLikes = PDType1Font.HELVETICA.getStringWidth(totalLikesTexte) / 1000 * 14;
            float debutTotalLikesX = (largeurPage - largeurTotalLikes) / 2;
            contenuTitre.newLineAtOffset(debutTotalLikesX, debutTitreY - 120);
            contenuTitre.showText(totalLikesTexte);
            contenuTitre.endText();
            contenuTitre.close();
            for (Course course : coursesList) {
                PDPage pageCours = new PDPage();
                document.addPage(pageCours);
                PDRectangle boxCours = pageCours.getMediaBox();
                float largeurCours = boxCours.getWidth();
                float hauteurCours = boxCours.getHeight();
                float marge = 50;
                float y = hauteurCours - marge;
                PDPageContentStream contenuCours = new PDPageContentStream(document, pageCours);
                float hauteurEnTete = 40;
                contenuCours.setNonStrokingColor(0.2f, 0.4f, 0.8f);
                contenuCours.addRect(marge, y - hauteurEnTete, largeurCours - 2 * marge, hauteurEnTete);
                contenuCours.fill();
                contenuCours.beginText();
                contenuCours.setNonStrokingColor(1, 1, 1);
                contenuCours.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contenuCours.newLineAtOffset(marge + 10, y - 30);
                String texteEnTete = "Cours : " + course.getTitle() + " | Likes : " + course.getLikes();
                contenuCours.showText(texteEnTete);
                contenuCours.endText();
                y = y - hauteurEnTete - 20;
                contenuCours.beginText();
                contenuCours.setNonStrokingColor(0, 0, 0);
                contenuCours.setFont(PDType1Font.HELVETICA, 14);
                contenuCours.newLineAtOffset(marge, y);
                String instructeur = (course.getInstructor() != null && !course.getInstructor().isEmpty()) ? course.getInstructor() : "N/A";
                String texteInfo = "Description : " + course.getDescription() + " | Instructeur : " + instructeur;
                contenuCours.showText(texteInfo);
                contenuCours.endText();
                y = y - 30;
                contenuCours.beginText();
                contenuCours.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contenuCours.newLineAtOffset(marge, y);
                contenuCours.showText("Commentaires :");
                contenuCours.endText();
                y = y - 20;
                float utilisateurX = marge;
                float commentaireX = utilisateurX + 110;
                float dateX = commentaireX + 310;
                contenuCours.beginText();
                contenuCours.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contenuCours.newLineAtOffset(utilisateurX, y);
                contenuCours.showText("Utilisateur");
                contenuCours.endText();
                contenuCours.beginText();
                contenuCours.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contenuCours.newLineAtOffset(commentaireX, y);
                contenuCours.showText("Commentaire");
                contenuCours.endText();
                contenuCours.beginText();
                contenuCours.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contenuCours.newLineAtOffset(dateX, y);
                contenuCours.showText("Date");
                contenuCours.endText();
                y = y - 15;
                List<Commentaire> commentaires = commentService.getCommentairesByCours(course.getId());
                if (commentaires.isEmpty()) {
                    contenuCours.beginText();
                    contenuCours.setFont(PDType1Font.HELVETICA, 12);
                    contenuCours.newLineAtOffset(marge, y);
                    contenuCours.showText("Aucun commentaire.");
                    contenuCours.endText();
                    y = y - 15;
                } else {
                    for (Commentaire c : commentaires) {
                        if (y < marge + 50) {
                            contenuCours.close();
                            PDPage nouvellePage = new PDPage();
                            document.addPage(nouvellePage);
                            contenuCours = new PDPageContentStream(document, nouvellePage);
                            y = nouvellePage.getMediaBox().getHeight() - marge;
                            contenuCours.beginText();
                            contenuCours.setFont(PDType1Font.HELVETICA_BOLD, 12);
                            contenuCours.newLineAtOffset(utilisateurX, y);
                            contenuCours.showText("Utilisateur");
                            contenuCours.endText();
                            contenuCours.beginText();
                            contenuCours.setFont(PDType1Font.HELVETICA_BOLD, 12);
                            contenuCours.newLineAtOffset(commentaireX, y);
                            contenuCours.showText("Commentaire");
                            contenuCours.endText();
                            contenuCours.beginText();
                            contenuCours.setFont(PDType1Font.HELVETICA_BOLD, 12);
                            contenuCours.newLineAtOffset(dateX, y);
                            contenuCours.showText("Date");
                            contenuCours.endText();
                            y = y - 15;
                        }
                        contenuCours.beginText();
                        contenuCours.setFont(PDType1Font.HELVETICA, 12);
                        contenuCours.newLineAtOffset(utilisateurX, y);
                        contenuCours.showText(c.getUserEmail());
                        contenuCours.endText();
                        contenuCours.beginText();
                        contenuCours.setFont(PDType1Font.HELVETICA, 12);
                        contenuCours.newLineAtOffset(commentaireX, y);
                        String texteCommentaire = c.getContenu();
                        String[] tokens = texteCommentaire.split(" ");
                        for (int i = 0; i < tokens.length; i++) {
                            String token = tokens[i];
                            String tokenNettoye = token.replaceAll("[^a-zA-Z]", "").toLowerCase();
                            if (isBadWord(tokenNettoye)) {
                                contenuCours.setNonStrokingColor(1, 0, 0);
                            } else {
                                contenuCours.setNonStrokingColor(0, 0, 0);
                            }
                            contenuCours.showText(token);
                            if (i < tokens.length - 1) {
                                contenuCours.showText(" ");
                            }
                        }
                        contenuCours.endText();
                        contenuCours.beginText();
                        contenuCours.setFont(PDType1Font.HELVETICA, 12);
                        contenuCours.setNonStrokingColor(0, 0, 0);
                        contenuCours.newLineAtOffset(dateX, y);
                        contenuCours.showText(c.getDate());
                        contenuCours.endText();
                        y = y - 15;
                    }
                }
                contenuCours.close();
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                document.save(file);
                showAlert("Succès", "PDF exporté avec succès !");
            }
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'exportation du PDF : " + ex.getMessage());
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isBadWord(String token) {
        if (token == null) {
            return false;
        }
        for (String bad : badWords) {
            if (token.equals(bad)) {
                return true;
            }
        }
        return false;
    }

}
