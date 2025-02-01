package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("C:/Users/sarra.khelifi/Desktop/Connexion1AL3 - Copy/src/main/resources/DashboardView.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        primaryStage.setTitle("Course Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Database credentials
        String url = "jdbc:mysql://localhost:3306/projetgui"; // Change URL as needed
        String username = "root"; // Replace with your database username
        String password = ""; // Replace with your database password

        // Test the connection before launching the app
        testConnection(url, username, password);

        // Launch the JavaFX application
        launch(args);
    }

    // Method to test database connection
    public static void testConnection(String url, String username, String password) {
        try {
            // Try to establish a connection
            Connection connection = DriverManager.getConnection(url, username, password);

            // If the connection is successful, print a success message
            if (connection != null) {
                System.out.println("Connection established successfully!");
                connection.close(); // Always close the connection after use
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            // If there's an error, print the error message
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
