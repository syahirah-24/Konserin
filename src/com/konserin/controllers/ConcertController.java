package com.konserin.controllers;

import com.konserin.models.Concert;
import com.konserin.models.TicketCategory;
import com.konserin.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConcertController {

    // Mendapatkan semua daftar konser
    public List<Concert> getAllConcerts() {
        List<Concert> concerts = new ArrayList<>();
        String query = "SELECT * FROM concerts ORDER BY tanggal_konser DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Concert c = new Concert(
                    rs.getInt("concert_id"),
                    rs.getString("nama_konser"),
                    rs.getString("deskripsi"),
                    rs.getString("lokasi"),
                    rs.getDate("tanggal_konser"),
                    rs.getString("status")
                );
                concerts.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return concerts;
    }

    // Menambah konser baru (Untuk Admin)
    public boolean addConcert(Concert c) {
        String query = "INSERT INTO concerts (nama_konser, deskripsi, lokasi, tanggal_konser, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, c.getNamaKonser());
            stmt.setString(2, c.getDeskripsi());
            stmt.setString(3, c.getLokasi());
            stmt.setDate(4, new java.sql.Date(c.getTanggalKonser().getTime()));
            stmt.setString(5, c.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mendapatkan kategori tiket berdasarkan ID konser
    public List<TicketCategory> getTicketCategoriesByConcertId(int concertId) {
        List<TicketCategory> categories = new ArrayList<>();
        String query = "SELECT * FROM ticket_categories WHERE concert_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, concertId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TicketCategory tc = new TicketCategory(
                    rs.getInt("category_id"),
                    rs.getInt("concert_id"),
                    rs.getString("nama_kategori"),
                    rs.getDouble("harga"),
                    rs.getInt("kuota"),
                    rs.getInt("stok_tersedia")
                );
                categories.add(tc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}
