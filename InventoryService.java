package com.pos.service;

import com.pos.config.ConnectDb;
import com.pos.model.InventoryBatch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryService {

    /**
     * 1. ADD NEW BATCH WITH EXPIRATION DATE & DATE RECEIVED
     */
    public boolean addInventoryBatch(InventoryBatch batch) {
        String sql = "INSERT INTO inventory_batches (product_id, batch_number, date_received, expiration_date, " +
                     "bulk_added, initial_pieces, remaining_pieces, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Available')";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, batch.getProductId());
            pstmt.setString(2, batch.getBatchNumber());
            pstmt.setDate(3, batch.getDateReceived());
            pstmt.setDate(4, batch.getExpirationDate());
            pstmt.setInt(5, batch.getBulkAdded());
            pstmt.setInt(6, batch.getInitialPieces());
            pstmt.setInt(7, batch.getRemainingPieces());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error adding inventory batch: " + e.getMessage());
            return false;
        }
    }

    /**
     * 2. STRICT FIFO DEDUCTION LOGIC
     */
    public boolean processFIFODeduction(int productId, int piecesToDeduct) throws SQLException {
        Connection conn = ConnectDb.getConnection();

        String selectSQL = "SELECT batch_id, remaining_pieces FROM inventory_batches " +
                           "WHERE product_id = ? AND remaining_pieces > 0 AND status = 'Available' " +
                           "ORDER BY expiration_date ASC, date_received ASC";

        String updateSQL = "UPDATE inventory_batches SET remaining_pieces = ?, status = ? WHERE batch_id = ?";

        try {
            conn.setAutoCommit(false);

            PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
            selectStmt.setInt(1, productId);
            ResultSet rs = selectStmt.executeQuery();

            int remainingToDeduct = piecesToDeduct;

            while (rs.next() && remainingToDeduct > 0) {
                int batchId = rs.getInt("batch_id");
                int currentBatchPieces = rs.getInt("remaining_pieces");

                if (currentBatchPieces >= remainingToDeduct) {
                    int newQty = currentBatchPieces - remainingToDeduct;
                    String newStatus = (newQty == 0) ? "Depleted" : "Available";

                    PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                    updateStmt.setInt(1, newQty);
                    updateStmt.setString(2, newStatus);
                    updateStmt.setInt(3, batchId);
                    updateStmt.executeUpdate();

                    remainingToDeduct = 0;
                } else {
                    remainingToDeduct -= currentBatchPieces;

                    PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                    updateStmt.setInt(1, 0);
                    updateStmt.setString(2, "Depleted");
                    updateStmt.setInt(3, batchId);
                    updateStmt.executeUpdate();
                }
            }

            if (remainingToDeduct > 0) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * 3. GET TOTAL REMAINING STOCK FOR A PRODUCT
     */
    public int getTotalStockInPieces(int productId) {
        String sql = "SELECT SUM(remaining_pieces) AS total_stock FROM inventory_batches " +
                     "WHERE product_id = ? AND status = 'Available'";
        
        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_stock");
            }

        } catch (SQLException e) {
            System.err.println("Error calculating total stock: " + e.getMessage());
        }
        return 0;
    }
}