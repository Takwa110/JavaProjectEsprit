package org.esprit.projets.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.esprit.projets.entity.Admin;
import org.esprit.projets.entity.Enseignant;
import org.esprit.projets.entity.Etudiant;
import org.esprit.projets.entity.Utilisateur;
import org.esprit.projets.services.UtilisateurService;

import java.io.IOException;
import java.sql.SQLException;

public class CreateAccountController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField specialiteField;

    @FXML
    private TextField niveauField;

    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        // Initialiser la ComboBox avec les rôles
        roleComboBox.getItems().addAll("Admin", "Enseignant", "Etudiant");

        // Activer/désactiver les champs en fonction du rôle sélectionné
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            specialiteField.setDisable(!newValue.equals("Enseignant"));
            niveauField.setDisable(!newValue.equals("Etudiant"));
        });
    }


    @FXML
    protected void handleCreateAccountButtonAction(ActionEvent event) {
        String nom = nomField.getText();
        String email = emailField.getText();
        String motDePasse = passwordField.getText();
        String role = roleComboBox.getValue();
        String specialite = specialiteField.getText();
        String niveau = niveauField.getText();

        try {
            // Validation des champs
            if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || role == null) {
                showAlert("Erreur", "Tous les champs sont obligatoires !");
                return;
            }

            // Validation email
            if (!isValidEmail(email)) {
                showAlert("Erreur", "Veuillez entrer un email valide !");
                return;
            }

            // Validation mot de passe
            if (!isValidPassword(motDePasse)) {
                showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères et un chiffre !");
                return;
            }

            // Créer l'utilisateur en fonction du rôle
            Utilisateur utilisateur;
            switch (role) {
                case "Admin":
                    utilisateur = new Admin(nom, email, motDePasse);
                    break;
                case "Enseignant":
                    if (specialite.isEmpty()) {
                        showAlert("Erreur", "La spécialité est obligatoire pour un enseignant !");
                        return;
                    }
                    utilisateur = new Enseignant(nom, email, motDePasse, specialite);
                    break;
                case "Etudiant":
                    if (niveau.isEmpty()) {
                        showAlert("Erreur", "Le niveau est obligatoire pour un étudiant !");
                        return;
                    }
                    utilisateur = new Etudiant(nom, email, motDePasse, niveau);
                    break;
                default:
                    throw new IllegalArgumentException("Rôle invalide");
            }

            // Ajouter l'utilisateur
            utilisateurService.ajouter(utilisateur);

            // Afficher un message de succès
            showAlert("Succès", "Compte créé avec succès !");

            // Retour à la page de connexion
            handleBackButtonAction(event);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la création du compte.");
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    protected void handleBackButtonAction(ActionEvent event) {
        try {
            // Charger la page de connexion
            Parent root = FXMLLoader.load(getClass().getResource("/org/esprit/projets/LoginPage.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*\\d.*");
    }

    private void showAlert(String title, String message) {
        // Afficher une alerte avec un message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
