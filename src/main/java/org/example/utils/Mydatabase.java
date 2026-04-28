package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mydatabase {
    // TODO: Provide your Database username and password if not root / empty
    final String USERNAME="root";
    // TODO: Replace 'NOM_DE_LA_BASE' with your actual database name
    String url="jdbc:mysql://localhost:3306/innolearn_db";
    final String PASSWORD="";
     Connection connection;
     static Mydatabase  instance;

    public Connection getConnection() {
        return connection;
    }
    
    //constructeurs

    public static Mydatabase getInstance() {
        if (instance == null) {
            instance = new Mydatabase();
        }
        return instance;
    }

    private Mydatabase()  {
        try {
            connection=DriverManager.getConnection(url,USERNAME,PASSWORD);
            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }


    }}

