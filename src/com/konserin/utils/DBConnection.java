package com.konserin.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    // Sesuaikan URL, username, dan password dengan database Anda
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "db_konserin";
    private static final String URL = BASE_URL + DB_NAME;
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default XAMPP biasanya kosong

    private static Connection connection = null;

    // Method untuk mendapatkan koneksi database
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                try {
                    // Coba buka koneksi ke db_konserin
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                } catch (SQLException e) {
                    // Error Code 1049: Unknown database
                    if (e.getErrorCode() == 1049 || e.getMessage().contains("Unknown database")) {
                        System.out.println("Database '" + DB_NAME + "' belum ada. Sistem sedang membuat otomatis dari schema.sql...");
                        createDatabaseFromSchema();
                        // Coba koneksi ulang setelah dibuat
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        System.out.println("Koneksi ke database " + DB_NAME + " berhasil!");
                    } else {
                        throw e; // Lempar error lain
                    }
                }
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

    private static void createDatabaseFromSchema() {
        // Connect ke MySQL root tanpa memilih database (aktifkan multi queries)
        String setupUrl = BASE_URL + "?allowMultiQueries=true";
        try (Connection setupConn = DriverManager.getConnection(setupUrl, USER, PASSWORD);
             Statement stmt = setupConn.createStatement()) {
             
             // Baca file schema.sql
             String schemaPath = "database/schema.sql";
             String sql = new String(Files.readAllBytes(Paths.get(schemaPath)));
             
             // Eksekusi semua query yang ada di schema.sql
             stmt.execute(sql);
             System.out.println("✅ Database dan tabel berhasil dibuat secara otomatis!");
             
        } catch (SQLException | IOException e) {
            System.err.println("❌ Gagal membuat database otomatis dari schema.sql!");
            e.printStackTrace();
        }
    }
}
