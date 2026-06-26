package com.konserin.models;

public class Admin extends Account {
    
    public Admin() {
        super();
    }

    public Admin(int id, String nama, String email, String password) {
        super(id, nama, email, password);
    }

    @Override
    public String getRoleDescription() {
        return "Admin: Mengelola data konser, kategori tiket, dan memantau transaksi.";
    }
}
