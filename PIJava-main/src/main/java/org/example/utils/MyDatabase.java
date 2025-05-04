package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private String URL="jdbc:mysql://localhost:3306/test1";
    private String USER="root";
    private String PASSWORD="";
    private Connection conn;
    private static MyDatabase instance;
    private MyDatabase() {
        try {
            conn= DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static MyDatabase getInstance() {
        if(instance==null) {
            instance=new MyDatabase();
        }else{
            System.out.println("Already connected");
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }
}
