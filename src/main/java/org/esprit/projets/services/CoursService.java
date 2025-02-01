package org.esprit.projets.services;

import org.esprit.projets.entity.Cours;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService {
    private final Connection conn = DBConnection.getConnection();

    public List<Cours> getAllCours() throws SQLException {
        List<Cours> coursList = new ArrayList<>();
        String query = "SELECT * FROM cours";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                coursList.add(new Cours(rs.getInt("id"), rs.getString("nom"),rs.getInt("likes")));
            }
        }
        return coursList;
    }
    public void updateLikeCount(int idCours, int increment) throws SQLException {
        String query = "UPDATE cours SET likes = likes + ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, increment);
            stmt.setInt(2, idCours);
            stmt.executeUpdate();
        }
    }

}
