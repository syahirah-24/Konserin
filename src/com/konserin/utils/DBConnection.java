package com.konserin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Sesuaikan URL, username, dan password dengan database Anda
    private static final String URL = "jdbc:mysql://localhost:3306/db_konserin";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default XAMPP biasanya kosong

    private static Connection connection = null;

    // Method untuk mendapatkan koneksi database
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Membuka koneksi
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Koneksi ke database db_konserin berhasil!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL tidak ditemukan. Pastikan file .jar sudah ditambahkan ke project!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database!");
            e.printStackTrace();
        }
        return connection;
    }
}
