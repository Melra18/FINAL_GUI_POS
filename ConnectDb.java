package com.pos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConnectDb {
    
    private static final String URL = "jdbc:mysql://localhost:3306/melra_pos_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Add your XAMPP password if you have one

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Database Connection Error: " + e.getMessage(), 
                "Connection Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }
}