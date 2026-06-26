package com.konserin.models;

import java.sql.Timestamp;

public class ETicket {
    private int eticketId;
    private int orderId;
    private String kodeTiket;
    private String qrCode;
    private Timestamp tanggalTerbit;
    private String statusTiket; // ACTIVE, USED, INVALID

    public ETicket() {}

    public ETicket(int eticketId, int orderId, String kodeTiket, String qrCode, Timestamp tanggalTerbit, String statusTiket) {
        this.eticketId = eticketId;
        this.orderId = orderId;
        this.kodeTiket = kodeTiket;
        this.qrCode = qrCode;
        this.tanggalTerbit = tanggalTerbit;
        this.statusTiket = statusTiket;
    }

    public int getEticketId() { return eticketId; }
    public void setEticketId(int eticketId) { this.eticketId = eticketId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getKodeTiket() { return kodeTiket; }
    public void setKodeTiket(String kodeTiket) { this.kodeTiket = kodeTiket; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public Timestamp getTanggalTerbit() { return tanggalTerbit; }
    public void setTanggalTerbit(Timestamp tanggalTerbit) { this.tanggalTerbit = tanggalTerbit; }

    public String getStatusTiket() { return statusTiket; }
    public void setStatusTiket(String statusTiket) { this.statusTiket = statusTiket; }
}
