package com.konserin.models;

import java.util.Date;

public class Concert {
    private int concertId;
    private String namaKonser;
    private String deskripsi;
    private String lokasi;
    private Date tanggalKonser;
    private String status; // UPCOMING, ONGOING, COMPLETED, CANCELED

    public Concert() {}

    public Concert(int concertId, String namaKonser, String deskripsi, String lokasi, Date tanggalKonser, String status) {
        this.concertId = concertId;
        this.namaKonser = namaKonser;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.tanggalKonser = tanggalKonser;
        this.status = status;
    }

    public int getConcertId() { return concertId; }
    public void setConcertId(int concertId) { this.concertId = concertId; }

    public String getNamaKonser() { return namaKonser; }
    public void setNamaKonser(String namaKonser) { this.namaKonser = namaKonser; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public Date getTanggalKonser() { return tanggalKonser; }
    public void setTanggalKonser(Date tanggalKonser) { this.tanggalKonser = tanggalKonser; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
