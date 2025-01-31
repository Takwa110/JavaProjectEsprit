package tn.esprit.cours;


import services.UtilisateurService;
import entity.Utilisateur;
import java.sql.SQLException;

public class TestUtilisateurService {
    public static void main(String[] args) {
        UtilisateurService us = new UtilisateurService();
        try {
            // Ajouter un utilisateur
            Utilisateur u = new Utilisateur("Ali Ben Salah", "ali@example.com", "password123");
            us.ajouter(u);

            // Récupérer tous les utilisateurs
            System.out.println(us.getAll());

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}