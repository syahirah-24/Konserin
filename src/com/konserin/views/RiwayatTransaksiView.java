package com.konserin.views;

import com.konserin.models.User;
import com.konserin.utils.DBConnection;
import com.konserin.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * RiwayatTransaksiView — Menampilkan riwayat transaksi/pesanan seorang user
 * beserta tombol "Lihat E-Tiket" untuk order yang sudah dibayar (PAID).
 */
public class RiwayatTransaksiView extends JFrame {

    private final User         user;
    private final NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
    private JTable             table;
    private DefaultTableModel  tableModel;

    public RiwayatTransaksiView(User user) {
        this.user = user;
        setTitle("KONSERIN — Riwayat Transaksi");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);

        JPanel root = UITheme.makeDarkPanel(new BorderLayout());
        setContentPane(root);

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildHeader() {
        JPanel h = UITheme.makeCardPanel(new BorderLayout());
        h.setBorder(new EmptyBorder(16, 24, 16, 24));
        JLabel title = UITheme.makeLabel("Riwayat Transaksi — " + user.getNama(),
                                          UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        JButton refresh = UITheme.makeGhostButton("Refresh");
        refresh.addActionListener(e -> loadData());
        h.add(title,   BorderLayout.WEST);
        h.add(refresh, BorderLayout.EAST);
        return h;
    }

    private JPanel buildContent() {
        JPanel outer = UITheme.makeDarkPanel(new BorderLayout());
        outer.setBorder(new EmptyBorder(0, 24, 24, 24));

        String[] cols = {"Order ID", "Tanggal", "Total Harga", "Status", "E-Tiket"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 4; }
            @Override public Class<?> getColumnClass(int col) {
                return col == 4 ? JButton.class : String.class;
            }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Button renderer + editor for E-Tiket column
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), this));
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(3).setMaxWidth(110);
        table.getColumnModel().getColumn(4).setMaxWidth(130);

        JScrollPane sp = UITheme.makeScrollPane(table);
        outer.add(sp, BorderLayout.CENTER);
        return outer;
    }

    public void loadData() {
        tableModel.setRowCount(0);
        String sql = """
            SELECT o.order_id, o.tanggal_pesan, o.total_harga, o.status_order,
                   e.kode_tiket
            FROM orders o
            LEFT JOIN e_tickets e ON o.order_id = e.order_id
            WHERE o.user_id = ?
            ORDER BY o.order_id DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int    orderId  = rs.getInt("order_id");
                String tgl      = rs.getTimestamp("tanggal_pesan") != null
                                    ? rs.getTimestamp("tanggal_pesan").toString().substring(0, 16) : "-";
                String total    = rupiah.format(rs.getDouble("total_harga"));
                String status   = rs.getString("status_order");
                String etiket   = rs.getString("kode_tiket");
                String btnLabel = (etiket != null) ? "Lihat Tiket" : "-";

                tableModel.addRow(new Object[]{orderId, tgl, total, status, btnLabel});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Shows e-ticket popup for the selected row. */
    public void showETicket(int row) {
        int orderId = (int) tableModel.getValueAt(row, 0);
        String sql = "SELECT * FROM e_tickets WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                showETicketDialog(
                    rs.getString("kode_tiket"),
                    rs.getString("qr_code"),
                    rs.getString("status_tiket"),
                    rs.getTimestamp("tanggal_terbit") != null ? rs.getTimestamp("tanggal_terbit").toString() : "-"
                );
            } else {
                JOptionPane.showMessageDialog(this, "E-Tiket belum tersedia untuk order ini.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showETicketDialog(String kode, String qr, String status, String tanggal) {
        JDialog dialog = new JDialog(this, "E-Tiket", true);
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = UITheme.makeCardPanel(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        // QR Code visual simulation (ASCII art box)
        JTextArea qrVisual = new JTextArea(buildQRVisual(kode));
        qrVisual.setEditable(false);
        qrVisual.setFont(new Font("Monospaced", Font.PLAIN, 11));
        qrVisual.setForeground(UITheme.ACCENT_PRIMARY);
        qrVisual.setBackground(UITheme.BG_CARD);
        qrVisual.setBorder(new EmptyBorder(8, 8, 8, 8));
        qrVisual.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl  = UITheme.makeLabel("E-TIKET KONSERIN", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        JLabel kodeLbl   = UITheme.makeLabel("Kode: " + kode, UITheme.FONT_BODY, UITheme.ACCENT_PRIMARY);
        JLabel qrLbl     = UITheme.makeLabel("QR: " + qr, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        JLabel statusLbl = UITheme.makeLabel("Status: " + status, UITheme.FONT_SMALL,
                                              status.equals("ACTIVE") ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_DANGER);
        JLabel tglLbl    = UITheme.makeLabel("Terbit: " + tanggal.substring(0, 16), UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);

        JButton closeBtn = UITheme.makePrimaryButton("Tutup");
        closeBtn.addActionListener(e -> dialog.dispose());
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (Component c : new Component[]{titleLbl, Box.createVerticalStrut(14),
                                           qrVisual, Box.createVerticalStrut(14),
                                           kodeLbl, qrLbl, statusLbl, tglLbl,
                                           Box.createVerticalStrut(18), closeBtn}) {
            if (c instanceof JComponent jc) jc.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(c);
            if (!(c instanceof Box.Filler)) panel.add(Box.createVerticalStrut(4));
        }

        dialog.setContentPane(panel);
        dialog.getContentPane().setBackground(UITheme.BG_CARD);
        dialog.setVisible(true);
    }

    private String buildQRVisual(String code) {
        // Simple text-based "QR" representation
        StringBuilder sb = new StringBuilder();
        sb.append("┌─────────────────────┐\n");
        sb.append("│ ██  ██ ████ ██  ██  │\n");
        sb.append("│ ██  ██  ██  ████    │\n");
        sb.append("│ ████    ██  ██  ██  │\n");
        sb.append("│  ").append(centerPad(code.substring(0, Math.min(code.length(), 18)), 18)).append("  │\n");
        sb.append("│ ██  ██ ████ ██  ██  │\n");
        sb.append("│ ████    ██  ████    │\n");
        sb.append("└─────────────────────┘");
        return sb.toString();
    }

    private String centerPad(String s, int width) {
        int pad = (width - s.length()) / 2;
        return " ".repeat(pad) + s + " ".repeat(width - s.length() - pad);
    }

    // ── Button Renderer ───────────────────────────────────────────────────────
    static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(UITheme.ACCENT_PRIMARY);
            setForeground(Color.WHITE);
            setFont(UITheme.FONT_SMALL);
            setFocusPainted(false);
            setBorderPainted(false);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            String txt = val != null ? val.toString() : "-";
            setText(txt);
            setEnabled(!txt.equals("-"));
            return this;
        }
    }

    // ── Button Editor ─────────────────────────────────────────────────────────
    static class ButtonEditor extends DefaultCellEditor {
        private final RiwayatTransaksiView parent;
        private String label;
        private int    currentRow;
        private final JButton btn;

        public ButtonEditor(JCheckBox cb, RiwayatTransaksiView parent) {
            super(cb);
            this.parent = parent;
            btn = new JButton();
            btn.setBackground(UITheme.ACCENT_PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setFont(UITheme.FONT_SMALL);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.addActionListener(e -> {
                fireEditingStopped();
                if (!label.equals("-")) parent.showETicket(currentRow);
            });
        }

        @Override public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
            label = val != null ? val.toString() : "-";
            currentRow = row;
            btn.setText(label);
            btn.setEnabled(!label.equals("-"));
            return btn;
        }

        @Override public Object getCellEditorValue() { return label; }
    }
}
