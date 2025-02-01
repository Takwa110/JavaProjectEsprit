//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static final String URL = "jdbc:mysql://localhost:3306/projetGUI";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;
    private static DataSource instance;

    private DataSource() {
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }

        return instance;
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetGUI", "root", "");
                System.out.println("Connection established.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Connection failed.");
            }
        }

        return connection;
    }

    public boolean isConnectionValid() {
        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection is valid.");
                return true;
            } else {
                System.out.println("Connection is invalid or closed.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error checking connection validity: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }

    }
}
