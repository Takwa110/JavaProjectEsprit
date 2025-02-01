package org.esprit.projets.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/esprit";
    private static final String USER = "root";  // Change if needed
    private static final String PASSWORD = ""; // Change if needed

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Ensure the driver is loaded
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected!");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL Driver not found!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}
