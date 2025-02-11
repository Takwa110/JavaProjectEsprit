package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.UtilisateurService;
import services.EmailService;
import java.util.UUID;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private EmailService emailService = new EmailService();

    @FXML
    protected void handleSendResetLink() {
        String email = emailField.getText();

        // Vérifier si l'email est vide
        if (email.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre email.");
            return;
        }

        try {
            // Vérifier si l'utilisateur existe
            if (utilisateurService.getByEmail(email) == null) {
                showAlert("Erreur", "Aucun utilisateur trouvé avec cet email.");
                return;
            }

            // Générer un token pour la réinitialisation
            String token = UUID.randomUUID().toString();
            // Enregistrer le token et son expiration dans la base de données
            utilisateurService.saveResetToken(email, token);

            // Envoyer un email avec le lien de réinitialisation
            String resetLink = "http://votre-site.com/reset-password?token=" + token;
            String message = "Cliquez sur ce lien pour réinitialiser votre mot de passe : " + resetLink;
            emailService.sendEmail(email, "Réinitialisation de votre mot de passe", message);

            showAlert("Succès", "Un lien de réinitialisation a été envoyé à votre email.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue. Veuillez réessayer.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
