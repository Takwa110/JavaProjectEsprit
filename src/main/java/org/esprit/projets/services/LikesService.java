package org.esprit.projets.services;

import org.esprit.projets.entity.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikesService {
    private final Connection connection = DBConnection.getConnection();

    public boolean hasLiked(int idUtilisateur, int idCours) {
        String query = "SELECT COUNT(*) FROM likes WHERE utilisateur_id = ? AND cours_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCours);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addLike(int idUtilisateur, int idCours) {
        String query = "INSERT INTO likes (utilisateur_id, cours_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCours);
            int result = stmt.executeUpdate();
            System.out.println(result + " like added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeLike(int idUtilisateur, int idCours) {
        String query = "DELETE FROM likes WHERE utilisateur_id = ? AND cours_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCours);
            int result = stmt.executeUpdate();
            System.out.println(result + " like removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Course> getCoursesWithLikes() {
        List<Course> courses = new ArrayList<>();
        // Updated query to select all course fields and group by them
        String query = "SELECT c.id, c.title, c.description, c.pdfPath, COUNT(l.id) AS like_count " +
                "FROM course c LEFT JOIN likes l ON c.id = l.cours_id " +
                "GROUP BY c.id, c.title, c.description, c.pdfPath";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String pdfPath = rs.getString("pdfPath");
                int likeCount = rs.getInt("like_count");
                // If needed, you can handle likeCount (e.g., set it in the Course object if supported)
                courses.add(new Course(id, title, description, pdfPath,likeCount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
}
