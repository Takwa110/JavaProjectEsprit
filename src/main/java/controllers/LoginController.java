package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import services.UtilisateurService;
import entity.Admin;
import entity.Enseignant;
import entity.Etudiant;
import entity.Utilisateur;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            Utilisateur utilisateur = utilisateurService.getByEmail(email);
            if (utilisateur != null && utilisateur.getMotDePasse().equals(password)) {
                // Redirection en fonction du rôle
                if (utilisateur instanceof Admin) {
                    redirectToPage("/AdminDashboard.fxml", "Tableau de bord Admin");
                } else if (utilisateur instanceof Enseignant) {
                    redirectToPage("EnseignantDashboard.fxml", "Tableau de bord Enseignant");
                } else if (utilisateur instanceof Etudiant) {
                    redirectToPage("EtudiantDashboard.fxml", "Tableau de bord Étudiant");
                }
            } else {
                showAlert("Erreur de connexion", "Email ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la connexion.");
        }
    }

    @FXML
    protected void handleCreateAccountButtonAction(ActionEvent event) {



        redirectToPage("/CreateAccountPage.fxml", "Créer un compte");
    }

    private void redirectToPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page : " + fxmlPath);
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

