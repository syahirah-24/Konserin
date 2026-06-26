package com.konserin.models;

import java.sql.Timestamp;

public class Payment {
    private int paymentId;
    private int orderId;
    private String metodePembayaran;
    private Timestamp tanggalBayar;
    private double jumlahBayar;
    private String statusPembayaran; // SUCCESS, FAILED, PENDING
    private String referenceNumber;

    public Payment() {}

    public Payment(int paymentId, int orderId, String metodePembayaran, Timestamp tanggalBayar, double jumlahBayar, String statusPembayaran, String referenceNumber) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.metodePembayaran = metodePembayaran;
        this.tanggalBayar = tanggalBayar;
        this.jumlahBayar = jumlahBayar;
        this.statusPembayaran = statusPembayaran;
        this.referenceNumber = referenceNumber;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public Timestamp getTanggalBayar() { return tanggalBayar; }
    public void setTanggalBayar(Timestamp tanggalBayar) { this.tanggalBayar = tanggalBayar; }

    public double getJumlahBayar() { return jumlahBayar; }
    public void setJumlahBayar(double jumlahBayar) { this.jumlahBayar = jumlahBayar; }

    public String getStatusPembayaran() { return statusPembayaran; }
    public void setStatusPembayaran(String statusPembayaran) { this.statusPembayaran = statusPembayaran; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
}
