package org.esprit.projets.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.esprit.projets.entity.Admin;
import org.esprit.projets.entity.Enseignant;
import org.esprit.projets.entity.Etudiant;
import org.esprit.projets.entity.Utilisateur;
import org.esprit.projets.services.UtilisateurService;

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
            // IMPORTANT : rendre la table éditable
            tableView.setEditable(true);

            // Chargement des utilisateurs dans la table
            List<Utilisateur> utilisateurs = utilisateurService.getAll();
            tableView.getItems().setAll(utilisateurs);

            // Associer les valeurs aux colonnes
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

            // Rendre les cellules éditables en utilisant TextFieldTableCell
            colNom.setCellFactory(TextFieldTableCell.forTableColumn());
            colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
            colRole.setCellFactory(TextFieldTableCell.forTableColumn());

            // Définir l'action lors de la validation de l'édition
            colNom.setOnEditCommit(event -> {
                updateUtilisateur(event.getRowValue(), "nom", event.getNewValue());
            });
            colEmail.setOnEditCommit(event -> {
                updateUtilisateur(event.getRowValue(), "email", event.getNewValue());
            });
            colRole.setOnEditCommit(event -> {
                updateUtilisateur(event.getRowValue(), "role", event.getNewValue());
            });

            // Configuration de la colonne Action avec deux boutons (Update et Delete)
            colAction.setCellFactory(new Callback<TableColumn<Utilisateur, String>, TableCell<Utilisateur, String>>() {
                @Override
                public TableCell<Utilisateur, String> call(TableColumn<Utilisateur, String> param) {
                    return new TableCell<Utilisateur, String>() {
                        final Button btnUpdate = new Button("Update");
                        final Button btnDelete = new Button("Delete");
                        final HBox container = new HBox(10, btnUpdate, btnDelete); // espacement de 10 entre les boutons

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                // Le bouton "Update" déclenche l'édition sur la colonne "nom" (vous pouvez adapter)
                                btnUpdate.setOnAction(e -> enableEdit(getTableRow().getItem()));
                                btnDelete.setOnAction(e -> deleteUtilisateur(getTableRow().getItem()));
                                setGraphic(container);
                            }
                        }
                    };
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Active l'édition d'une ligne (ici sur la colonne "nom").
     */
    private void enableEdit(Utilisateur utilisateur) {
        int rowIndex = tableView.getItems().indexOf(utilisateur);
        // Cette instruction démarre l'édition de la cellule "nom" pour la ligne concernée.
        tableView.edit(rowIndex, colNom);
    }

    /**
     * Méthode de mise à jour d'un utilisateur lorsqu'une cellule est modifiée.
     * @param utilisateur L'utilisateur à mettre à jour.
     * @param champ Le champ modifié ("nom", "email", "role").
     * @param nouvelleValeur La nouvelle valeur saisie.
     */
    private void updateUtilisateur(Utilisateur utilisateur, String champ, String nouvelleValeur) {
        if ("nom".equals(champ)) {
            utilisateur.setNom(nouvelleValeur);
        } else if ("email".equals(champ)) {
            utilisateur.setEmail(nouvelleValeur);
        } else if ("role".equals(champ)) {
            // Pour le rôle, recréation si nécessaire (attention aux propriétés spécifiques)
            if ("Admin".equals(nouvelleValeur)) {
                utilisateur = new Admin(utilisateur.getId(), utilisateur.getNom(), utilisateur.getEmail(), utilisateur.getMotDePasse());
            } else if ("Enseignant".equals(nouvelleValeur)) {
                // Ici, vous devez gérer la spécialité existante ou une valeur par défaut
                utilisateur = new Enseignant(utilisateur.getId(), utilisateur.getNom(), utilisateur.getEmail(), utilisateur.getMotDePasse(),
                        (utilisateur instanceof Enseignant) ? ((Enseignant) utilisateur).getSpecialite() : "");
            } else if ("Etudiant".equals(nouvelleValeur)) {
                // Ici, vous devez gérer le niveau existant ou une valeur par défaut
                utilisateur = new Etudiant(utilisateur.getId(), utilisateur.getNom(), utilisateur.getEmail(), utilisateur.getMotDePasse(),
                        (utilisateur instanceof Etudiant) ? ((Etudiant) utilisateur).getNiveau() : "");
            }
        }

        try {
            // Mise à jour dans la base de données
            utilisateurService.update(utilisateur);
            // Actualisation de la table pour refléter les modifications
            tableView.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode de suppression d'un utilisateur.
     */
    private void deleteUtilisateur(Utilisateur utilisateur) {
        try {
            utilisateurService.supprimer(utilisateur);
            tableView.getItems().remove(utilisateur);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
