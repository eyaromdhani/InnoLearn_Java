package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String URL = "jdbc:mysql://127.0.0.1:3306/innolearn_db?serverTimezone=UTC&zeroDateTimeBehavior=convertToNull";
    private final String USER = "root";
    private final String PASSWORD = "";

    private Connection connection;
    private static MyDataBase instance;

    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie !");
            
            // Auto-migration pour ajouter enseignant_id
            try {
                java.sql.Statement stmt = connection.createStatement();
                stmt.execute("ALTER TABLE cours ADD COLUMN enseignant_id INT");
                stmt.execute("ALTER TABLE cours ADD CONSTRAINT fk_cours_user FOREIGN KEY (enseignant_id) REFERENCES user(id)");
                System.out.println("Migration BD: Colonne enseignant_id ajoutée avec succès !");
            } catch (SQLException e) {
                // Ignore, la colonne existe probablement déjà
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    // Méthode pour obtenir l'instance unique
    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de reconnexion : " + e.getMessage());
        }
        return connection;
    }
}