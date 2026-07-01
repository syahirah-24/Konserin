# KONSERIN — Sistem Informasi Penjualan Tiket Konser

KONSERIN adalah aplikasi desktop berbasis Java Swing dan MySQL yang dirancang untuk memudahkan proses penjualan, pembelian, dan pengelolaan tiket konser secara terkomputerisasi. Proyek ini dibuat sebagai pemenuhan tugas akhir mata kuliah Pemrograman Berorientasi Objek (PBO).

---

## 🚀 Fitur Utama

### 👨‍💻 Admin Panel
1. **Kelola Konser (CRUD)**: Menambah, mengedit, menghapus, dan memperbarui status konser (UPCOMING, ONGOING, COMPLETED, CANCELED).
2. **Kelola Kategori Tiket (CRUD)**: Mengatur kuota, harga, nama kategori, dan ketersediaan stok tiket per konser.
3. **Lihat Transaksi**: Memantau seluruh pemesanan tiket yang dilakukan oleh pembeli beserta status pembayarannya.
4. **Generate Laporan**: Rekapitulasi penjualan total, total pendapatan, tiket terjual per konser, dan rincian detail per kategori tiket secara otomatis.

### 👤 Pembeli (User)
1. **Autentikasi**: Registrasi akun baru dan login.
2. **Eksplorasi Konser**: Melihat daftar konser aktif beserta detail lokasi dan tanggal.
3. **Pemesanan Tiket**: Memilih kategori tiket dan menentukan jumlah dengan pengecekan stok secara real-time.
4. **Simulasi Pembayaran**: Simulasi pemrosesan pembayaran instan dengan berbagai metode pilihan.
5. **E-Tiket & QR Code**: Unduh e-tiket unik berstatus `ACTIVE` lengkap dengan visualisasi simulasi QR Code setelah pembayaran berhasil.
6. **Riwayat Transaksi**: Melihat riwayat lengkap pesanan dari yang paling baru.

---

## 🛠️ Desain & Konsep OOP yang Diterapkan

1. **Encapsulation**: Semua atribut model (pada package `com.konserin.models`) diset `private` dan diakses melalui getter/setter yang divalidasi.
2. **Inheritance**: Class `Admin` dan `User` mewarisi (inherit) atribut dan method umum dari class induk (parent) `Account`.
3. **Polymorphism**: Demonstrasi method overriding pada abstract method `getRoleDescription()` di kelas turunan `Account`.
4. **Database Transaction**: Proses checkout menggunakan commit/rollback SQL transaksi untuk menjaga konsistensi kuota tiket (mencegah *overselling*).

---

## 💻 Prasyarat Menjalankan Aplikasi

- Java Development Kit (JDK) versi 17 atau di atasnya.
- XAMPP / MySQL Server lokal dalam keadaan berjalan.
- **Kredensial database default**: Username: `root`, Password: `""` (kosong).

---

## 🔌 Cara Menjalankan Aplikasi

Aplikasi ini dilengkapi fitur **Auto-Create Database**. Anda tidak perlu mengimpor schema.sql secara manual di phpMyAdmin. Sistem akan otomatis mendeteksi, membuat database `db_konserin`, dan menginisialisasi tabel-tabelnya ketika pertama kali dijalankan.

Jalankan perintah ini di PowerShell atau Terminal pada root direktori proyek:

### 1. Kompilasi Kode
```powershell
# Buat folder output kompilasi
New-Item -ItemType Directory -Force -Path out

# Kompilasi seluruh file Java
javac -cp "lib\mysql-connector-j-9.7.0.jar" -d out (Get-ChildItem -Recurse -Filter "*.java" -Path "src" | ForEach-Object { $_.FullName })
```

### 2. Jalankan Aplikasi
```powershell
java -cp "out;lib\mysql-connector-j-9.7.0.jar" com.konserin.MainApp
```

---

## 👤 Akun Uji Coba Default (Admin)
Untuk masuk sebagai admin tanpa mendaftar baru, gunakan kredensial berikut pada tab login Admin:
- **Email**: `admin@konserin.com`
- **Password**: `admin123`
