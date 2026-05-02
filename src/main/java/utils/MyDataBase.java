package utils;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class MyDataBase {
    //atributs

    final String USERNAME="root";
    final String URL ="jdbc:mysql://localhost:3306/innolearn_db?useSSL=false&serverTimezone=UTC";
    final String PASSWORD="";

    Connection connection;
    static MyDataBase instance;

    //constructeur

    private MyDataBase(){
        try{
            connection= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connection to database established successfully");
        }catch(SQLException e){
            e.printStackTrace();
        }


    }

    //methodes

    public static MyDataBase getInstance(){
        if(instance==null){
            instance=new MyDataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
