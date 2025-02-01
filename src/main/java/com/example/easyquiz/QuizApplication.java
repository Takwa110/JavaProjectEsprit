package com.example.easyquiz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Utils.DataSource;  // Import your DataSource class
import Services.QuizService;  // Import QuestionService
import Entite.Question;  // Import Question entity
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;

public class QuizApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Test the database connection
        DataSource dataSource = DataSource.getInstance();
        if (dataSource.getCon() != null) {
            System.out.println("Database connection successful!");
        } else {
            System.out.println("Failed to connect to the database.");
        }

        // Test for adding a question
        testAddQuestion();

        FXMLLoader fxmlLoader = new FXMLLoader(QuizApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    // Method to test adding a question
    private void testAddQuestion() {
        try {
            // Create an instance of QuestionService
            QuizService questionService = new QuizService();

            // Add a new question
            Question newQuestion = new Question(0, "What is the capital of France?", "Paris");
            questionService.ajouter(newQuestion);

            // Retrieve and print all questions
            List<Question> questions = questionService.getAll();
            System.out.println("All questions after addition:");
            questions.forEach(System.out::println);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding question: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
