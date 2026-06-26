# Product Requirements Document (PRD)
## Aplikasi KONSERIN — Sistem Informasi Penjualan Tiket Konser

**Versi:** 1.0
**Tanggal:** 26 Juni 2026
**Disusun untuk:** Project Akhir Mata Kuliah Pemrograman Berorientasi Objek
**Kelompok:** Kelompok 4 — Kelas A
**Anggota:**
1. Aisyah Rahma Setiaputri — 2410512002
2. Azzahra Salwa Syahirah — 2410512021
3. Zaskia Maharani — 2410512025
4. Shifa Nurul Deswita — 2410512028
5. Nadya Rouli Br Sibuea — 2410512030

---

## 1. Latar Belakang

Proses penjualan tiket konser secara manual atau semi-manual rentan terhadap masalah seperti pencatatan stok tiket yang tidak akurat, kesalahan transaksi, dan kesulitan dalam pelacakan riwayat pemesanan. **KONSERIN** dikembangkan sebagai aplikasi desktop berbasis Java untuk mendigitalisasi dan mengotomatisasi seluruh proses penjualan tiket konser — mulai dari pengelolaan data konser, pemesanan, pembayaran, hingga penerbitan e-tiket — dengan data tersimpan secara terstruktur dalam database MySQL.

## 2. Tujuan Produk

- Menyediakan platform terkomputerisasi bagi pengelola konser untuk mengelola data konser, kategori tiket, dan transaksi penjualan.
- Memudahkan pelanggan (user) dalam mencari konser, memesan tiket, melakukan pembayaran, dan mengunduh e-tiket secara mandiri.
- Menerapkan prinsip Object Oriented Programming (Encapsulation, Inheritance, Polymorphism) agar sistem mudah dikembangkan dan dipelihara.
- Menjamin integritas data stok tiket melalui validasi otomatis sebelum tiket diterbitkan.

## 3. Lingkup Produk (Scope)

### Termasuk dalam scope:
- Aplikasi desktop Java dengan GUI berbasis Java Swing.
- Database MySQL untuk penyimpanan data konser, pelanggan, transaksi, dan pembayaran.
- Simulasi Payment Gateway (bukan integrasi nyata dengan penyedia pembayaran).
- Modul autentikasi, manajemen konser, transaksi/pemesanan, dan pembayaran/pelaporan.

### Di luar scope:
- Integrasi pembayaran nyata (payment gateway sungguhan).
- Aplikasi versi mobile atau web.
- Notifikasi via email/SMS/push notification.

## 4. Peran Pengguna (User Roles)

| Peran | Deskripsi |
|---|---|
| **Admin** | Mengelola data konser & kategori tiket, memantau transaksi dan penjualan, men-generate laporan penjualan. |
| **User (Pembeli Tiket)** | Registrasi/login, mencari konser, memesan tiket, melakukan pembayaran, mengunduh e-tiket berbentuk kode QR. |
| **Payment Gateway** | Entitas pembayaran yang disimulasikan untuk memproses status pembayaran (berhasil/gagal). |

## 5. Modul Fungsional

1. **Modul Autentikasi**
   Menangani login & registrasi untuk admin dan user, lengkap dengan validasi kredensial.
2. **Modul Manajemen Konser**
   Admin dapat melakukan CRUD (tambah/ubah/hapus) terhadap data konser dan kategori tiket.
3. **Modul Transaksi dan Pemesanan**
   Menangani logika pemesanan tiket, perhitungan total harga, pengecekan stok tiket secara real time, dan pembuatan e-tiket.
4. **Modul Pembayaran dan Pelaporan**
   Mensimulasikan proses pembayaran melalui payment gateway dan menghasilkan laporan penjualan bagi admin.

## 6. Kebutuhan Fungsional (Functional Requirements)

### 6.1 User
| ID | Kebutuhan |
|---|---|
| FR-U1 | User dapat melakukan registrasi akun baru. |
| FR-U2 | User dapat login menggunakan email & password. |
| FR-U3 | User dapat melihat daftar konser yang tersedia. |
| FR-U4 | User dapat melihat detail konser & kategori tiket. |
| FR-U5 | User dapat memilih kategori tiket dan memasukkan jumlah tiket. |
| FR-U6 | Sistem mengecek ketersediaan stok tiket secara real time sebelum pesanan dibuat. |
| FR-U7 | User dapat melihat ringkasan pesanan dan total harga sebelum konfirmasi. |
| FR-U8 | User dapat memilih metode pembayaran dan melakukan pembayaran. |
| FR-U9 | Jika pembayaran berhasil: sistem memperbarui status transaksi, mengurangi stok tiket, men-generate QR code, serta membuat & menyimpan e-tiket. |
| FR-U10 | Jika pembayaran gagal: sistem memberi opsi bayar ulang atau membatalkan pesanan. |
| FR-U11 | User dapat melihat riwayat transaksi. |
| FR-U12 | User dapat mengunduh e-tiket (kode QR). |

### 6.2 Admin
| ID | Kebutuhan |
|---|---|
| FR-A1 | Admin dapat login dengan validasi kredensial; akses ditolak jika tidak valid. |
| FR-A2 | Admin dapat menambah, mengubah, dan menghapus data konser (CRUD Concert). |
| FR-A3 | Admin dapat mengelola (CRUD) kategori tiket. |
| FR-A4 | Admin dapat melihat data transaksi seluruh user. |
| FR-A5 | Admin dapat memantau penjualan tiket. |
| FR-A6 | Admin dapat men-generate laporan penjualan. |
| FR-A7 | Setiap perubahan data oleh admin tersimpan dan terekapitulasi otomatis dalam laporan. |

## 7. Kebutuhan Non-Fungsional

| Kategori | Deskripsi |
|---|---|
| **Bahasa Pemrograman** | Java |
| **Antarmuka** | Graphical User Interface (GUI) menggunakan Java Swing |
| **Basis Data** | MySQL |
| **Konsep OOP** | Wajib menerapkan minimal 3 konsep: Encapsulation (private attribute + getter/setter), Inheritance (class turunan), Polymorphism (multi-implementasi method pembayaran) |
| **Usability** | Tampilan harus mudah dipahami oleh pengguna non-teknis |
| **Reliabilitas Data** | Validasi stok tiket otomatis untuk mencegah overselling |
| **Keamanan** | Validasi login untuk mencegah akses tidak sah pada akun admin maupun user |

## 8. Entitas Data Utama (berdasarkan ERD/Class Diagram)

| Entitas | Atribut Kunci | Relasi |
|---|---|---|
| **Admin** | admin_id, nama_admin, email, password | mengelola Concert & TicketCategory; memantau Payment |
| **User** | user_id, nama, email, password, no_telp, tanggal_daftar | membuat Order |
| **Concert** | concert_id, nama_konser, deskripsi, lokasi, tanggal_konser, status | menyediakan TicketCategory |
| **TicketCategory** | category_id, concert_id (FK), nama_kategori, harga, kuota, stok_tersedia | dipilih oleh OrderDetail |
| **Order** | order_id, user_id (FK), tanggal_pesan, total_harga, status_order | memiliki OrderDetail; menghasilkan ETicket; dibayar_dengan Payment |
| **OrderDetail** | detail_id, order_id (FK), category_id (FK), jumlah_tiket, subtotal | — |
| **ETicket** | eticket_id, order_id (FK), kode_tiket, qr_code, tanggal_terbit, status_tiket | — |
| **Payment** | payment_id, order_id (FK), metode_pembayaran, tanggal_bayar, jumlah_bayar, status_pembayaran, reference_number | — |

## 9. Alur Proses Utama

### 9.1 Alur User (Pemesanan Tiket)
1. Buka aplikasi → Login / Registrasi akun.
2. Sistem memvalidasi data login.
3. User melihat daftar konser → memilih konser → melihat detail & kategori tiket.
4. User memilih kategori tiket & jumlah → sistem cek ketersediaan stok.
   - Jika stok habis → tampilkan notifikasi, proses berhenti.
   - Jika tersedia → sistem membuat pesanan sementara, menghitung total harga, menampilkan ringkasan.
5. User konfirmasi pesanan & pilih metode pembayaran.
6. Sistem membuat data transaksi dan mengirim ke Payment Gateway.
7. Payment Gateway memproses pembayaran:
   - **Berhasil** → update status transaksi, kurangi stok tiket, generate QR code, buat & simpan e-tiket, kirim notifikasi berhasil.
   - **Gagal** → update status transaksi gagal, user diberi opsi bayar ulang atau batalkan pesanan.
8. User dapat melihat riwayat transaksi dan mengunduh e-tiket.

### 9.2 Alur Admin
1. Login admin → validasi kredensial.
   - Tidak valid → akses ditolak, sesi dihentikan.
2. Login berhasil → masuk ke dashboard admin.
3. Admin dapat: kelola data konser (CRUD), kelola kategori tiket (CRUD), lihat data transaksi, pantau penjualan, generate laporan penjualan.
4. Setiap perubahan data disimpan sistem dan direkapitulasi ke dalam laporan otomatis.

## 10. Kriteria Penerimaan (Acceptance Criteria)

- [ ] Aplikasi menerapkan minimal 3 konsep OOP (Encapsulation, Inheritance, Polymorphism) yang dapat ditunjukkan pada source code.
- [ ] Aplikasi memiliki GUI berbasis Java Swing yang berfungsi penuh untuk seluruh modul.
- [ ] Data konser, pelanggan, transaksi, dan pembayaran berhasil diambil dan ditampilkan dari database MySQL.
- [ ] User dapat menyelesaikan siklus penuh: registrasi → login → pesan tiket → bayar → unduh e-tiket.
- [ ] Admin dapat menyelesaikan siklus penuh: login → CRUD konser/kategori → lihat transaksi → generate laporan.
- [ ] Stok tiket berkurang secara otomatis dan akurat setiap kali pembayaran berhasil.
- [ ] Skenario pembayaran gagal tertangani dengan opsi bayar ulang/batal.

## 11. Pembagian Tanggung Jawab Tim

| Anggota | Tanggung Jawab |
|---|---|
| Anggota 1 | Analisis kebutuhan sistem, flowchart, ERD, perancangan database |
| Anggota 2 | Pembuatan database MySQL, tabel, relasi data, koneksi database-aplikasi |
| Anggota 3 | Pembuatan GUI (Java Swing): menu utama, form data konser, form data pelanggan |
| Anggota 4 | Pengembangan fitur pemesanan tiket & pembayaran, implementasi CRUD |
| Anggota 5 | Pengujian aplikasi, dokumentasi, pembuatan video demo, penyusunan laporan akhir |

## 12. Batasan dan Asumsi

- Payment Gateway bersifat simulasi, tidak terhubung ke penyedia pembayaran sungguhan.
- Aplikasi berjalan sebagai aplikasi desktop standalone, bukan berbasis web/cloud.
- Tidak ada batasan jumlah konser/kategori tiket yang dikelola sistem, mengikuti kapasitas database.

## 13. Referensi Dokumen Asal

Dokumen ini disusun berdasarkan laporan project "Pengembangan Sistem Informasi Penjualan Tiket Konser Menggunakan Java dan MySQL dengan Pendekatan OOP pada Aplikasi KONSERIN" — Kelompok 4, Program Studi S1 Sistem Informasi, Fakultas Ilmu Komputer, Universitas Pembangunan Nasional Veteran Jakarta, 2026.