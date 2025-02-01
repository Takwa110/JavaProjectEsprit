package org.esprit.projets.services;

import org.esprit.projets.entity.Commentaire;
import org.esprit.projets.services.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService {
    private final Connection conn = DBConnection.getConnection();

    public void addCommentaire(Commentaire commentaire) throws SQLException {
        String query = "INSERT INTO commentaires (utilisateur_id, cours_id, contenu) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, commentaire.getId_utilisateur());
            stmt.setInt(2, commentaire.getId_cours());
            stmt.setString(3, commentaire.getContenu());
            stmt.executeUpdate();
        }
    }

    public List<Commentaire> getAllComments() throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT commentaires.*, " +
                "cours.nom AS courseName, " +
                "utilisateur.email AS userEmail " +
                "FROM commentaires " +
                "JOIN cours ON cours.id = commentaires.cours_id " +
                "JOIN utilisateur ON utilisateur.id = commentaires.utilisateur_id";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("date_creation");
                commentaires.add(new Commentaire(
                        rs.getInt("id"),
                        rs.getInt("utilisateur_id"),
                        rs.getInt("cours_id"),
                        rs.getString("contenu"),
                        ts != null ? ts.toLocalDateTime() : null,
                        rs.getString("courseName"),
                        rs.getString("userEmail")
                ));
            }
        }
        return commentaires;
    }

    public List<Commentaire> getCommentairesByCours(int idCours) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT commentaires.*, " +
                "cours.nom AS courseName, " +
                "utilisateur.email AS userEmail " +
                "FROM commentaires " +
                "JOIN cours ON cours.id = commentaires.cours_id " +
                "JOIN utilisateur ON utilisateur.id = commentaires.utilisateur_id"+
                  " WHERE cours_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCours);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("date_creation");
                commentaires.add(new Commentaire(
                        rs.getInt("id"),
                        rs.getInt("utilisateur_id"),
                        rs.getInt("cours_id"),
                        rs.getString("contenu"),
                        ts != null ? ts.toLocalDateTime() : null,
                        rs.getString("courseName"),
                        rs.getString("userEmail")
                ));
            }
        }
        return commentaires;
    }

    public void updateCommentaire(Commentaire commentaire) throws SQLException {
        String query = "UPDATE commentaires SET contenu = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, commentaire.getContenu());
            stmt.setInt(2, commentaire.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteCommentaire(int commentId) throws SQLException {
        String query = "DELETE FROM commentaires WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, commentId);
            stmt.executeUpdate();
        }
    }
}
