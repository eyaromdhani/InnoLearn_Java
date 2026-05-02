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

    // Constructeur privé
    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie !");
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