package Services;

import Entite.IService;
import Entite.Question;
import Utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService implements IService<Question> {
    private Connection con;

    // Constructor
    public QuizService() {
        this.con = DataSource.getInstance().getCon();
    }

    @Override
    public void ajouter(Question quiz) throws SQLException {
        String query = "INSERT INTO question (questionText, correctAnswer) VALUES (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, quiz.getQuestion());
            stmt.setString(2, quiz.getAnswer());
            stmt.executeUpdate();
            System.out.println("Quiz added successfully.");
        }
    }

    @Override
    public void supprimer(Question quiz) throws SQLException {
        String query = "DELETE FROM question WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, quiz.getId());
            stmt.executeUpdate();
            System.out.println("Quiz deleted successfully.");
        }
    }

    @Override
    public void update(Question quiz) throws SQLException {
        String query = "UPDATE question SET questionText = ?, correctAnswer = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, quiz.getQuestion());
            stmt.setString(2, quiz.getAnswer());
            stmt.setInt(3, quiz.getId());
            stmt.executeUpdate();
            System.out.println("Quiz updated successfully.");
        }
    }

    @Override
    public List<Question> getAll() throws SQLException {
        List<Question> quizzes = new ArrayList<>();
        String query = "SELECT * FROM question";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String question = rs.getString("questionText");
                String answer = rs.getString("correctAnswer");
                quizzes.add(new Question(id, question, answer));
            }
        }
        return quizzes;
    }

    @Override
    public Question getById(int id) throws SQLException {
        String query = "SELECT * FROM question WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String question = rs.getString("questionText");
                    String answer = rs.getString("correctAnswer");
                    return new Question(id, question, answer);
                } else {
                    return null;
                }
            }
        }
    }
}
