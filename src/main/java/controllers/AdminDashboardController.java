package controllers;

import entity.Utilisateur;
import services.UtilisateurService;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import entity.Admin;
import entity.Enseignant;


import java.sql.SQLException;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private TableView<Utilisateur> tableView;
    @FXML
    private TableColumn<Utilisateur, String> colNom;
    @FXML
    private TableColumn<Utilisateur, String> colEmail;
    @FXML
    private TableColumn<Utilisateur, String> colRole;
    @FXML
    private TableColumn<Utilisateur, String> colAction;
    @FXML
    private Button btnAjouter;

    private UtilisateurService utilisateurService = new UtilisateurService();

    public void initialize() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.getAll();
            tableView.getItems().setAll(utilisateurs);

            // Associer les valeurs aux colonnes en utilisant SimpleStringProperty
            colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
            colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            colRole.setCellValueFactory(cellData -> {
                if (cellData.getValue() instanceof Admin) {
                    return new SimpleStringProperty("Admin");
                } else if (cellData.getValue() instanceof Enseignant) {
                    return new SimpleStringProperty("Enseignant");
                } else {
                    return new SimpleStringProperty("Etudiant");
                }
            });

            // Cellule pour les actions
            colAction.setCellFactory(new Callback<TableColumn<Utilisateur, String>, TableCell<Utilisateur, String>>() {
                @Override
                public TableCell<Utilisateur, String> call(TableColumn<Utilisateur, String> param) {
                    return new TableCell<Utilisateur, String>() {
                        final Button btnUpdate = new Button("Update");
                        final Button btnDelete = new Button("Delete");

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btnUpdate);
                                btnUpdate.setOnAction(e -> updateUtilisateur(getTableRow().getItem()));

                                setGraphic(btnDelete);
                                btnDelete.setOnAction(e -> deleteUtilisateur(getTableRow().getItem()));
                            }
                        }
                    };
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUtilisateur(Utilisateur utilisateur) {
        // Logique pour mettre à jour un utilisateur (changer son rôle, etc.)
    }

    private void deleteUtilisateur(Utilisateur utilisateur) {
        try {
            utilisateurService.supprimer(utilisateur);
            tableView.getItems().remove(utilisateur);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
