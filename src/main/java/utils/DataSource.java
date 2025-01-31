package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static DataSource data;
    private String url = "jdbc:mysql://localhost:3306/espritcours";
    private String user = "root";
    private String password = "";
    private Connection con;

    private DataSource() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connexion à la base de données
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion établie avec succès !");
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur : Driver JDBC introuvable !");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public Connection getCon() {
        return con;
    }

    public static DataSource getInstance() {
        if (data == null) data = new DataSource();
        return data;
    }
}
