package org.esprit.projets.services;

import org.esprit.projets.entity.Cours;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikesService {
    private final Connection conn = DBConnection.getConnection();

    public boolean hasLiked(int idUtilisateur, int idCours) throws SQLException {
        String query = "SELECT COUNT(*) FROM likes WHERE utilisateur_id = ? AND cours_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCours);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void addLike(int idUtilisateur, int idCours) throws SQLException {
        String query = "INSERT INTO likes (utilisateur_id, cours_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCours);
            stmt.executeUpdate();
        }
    }

    public void removeLike(int idUtilisateur, int idCours) throws SQLException {
        String query = "DELETE FROM likes WHERE utilisateur_id = ? AND cours_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCours);
            stmt.executeUpdate();
        }
    }
    public List<Cours> getCoursesWithLikes() throws SQLException {
        String query = "SELECT c.id,c.nom, COUNT(l.id) AS like_count " +
                "FROM cours c LEFT JOIN likes l ON c.id = l.cours_id " +
                "GROUP BY c.nom";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            List<Cours> courses = new ArrayList<>();
            while (rs.next()) {
                String courseName = rs.getString("nom");
                int likeCount = rs.getInt("like_count");
                int id = rs.getInt("id");
                courses.add(new Cours(id, courseName,likeCount));
            }

            return courses;
        }
    }

}

