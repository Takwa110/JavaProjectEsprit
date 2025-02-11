package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import services.UtilisateurService;
import entity.Utilisateur;
import entity.Admin;
import entity.Enseignant;
import entity.Etudiant;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.sql.SQLException;
import java.io.IOException;

import javafx.scene.input.KeyEvent;


public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label error;

    private UtilisateurService utilisateurService = new UtilisateurService();


    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            error.setText("Veuillez remplir tous les champs.");
            return; // Si un des champs est vide, on arrête l'exécution
        }

        if (!isValidEmail(email)) {
            error.setText("Veuillez entrer un email valide.");
            return; // Si l'email n'est pas valide, on arrête l'exécution
        }

        try {
            Utilisateur utilisateur = utilisateurService.getByEmail(email);

            if (utilisateur == null) {
                error.setText("Aucun compte trouvé pour cet email.");
            } else if (!utilisateur.getMotDePasse().equals(password)) {
                error.setText("Email ou mot de passe incorrect.");
            } else {
                // Connexion réussie, rediriger selon le type d'utilisateur
                if (utilisateur instanceof Admin) {
                    redirectToPage("/AdminDashboard.fxml", "Tableau de bord Admin");
                } else if (utilisateur instanceof Enseignant) {
                    redirectToPage("/EnseignantDashboard.fxml", "Tableau de bord Enseignant");
                } else if (utilisateur instanceof Etudiant) {
                    redirectToPage("/EtudiantDashboard.fxml", "Tableau de bord Étudiant");
                }
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


    @FXML
    protected void handleKeyReleased(KeyEvent event) {
        // Logique à exécuter lorsque l'utilisateur relâche une touche dans le champ texte
        System.out.println("Touche relâchée : " + event.getText());

        // Vous pouvez aussi ajouter des conditions comme vérifier si les champs sont vides
        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            // Afficher un message d'erreur ou effectuer une action spécifique
            System.out.println("Veuillez remplir tous les champs.");
        }
    }


    private boolean isValidEmail(String email) {
        // Expression régulière pour un format email simple
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }


    @FXML
    protected void handleForgotPasswordAction(ActionEvent event) {
        System.out.println("Bouton 'Réinitialiser le mot de passe' cliqué");

        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ForgotPasswordPage.fxml"));
            Parent root = loader.load();  // Charger la page FXML

            // Créer une nouvelle scène avec la racine chargée
            Scene scene = new Scene(root);

            // Récupérer le stage actuel
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Définir la nouvelle scène sur le stage
            stage.setScene(scene);
            stage.setTitle("Réinitialiser le mot de passe");
            stage.show();  // Afficher la nouvelle scène
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
