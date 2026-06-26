package com.konserin.models;

import java.sql.Timestamp;

public class User extends Account {
    private String noTelp;
    private Timestamp tanggalDaftar;

    public User() {
        super();
    }

    public User(int id, String nama, String email, String password, String noTelp, Timestamp tanggalDaftar) {
        super(id, nama, email, password);
        this.noTelp = noTelp;
        this.tanggalDaftar = tanggalDaftar;
    }

    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }

    public Timestamp getTanggalDaftar() { return tanggalDaftar; }
    public void setTanggalDaftar(Timestamp tanggalDaftar) { this.tanggalDaftar = tanggalDaftar; }

    @Override
    public String getRoleDescription() {
        return "User: Pembeli tiket yang memesan dan membeli tiket konser.";
    }
}
