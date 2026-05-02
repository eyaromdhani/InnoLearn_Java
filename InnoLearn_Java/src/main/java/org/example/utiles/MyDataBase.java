package org.example.utiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    final String url = "jdbc:mysql://localhost:3306/innolearn_db";
    final String user = "root";
    final String pwd = "";

    private Connection conn;
    static MyDataBase instance ;
    //constructor
    private MyDataBase(){
        try{
            conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connected to database successfully");
        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static MyDataBase getInstance()  {
        if (instance == null) {
            instance = new MyDataBase() ;
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }


}
