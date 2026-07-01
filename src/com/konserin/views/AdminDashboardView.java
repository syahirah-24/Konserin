package com.konserin.views;

import com.konserin.controllers.ConcertController;
import com.konserin.models.Admin;
import com.konserin.models.Concert;
import com.konserin.models.TicketCategory;
import com.konserin.utils.DBConnection;
import com.konserin.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * AdminDashboardView — Panel utama Admin dengan 4 tab:
 *  1. Kelola Konser (CRUD)
 *  2. Kelola Kategori Tiket (CRUD per konser)
 *  3. Data Transaksi
 *  4. Laporan Penjualan
 */
public class AdminDashboardView extends JFrame {

    private final Admin             admin;
    private final ConcertController concertCtrl = new ConcertController();
    private final NumberFormat      rupiah      = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
    private final SimpleDateFormat  sdf         = new SimpleDateFormat("dd-MM-yyyy");

    // Tab 1 — Konser
    private DefaultTableModel concertTableModel;
    private JTable            concertTable;

    // Tab 2 — Kategori
    private DefaultTableModel catTableModel;
    private JTable            catTable;
    private JComboBox<String> concertFilterCombo;
    private List<Concert>     concertList;

    // Tab 3 — Transaksi
    private DefaultTableModel txTableModel;
    private JTable            txTable;

    // Tab 4 — Laporan
    private JTextArea reportArea;

    public AdminDashboardView(Admin admin) {
        this.admin = admin;
        setTitle("KONSERIN — Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);

        JPanel root = UITheme.makeDarkPanel(new BorderLayout());
        setContentPane(root);

        root.add(buildNavBar(),   BorderLayout.NORTH);
        root.add(buildTabs(),     BorderLayout.CENTER);
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.BG_CARD);
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(14, 24, 14, 24)
        ));

        JLabel logo  = UITheme.makeLabel("KONSERIN  |  Admin Panel", UITheme.FONT_HEADING, UITheme.ACCENT_PRIMARY);
        JLabel admin = UITheme.makeLabel("Admin: " + this.admin.getNama(), UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);

        JButton logout = UITheme.makeDangerButton("Keluar");
        logout.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(admin);
        right.add(logout);

        nav.add(logo,  BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BUTTON);

        tabs.addTab("Kelola Konser",  buildConcertTab());
        tabs.addTab("Kelola Tiket",   buildCategoryTab());
        tabs.addTab("Transaksi",       buildTransactionTab());
        tabs.addTab("Laporan",         buildReportTab());

        // Load data when tab changes
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) loadConcerts();
            else if (idx == 1) { loadConcertFilter(); loadCategories(); }
            else if (idx == 2) loadTransactions();
            else if (idx == 3) generateReport();
        });

        // Initial load
        loadConcerts();
        return tabs;
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  TAB 1: Kelola Konser
    // ──────────────────────────────────────────────────────────────────────────
    private JPanel buildConcertTab() {
        JPanel p = UITheme.makeDarkPanel(new BorderLayout(0, 0));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Table
        String[] cols = {"ID", "Nama Konser", "Lokasi", "Tanggal", "Status"};
        concertTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        concertTable = new JTable(concertTableModel);
        UITheme.styleTable(concertTable);
        concertTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        concertTable.getColumnModel().getColumn(0).setMaxWidth(50);
        concertTable.getColumnModel().getColumn(4).setMaxWidth(110);

        JScrollPane sp = UITheme.makeScrollPane(concertTable);

        // Action buttons
        JButton addBtn    = UITheme.makePrimaryButton("Tambah Konser");
        JButton editBtn   = UITheme.makeGhostButton("Edit");
        JButton deleteBtn = UITheme.makeDangerButton("Hapus");
        JButton refreshBtn = UITheme.makeGhostButton("Refresh");

        addBtn.addActionListener(e -> showConcertDialog(null));
        editBtn.addActionListener(e -> {
            int row = concertTable.getSelectedRow();
            if (row < 0) { showWarning("Pilih konser yang ingin diedit."); return; }
            int cId = (int) concertTableModel.getValueAt(row, 0);
            Concert selected = concertList.stream().filter(c -> c.getConcertId() == cId).findFirst().orElse(null);
            if (selected != null) showConcertDialog(selected);
        });
        deleteBtn.addActionListener(e -> {
            int row = concertTable.getSelectedRow();
            if (row < 0) { showWarning("Pilih konser yang ingin dihapus."); return; }
            int cId = (int) concertTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus konser ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteConcert(cId);
                loadConcerts();
            }
        });
        refreshBtn.addActionListener(e -> loadConcerts());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));
        toolbar.add(addBtn); toolbar.add(editBtn); toolbar.add(deleteBtn); toolbar.add(refreshBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(sp,      BorderLayout.CENTER);
        return p;
    }

    private void loadConcerts() {
        concertList = concertCtrl.getAllConcerts();
        concertTableModel.setRowCount(0);
        for (Concert c : concertList) {
            concertTableModel.addRow(new Object[]{
                c.getConcertId(), c.getNamaKonser(), c.getLokasi(),
                sdf.format(c.getTanggalKonser()), c.getStatus()
            });
        }
    }

    private void showConcertDialog(Concert existing) {
        boolean isEdit = (existing != null);
        JDialog dialog = new JDialog(this, isEdit ? "Edit Konser" : "Tambah Konser", true);
        dialog.setSize(480, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = UITheme.makeCardPanel(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JTextField namaField   = UITheme.makeTextField(20);
        JTextField lokasiField = UITheme.makeTextField(20);
        JTextField tglField    = UITheme.makeTextField(20); // dd-MM-yyyy
        tglField.setText("yyyy-MM-dd");
        JTextArea  deskArea    = new JTextArea(3, 20);
        deskArea.setBackground(UITheme.BG_SURFACE);
        deskArea.setForeground(UITheme.TEXT_PRIMARY);
        deskArea.setFont(UITheme.FONT_INPUT);
        deskArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER_COLOR), new EmptyBorder(4, 8, 4, 8)));
        String[] statuses = {"UPCOMING", "ONGOING", "COMPLETED", "CANCELED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        UITheme.styleComboBox(statusCombo);

        if (isEdit) {
            namaField.setText(existing.getNamaKonser());
            lokasiField.setText(existing.getLokasi());
            tglField.setText(new java.sql.Date(existing.getTanggalKonser().getTime()).toString());
            deskArea.setText(existing.getDeskripsi());
            statusCombo.setSelectedItem(existing.getStatus());
        }

        addDialogRow(panel, "Nama Konser",  namaField);
        addDialogRow(panel, "Lokasi",        lokasiField);
        addDialogRow(panel, "Tanggal (yyyy-MM-dd)", tglField);
        addDialogRow(panel, "Deskripsi",     new JScrollPane(deskArea));
        addDialogRow(panel, "Status",        statusCombo);
        panel.add(Box.createVerticalStrut(16));

        JButton saveBtn = UITheme.makePrimaryButton(isEdit ? "Simpan Perubahan" : "Tambah Konser");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> {
            String nama   = namaField.getText().trim();
            String lokasi = lokasiField.getText().trim();
            String tgl    = tglField.getText().trim();
            String desk   = deskArea.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

            if (nama.isEmpty() || lokasi.isEmpty() || tgl.isEmpty()) {
                showWarning("Nama, lokasi, dan tanggal wajib diisi.");
                return;
            }
            java.sql.Date sqlDate;
            try { sqlDate = java.sql.Date.valueOf(tgl); }
            catch (Exception ex) { showWarning("Format tanggal salah. Gunakan yyyy-MM-dd."); return; }

            Concert c = new Concert(isEdit ? existing.getConcertId() : 0, nama, desk, lokasi, sqlDate, status);
            boolean ok;
            if (isEdit) { ok = updateConcert(c); }
            else        { ok = concertCtrl.addConcert(c); }

            if (ok) { dialog.dispose(); loadConcerts(); }
            else    { JOptionPane.showMessageDialog(dialog, "Gagal menyimpan.", "Error", JOptionPane.ERROR_MESSAGE); }
        });
        panel.add(saveBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private boolean updateConcert(Concert c) {
        String sql = "UPDATE concerts SET nama_konser=?, deskripsi=?, lokasi=?, tanggal_konser=?, status=? WHERE concert_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getNamaKonser());
            stmt.setString(2, c.getDeskripsi());
            stmt.setString(3, c.getLokasi());
            stmt.setDate(4, new java.sql.Date(c.getTanggalKonser().getTime()));
            stmt.setString(5, c.getStatus());
            stmt.setInt(6, c.getConcertId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private void deleteConcert(int id) {
        String sql = "DELETE FROM concerts WHERE concert_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  TAB 2: Kelola Kategori Tiket
    // ──────────────────────────────────────────────────────────────────────────
    private JPanel buildCategoryTab() {
        JPanel p = UITheme.makeDarkPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Concert filter dropdown
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel filterLbl = UITheme.makeLabel("Filter Konser:", UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        concertFilterCombo = new JComboBox<>();
        UITheme.styleComboBox(concertFilterCombo);
        concertFilterCombo.setPreferredSize(new Dimension(250, 34));
        concertFilterCombo.addActionListener(e -> loadCategories());

        JButton addCatBtn = UITheme.makePrimaryButton("Tambah Kategori");
        JButton editCatBtn  = UITheme.makeGhostButton("Edit");
        JButton deleteCatBtn = UITheme.makeDangerButton("Hapus");

        addCatBtn.addActionListener(e -> showCategoryDialog(null));
        editCatBtn.addActionListener(e -> {
            int row = catTable.getSelectedRow();
            if (row < 0) { showWarning("Pilih kategori yang ingin diedit."); return; }
            showCategoryDialog(row);
        });
        deleteCatBtn.addActionListener(e -> {
            int row = catTable.getSelectedRow();
            if (row < 0) { showWarning("Pilih kategori yang ingin dihapus."); return; }
            int catId = (int) catTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus kategori ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) { deleteCategory(catId); loadCategories(); }
        });

        filterRow.add(filterLbl); filterRow.add(concertFilterCombo);
        filterRow.add(Box.createHorizontalStrut(16));
        filterRow.add(addCatBtn); filterRow.add(editCatBtn); filterRow.add(deleteCatBtn);

        // Table
        String[] cols = {"ID", "Nama Kategori", "Harga", "Kuota", "Stok Tersedia"};
        catTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        catTable = new JTable(catTableModel);
        UITheme.styleTable(catTable);
        catTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane sp = UITheme.makeScrollPane(catTable);

        p.add(filterRow, BorderLayout.NORTH);
        p.add(sp,        BorderLayout.CENTER);
        return p;
    }

    private void loadConcertFilter() {
        concertList = concertCtrl.getAllConcerts();
        concertFilterCombo.removeAllItems();
        concertFilterCombo.addItem("-- Pilih Konser --");
        for (Concert c : concertList) {
            concertFilterCombo.addItem(c.getConcertId() + " | " + c.getNamaKonser());
        }
    }

    private void loadCategories() {
        catTableModel.setRowCount(0);
        int selectedIdx = concertFilterCombo.getSelectedIndex();
        if (selectedIdx <= 0 || concertList == null || concertList.isEmpty()) return;

        Concert selected = concertList.get(selectedIdx - 1);
        List<TicketCategory> cats = concertCtrl.getTicketCategoriesByConcertId(selected.getConcertId());
        for (TicketCategory tc : cats) {
            catTableModel.addRow(new Object[]{
                tc.getCategoryId(), tc.getNamaKategori(),
                rupiah.format(tc.getHarga()), tc.getKuota(), tc.getStokTersedia()
            });
        }
    }

    private void showCategoryDialog(Integer editRow) {
        boolean isEdit = (editRow != null);
        JDialog dialog = new JDialog(this, isEdit ? "Edit Kategori" : "Tambah Kategori", true);
        dialog.setSize(420, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = UITheme.makeCardPanel(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JTextField namaField  = UITheme.makeTextField(20);
        JTextField hargaField = UITheme.makeTextField(20);
        JTextField kuotaField = UITheme.makeTextField(20);
        JTextField stokField  = UITheme.makeTextField(20);

        if (isEdit) {
            namaField.setText(catTableModel.getValueAt(editRow, 1).toString());
            hargaField.setText(catTableModel.getValueAt(editRow, 2).toString()
                .replaceAll("[^0-9]", ""));
            kuotaField.setText(catTableModel.getValueAt(editRow, 3).toString());
            stokField.setText(catTableModel.getValueAt(editRow, 4).toString());
        }

        addDialogRow(panel, "Nama Kategori", namaField);
        addDialogRow(panel, "Harga (Rp)",    hargaField);
        addDialogRow(panel, "Kuota",         kuotaField);
        addDialogRow(panel, "Stok Tersedia", stokField);
        panel.add(Box.createVerticalStrut(16));

        JButton saveBtn = UITheme.makePrimaryButton(isEdit ? "Simpan" : "Tambah");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> {
            String nama = namaField.getText().trim();
            if (nama.isEmpty()) { showWarning("Nama kategori wajib diisi."); return; }
            try {
                double harga = Double.parseDouble(hargaField.getText().trim());
                int    kuota = Integer.parseInt(kuotaField.getText().trim());
                int    stok  = Integer.parseInt(stokField.getText().trim());

                int selectedIdx = concertFilterCombo.getSelectedIndex();
                if (selectedIdx <= 0) { showWarning("Pilih konser terlebih dahulu."); return; }
                Concert concert = concertList.get(selectedIdx - 1);

                boolean ok;
                if (isEdit) {
                    int catId = (int) catTableModel.getValueAt(editRow, 0);
                    ok = updateCategory(catId, nama, harga, kuota, stok);
                } else {
                    ok = addCategory(concert.getConcertId(), nama, harga, kuota, stok);
                }
                if (ok) { dialog.dispose(); loadCategories(); }
                else    { JOptionPane.showMessageDialog(dialog, "Gagal menyimpan.", "Error", JOptionPane.ERROR_MESSAGE); }
            } catch (NumberFormatException ex) {
                showWarning("Harga, kuota, dan stok harus berupa angka.");
            }
        });
        panel.add(saveBtn);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private boolean addCategory(int concertId, String nama, double harga, int kuota, int stok) {
        String sql = "INSERT INTO ticket_categories (concert_id, nama_kategori, harga, kuota, stok_tersedia) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, concertId); stmt.setString(2, nama); stmt.setDouble(3, harga);
            stmt.setInt(4, kuota); stmt.setInt(5, stok);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private boolean updateCategory(int id, String nama, double harga, int kuota, int stok) {
        String sql = "UPDATE ticket_categories SET nama_kategori=?, harga=?, kuota=?, stok_tersedia=? WHERE category_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama); stmt.setDouble(2, harga);
            stmt.setInt(3, kuota); stmt.setInt(4, stok); stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private void deleteCategory(int id) {
        String sql = "DELETE FROM ticket_categories WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id); stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  TAB 3: Transaksi
    // ──────────────────────────────────────────────────────────────────────────
    private JPanel buildTransactionTab() {
        JPanel p = UITheme.makeDarkPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        String[] cols = {"Order ID", "User", "Tanggal", "Total", "Status Order", "Status Bayar", "Metode"};
        txTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        txTable = new JTable(txTableModel);
        UITheme.styleTable(txTable);
        txTable.getColumnModel().getColumn(0).setMaxWidth(70);

        JScrollPane sp = UITheme.makeScrollPane(txTable);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        JButton refresh = UITheme.makeGhostButton("Refresh");
        refresh.addActionListener(e -> loadTransactions());
        toolbar.add(refresh);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(sp,      BorderLayout.CENTER);

        loadTransactions();
        return p;
    }

    private void loadTransactions() {
        txTableModel.setRowCount(0);
        String sql = """
            SELECT o.order_id, u.nama AS user_nama, o.tanggal_pesan, o.total_harga, o.status_order,
                   COALESCE(py.status_pembayaran, '-') AS status_bayar,
                   COALESCE(py.metode_pembayaran, '-') AS metode
            FROM orders o
            JOIN users u ON o.user_id = u.user_id
            LEFT JOIN payments py ON o.order_id = py.order_id
            ORDER BY o.order_id DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                txTableModel.addRow(new Object[]{
                    rs.getInt("order_id"),
                    rs.getString("user_nama"),
                    rs.getTimestamp("tanggal_pesan") != null
                        ? rs.getTimestamp("tanggal_pesan").toString().substring(0, 16) : "-",
                    rupiah.format(rs.getDouble("total_harga")),
                    rs.getString("status_order"),
                    rs.getString("status_bayar"),
                    rs.getString("metode")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  TAB 4: Laporan Penjualan
    // ──────────────────────────────────────────────────────────────────────────
    private JPanel buildReportTab() {
        JPanel p = UITheme.makeDarkPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        JButton genBtn = UITheme.makePrimaryButton("Generate Laporan");
        genBtn.addActionListener(e -> generateReport());
        toolbar.add(genBtn);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        reportArea.setForeground(UITheme.TEXT_PRIMARY);
        reportArea.setBackground(UITheme.BG_SURFACE);
        reportArea.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane sp = UITheme.makeScrollPane(reportArea);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(sp,      BorderLayout.CENTER);
        return p;
    }

    private void generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║           LAPORAN PENJUALAN TIKET — KONSERIN                ║\n");
        sb.append("║      Generated: ").append(new java.util.Date()).append("      \n");
        sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        // Summary
        String summarySQL = """
            SELECT
                COUNT(DISTINCT o.order_id)          AS total_order,
                SUM(CASE WHEN o.status_order='PAID' THEN o.total_harga ELSE 0 END) AS total_pendapatan,
                COUNT(DISTINCT CASE WHEN o.status_order='PAID' THEN o.order_id END)  AS order_sukses,
                COUNT(DISTINCT CASE WHEN o.status_order='CANCELED' THEN o.order_id END) AS order_batal,
                COUNT(DISTINCT e.eticket_id)         AS total_etiket
            FROM orders o
            LEFT JOIN e_tickets e ON o.order_id = e.order_id
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(summarySQL)) {
            if (rs.next()) {
                sb.append(String.format("  Total Order       : %d%n",   rs.getInt("total_order")));
                sb.append(String.format("  Order Sukses      : %d%n",   rs.getInt("order_sukses")));
                sb.append(String.format("  Order Dibatalkan  : %d%n",   rs.getInt("order_batal")));
                sb.append(String.format("  Total Pendapatan  : %s%n",   rupiah.format(rs.getDouble("total_pendapatan"))));
                sb.append(String.format("  E-Tiket Diterbitkan: %d%n",  rs.getInt("total_etiket")));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        sb.append("\n─────────────────────────────────────────────────────────────────\n");
        sb.append(" PENJUALAN PER KONSER\n");
        sb.append("─────────────────────────────────────────────────────────────────\n");
        sb.append(String.format("  %-30s %10s %12s%n", "Nama Konser", "Tiket Terjual", "Pendapatan"));
        sb.append("  " + "─".repeat(55) + "\n");

        String concertSQL = """
            SELECT c.nama_konser,
                   SUM(od.jumlah_tiket) AS total_tiket,
                   SUM(od.subtotal) AS total_rev
            FROM concerts c
            JOIN ticket_categories tc ON c.concert_id = tc.concert_id
            JOIN order_details od ON tc.category_id = od.category_id
            JOIN orders o ON od.order_id = o.order_id
            WHERE o.status_order = 'PAID'
            GROUP BY c.concert_id, c.nama_konser
            ORDER BY total_rev DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(concertSQL)) {
            while (rs.next()) {
                sb.append(String.format("  %-30s %10d %12s%n",
                    truncate(rs.getString("nama_konser"), 28),
                    rs.getInt("total_tiket"),
                    rupiah.format(rs.getDouble("total_rev"))));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        sb.append("\n─────────────────────────────────────────────────────────────────\n");
        sb.append(" PENJUALAN PER KATEGORI TIKET\n");
        sb.append("─────────────────────────────────────────────────────────────────\n");
        sb.append(String.format("  %-22s %-18s %8s %12s%n", "Konser", "Kategori", "Terjual", "Subtotal"));
        sb.append("  " + "─".repeat(64) + "\n");

        String catSQL = """
            SELECT c.nama_konser, tc.nama_kategori,
                   SUM(od.jumlah_tiket) AS total_tiket,
                   SUM(od.subtotal) AS subtotal
            FROM concerts c
            JOIN ticket_categories tc ON c.concert_id = tc.concert_id
            JOIN order_details od ON tc.category_id = od.category_id
            JOIN orders o ON od.order_id = o.order_id
            WHERE o.status_order = 'PAID'
            GROUP BY c.nama_konser, tc.nama_kategori
            ORDER BY c.nama_konser, subtotal DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(catSQL)) {
            while (rs.next()) {
                sb.append(String.format("  %-22s %-18s %8d %12s%n",
                    truncate(rs.getString("nama_konser"), 20),
                    truncate(rs.getString("nama_kategori"), 16),
                    rs.getInt("total_tiket"),
                    rupiah.format(rs.getDouble("subtotal"))));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        sb.append("\n╚══════════════ AKHIR LAPORAN ══════════════╝\n");
        reportArea.setText(sb.toString());
        reportArea.setCaretPosition(0);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void addDialogRow(JPanel panel, String label, Component field) {
        JLabel lbl = UITheme.makeLabel(label, UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        if (field instanceof JComponent jc) {
            jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            jc.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
