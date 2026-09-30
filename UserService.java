package com.pos.service;

import com.pos.config.ConnectDb;
import com.pos.model.User;
import java.sql.*;

public class UserService {

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_archived = FALSE";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("role"),
                    rs.getInt("store_id"),
                    rs.getBoolean("is_archived")
                );
            }
        } catch (SQLException e) {
            System.err.println("Database Authentication Error: " + e.getMessage());
        }
        return null;
    }
}