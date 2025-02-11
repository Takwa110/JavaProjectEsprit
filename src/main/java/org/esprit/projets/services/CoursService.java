package org.esprit.projets.services;

import org.esprit.projets.entity.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService {
    private final Connection conn = DBConnection.getConnection();

    public void updateLikeCount(int idCours, int increment) throws SQLException {
        String query = "UPDATE course SET likes = likes + ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, increment);
            stmt.setInt(2, idCours);
            stmt.executeUpdate();
        }
    }

}
