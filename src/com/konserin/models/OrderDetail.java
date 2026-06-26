package com.konserin.models;

public class OrderDetail {
    private int detailId;
    private int orderId;
    private int categoryId;
    private int jumlahTiket;
    private double subtotal;

    public OrderDetail() {}

    public OrderDetail(int detailId, int orderId, int categoryId, int jumlahTiket, double subtotal) {
        this.detailId = detailId;
        this.orderId = orderId;
        this.categoryId = categoryId;
        this.jumlahTiket = jumlahTiket;
        this.subtotal = subtotal;
    }

    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getJumlahTiket() { return jumlahTiket; }
    public void setJumlahTiket(int jumlahTiket) { this.jumlahTiket = jumlahTiket; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
