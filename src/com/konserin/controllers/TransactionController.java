package com.konserin.controllers;

import com.konserin.models.Order;
import com.konserin.models.OrderDetail;
import com.konserin.utils.DBConnection;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class TransactionController {

    // Membuat pesanan baru
    public boolean createOrder(Order order, List<OrderDetail> details) {
        String orderQuery = "INSERT INTO orders (user_id, total_harga, status_order) VALUES (?, ?, ?)";
        String detailQuery = "INSERT INTO order_details (order_id, category_id, jumlah_tiket, subtotal) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Memulai transaction
            
            // 1. Insert Order
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setDouble(2, order.getTotalHarga());
            orderStmt.setString(3, "PENDING");
            orderStmt.executeUpdate();
            
            ResultSet rs = orderStmt.getGeneratedKeys();
            int generatedOrderId = 0;
            if (rs.next()) {
                generatedOrderId = rs.getInt(1);
            }
            
            // 2. Insert Order Details
            PreparedStatement detailStmt = conn.prepareStatement(detailQuery);
            for (OrderDetail detail : details) {
                detailStmt.setInt(1, generatedOrderId);
                detailStmt.setInt(2, detail.getCategoryId());
                detailStmt.setInt(3, detail.getJumlahTiket());
                detailStmt.setDouble(4, detail.getSubtotal());
                detailStmt.addBatch();
            }
            detailStmt.executeBatch();
            
            conn.commit(); // Selesaikan transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        }
    }

    // Menerbitkan E-Ticket
    public boolean generateETicket(int orderId) {
        String query = "INSERT INTO e_tickets (order_id, kode_tiket, qr_code, status_tiket) VALUES (?, ?, ?, ?)";
        String uniqueCode = "TIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, orderId);
            stmt.setString(2, uniqueCode);
            stmt.setString(3, "QR_" + uniqueCode);
            stmt.setString(4, "ACTIVE");
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
