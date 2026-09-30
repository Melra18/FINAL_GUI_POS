package com.pos.service;

import com.pos.config.ConnectDb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductService {
    private static final int DEFAULT_STORE_ID = 1;
    private static final int DEFAULT_SUPPLIER_ID = 1;

    public boolean addProduct(String productName, double unitPrice, int stockQuantity, int categoryId, String imagePath) {
        if (productName == null || productName.trim().isEmpty() || unitPrice < 0 || stockQuantity < 0) {
            throw new IllegalArgumentException("Invalid product details provided.");
        }

        String sql = "INSERT INTO products " +
                "(product_name, unit_price, stock_quantity, category_id, supplier_id, store_id, image_path, is_archived) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productName.trim());
            pstmt.setDouble(2, unitPrice);
            pstmt.setInt(3, stockQuantity);
            pstmt.setInt(4, categoryId);
            pstmt.setInt(5, DEFAULT_SUPPLIER_ID);
            pstmt.setInt(6, DEFAULT_STORE_ID);
            pstmt.setString(7, imagePath);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error while adding product: " + e.getMessage());
            return false;
        }
    }
}