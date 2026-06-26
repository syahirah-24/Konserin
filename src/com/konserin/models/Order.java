package com.konserin.models;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int userId;
    private Timestamp tanggalPesan;
    private double totalHarga;
    private String statusOrder; // PENDING, PAID, CANCELED

    public Order() {}

    public Order(int orderId, int userId, Timestamp tanggalPesan, double totalHarga, String statusOrder) {
        this.orderId = orderId;
        this.userId = userId;
        this.tanggalPesan = tanggalPesan;
        this.totalHarga = totalHarga;
        this.statusOrder = statusOrder;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Timestamp getTanggalPesan() { return tanggalPesan; }
    public void setTanggalPesan(Timestamp tanggalPesan) { this.tanggalPesan = tanggalPesan; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }

    public String getStatusOrder() { return statusOrder; }
    public void setStatusOrder(String statusOrder) { this.statusOrder = statusOrder; }
}
