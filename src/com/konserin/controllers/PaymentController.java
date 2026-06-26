package com.konserin.controllers;

import com.konserin.models.Payment;
import com.konserin.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PaymentController {

    // Memproses pembayaran (simulasi Payment Gateway)
    public boolean processPayment(Payment payment) {
        String paymentQuery = "INSERT INTO payments (order_id, metode_pembayaran, jumlah_bayar, status_pembayaran, reference_number) VALUES (?, ?, ?, ?, ?)";
        String updateOrderQuery = "UPDATE orders SET status_order = 'PAID' WHERE order_id = ?";
        
        // Simulasi Ref Number dari Payment Gateway
        String refNumber = "REF-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Mulai transaction
            
            // 1. Catat Pembayaran
            PreparedStatement payStmt = conn.prepareStatement(paymentQuery);
            payStmt.setInt(1, payment.getOrderId());
            payStmt.setString(2, payment.getMetodePembayaran());
            payStmt.setDouble(3, payment.getJumlahBayar());
            payStmt.setString(4, "SUCCESS");
            payStmt.setString(5, refNumber);
            payStmt.executeUpdate();
            
            // 2. Update Status Order menjadi PAID
            PreparedStatement orderStmt = conn.prepareStatement(updateOrderQuery);
            orderStmt.setInt(1, payment.getOrderId());
            orderStmt.executeUpdate();
            
            // (Catatan: Pengurangan stok idealnya dilakukan setelah pembayaran sukses)
            // Di sini kita asumsikan stok sudah di-lock saat order dibuat.
            
            conn.commit(); // Simpan
            
            // Setelah pembayaran sukses, panggil generateETicket di TransactionController
            TransactionController txCtrl = new TransactionController();
            txCtrl.generateETicket(payment.getOrderId());
            
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        }
    }
}
