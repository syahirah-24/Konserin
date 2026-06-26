package com.konserin.models;

public class TicketCategory {
    private int categoryId;
    private int concertId; // FK
    private String namaKategori;
    private double harga;
    private int kuota;
    private int stokTersedia;

    public TicketCategory() {}

    public TicketCategory(int categoryId, int concertId, String namaKategori, double harga, int kuota, int stokTersedia) {
        this.categoryId = categoryId;
        this.concertId = concertId;
        this.namaKategori = namaKategori;
        this.harga = harga;
        this.kuota = kuota;
        this.stokTersedia = stokTersedia;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getConcertId() { return concertId; }
    public void setConcertId(int concertId) { this.concertId = concertId; }

    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public int getKuota() { return kuota; }
    public void setKuota(int kuota) { this.kuota = kuota; }

    public int getStokTersedia() { return stokTersedia; }
    public void setStokTersedia(int stokTersedia) { this.stokTersedia = stokTersedia; }
}
