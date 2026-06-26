CREATE DATABASE IF NOT EXISTS db_konserin;
USE db_konserin;

-- Tabel Admin
CREATE TABLE admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    nama_admin VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Tabel User
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    no_telp VARCHAR(20),
    tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Concert
CREATE TABLE concerts (
    concert_id INT AUTO_INCREMENT PRIMARY KEY,
    nama_konser VARCHAR(150) NOT NULL,
    deskripsi TEXT,
    lokasi VARCHAR(255) NOT NULL,
    tanggal_konser DATETIME NOT NULL,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELED') DEFAULT 'UPCOMING'
);

-- Tabel TicketCategory
CREATE TABLE ticket_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    concert_id INT NOT NULL,
    nama_kategori VARCHAR(50) NOT NULL,
    harga DECIMAL(10,2) NOT NULL,
    kuota INT NOT NULL,
    stok_tersedia INT NOT NULL,
    FOREIGN KEY (concert_id) REFERENCES concerts(concert_id) ON DELETE CASCADE
);

-- Tabel Order (Pemesanan)
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    tanggal_pesan TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_harga DECIMAL(12,2) NOT NULL,
    status_order ENUM('PENDING', 'PAID', 'CANCELED') DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Tabel OrderDetail
CREATE TABLE order_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    category_id INT NOT NULL,
    jumlah_tiket INT NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES ticket_categories(category_id) ON DELETE RESTRICT
);

-- Tabel ETicket
CREATE TABLE e_tickets (
    eticket_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    kode_tiket VARCHAR(50) NOT NULL UNIQUE,
    qr_code VARCHAR(255),
    tanggal_terbit TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_tiket ENUM('ACTIVE', 'USED', 'INVALID') DEFAULT 'ACTIVE',
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Tabel Payment
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    metode_pembayaran VARCHAR(50) NOT NULL,
    tanggal_bayar TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    jumlah_bayar DECIMAL(12,2) NOT NULL,
    status_pembayaran ENUM('SUCCESS', 'FAILED', 'PENDING') DEFAULT 'PENDING',
    reference_number VARCHAR(100) UNIQUE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Data Dummy untuk Admin (opsional, agar bisa langsung login)
INSERT INTO admins (nama_admin, email, password) VALUES ('Super Admin', 'admin@konserin.com', 'admin123');
