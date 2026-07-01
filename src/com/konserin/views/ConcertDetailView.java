package com.konserin.views;

import com.konserin.controllers.ConcertController;
import com.konserin.controllers.PaymentController;
import com.konserin.controllers.TransactionController;
import com.konserin.models.*;
import com.konserin.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ConcertDetailView — Halaman detail konser:
 *  1. Info konser + daftar kategori tiket dengan pilihan jumlah.
 *  2. Ringkasan pesanan + konfirmasi.
 *  3. Halaman pembayaran simulasi.
 *  4. Konfirmasi pembayaran berhasil / gagal.
 */
public class ConcertDetailView extends JFrame {

    private final User                  user;
    private final Concert               concert;
    private final ConcertController     concertCtrl   = new ConcertController();
    private final TransactionController txCtrl        = new TransactionController();
    private final PaymentController     payCtrl       = new PaymentController();
    private final NumberFormat          rupiah        = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
    private final SimpleDateFormat      sdf           = new SimpleDateFormat("dd MMM yyyy");

    // State for order
    private List<TicketCategory> categories;
    private List<JSpinner>       spinners = new ArrayList<>();
    private JLabel               totalLabel;
    private int                  savedOrderId = -1;
    private double               savedTotal   = 0;

    // CardLayout pages
    private CardLayout cardLayout;
    private JPanel     cardPanel;

    public ConcertDetailView(User user, Concert concert) {
        this.user    = user;
        this.concert = concert;
        setTitle("KONSERIN — " + concert.getNamaKonser());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(680, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        categories = concertCtrl.getTicketCategoriesByConcertId(concert.getConcertId());

        JPanel root = UITheme.makeDarkPanel(new BorderLayout());
        setContentPane(root);

        cardLayout = new CardLayout();
        cardPanel  = UITheme.makeDarkPanel(cardLayout);

        cardPanel.add(buildDetailPage(),  "detail");
        cardPanel.add(buildPaymentPage(), "payment");

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(cardPanel,     BorderLayout.CENTER);

        cardLayout.show(cardPanel, "detail");
    }

    // ── Shared Header ─────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(16, 24, 16, 24)
        ));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = UITheme.makeLabel(concert.getNamaKonser(), UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel loc  = UITheme.makeLabel("Lokasi: " + concert.getLokasi() + "   Tanggal: " + sdf.format(concert.getTanggalKonser()),
                                         UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        info.add(name);
        info.add(Box.createVerticalStrut(4));
        info.add(loc);

        JButton back = UITheme.makeGhostButton("Kembali");
        back.addActionListener(e -> dispose());

        header.add(info, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  PAGE 1: Detail + Pilih Tiket
    // ──────────────────────────────────────────────────────────────────────────
    private JPanel buildDetailPage() {
        JPanel page = UITheme.makeDarkPanel(new BorderLayout());
        page.setBorder(new EmptyBorder(20, 28, 20, 28));

        // Deskripsi
        JTextArea desc = new JTextArea(concert.getDeskripsi());
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(UITheme.TEXT_SECONDARY);
        desc.setBackground(UITheme.BG_DARK);
        desc.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Kategori tiket
        JLabel catTitle = UITheme.makeLabel("Pilih Kategori Tiket", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);

        JPanel catPanel = UITheme.makeDarkPanel(new GridLayout(0, 1, 0, 10));
        catPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        spinners.clear();

        for (TicketCategory tc : categories) {
            catPanel.add(buildCategoryRow(tc));
        }

        JScrollPane catScroll = UITheme.makeScrollPane(catPanel);
        catScroll.setBorder(null);
        catScroll.getViewport().setBackground(UITheme.BG_DARK);

        // Total
        JPanel totalRow = UITheme.makeDarkPanel(new BorderLayout());
        totalRow.setBorder(new EmptyBorder(14, 0, 0, 0));
        JLabel totalKey = UITheme.makeLabel("Total Harga:", UITheme.FONT_HEADING, UITheme.TEXT_SECONDARY);
        totalLabel = UITheme.makeLabel("Rp 0", UITheme.FONT_TITLE, UITheme.ACCENT_PRIMARY);
        totalRow.add(totalKey,   BorderLayout.WEST);
        totalRow.add(totalLabel, BorderLayout.EAST);

        JButton orderBtn = UITheme.makePrimaryButton("Pesan Sekarang →");
        orderBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        orderBtn.addActionListener(e -> handleOrder());

        JPanel bottom = UITheme.makeDarkPanel(new BorderLayout(0, 10));
        bottom.add(totalRow, BorderLayout.NORTH);
        bottom.add(orderBtn, BorderLayout.SOUTH);

        JPanel topSection = UITheme.makeDarkPanel(new BorderLayout());
        topSection.add(desc,     BorderLayout.NORTH);
        topSection.add(catTitle, BorderLayout.CENTER);

        page.add(topSection, BorderLayout.NORTH);
        page.add(catScroll,  BorderLayout.CENTER);
        page.add(bottom,     BorderLayout.SOUTH);

        return page;
    }

    private JPanel buildCategoryRow(TicketCategory tc) {
        JPanel row = UITheme.makeCardPanel(new BorderLayout(12, 0));
        row.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Left: info
        JPanel left = UITheme.makeCardPanel(null);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel nameLbl  = UITheme.makeLabel(tc.getNamaKategori(), UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        JLabel hargaLbl = UITheme.makeLabel(rupiah.format(tc.getHarga()), UITheme.FONT_BODY, UITheme.ACCENT_PRIMARY);
        JLabel stokLbl  = UITheme.makeLabel("Stok: " + tc.getStokTersedia(), UITheme.FONT_SMALL,
                                             tc.getStokTersedia() > 0 ? UITheme.ACCENT_SUCCESS : UITheme.ACCENT_DANGER);
        left.add(nameLbl);
        left.add(hargaLbl);
        left.add(stokLbl);

        // Right: spinner
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0,
                                                           Math.max(tc.getStokTersedia(), 0), 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(UITheme.FONT_BODY);
        spinner.setPreferredSize(new Dimension(80, 36));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField()
            .setBackground(UITheme.BG_SURFACE);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField()
            .setForeground(UITheme.TEXT_PRIMARY);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField()
            .setHorizontalAlignment(JTextField.CENTER);
        spinner.setBorder(new LineBorder(UITheme.BORDER_COLOR));
        spinner.setEnabled(tc.getStokTersedia() > 0);
        spinner.addChangeListener(e -> recalcTotal());
        spinners.add(spinner);

        row.add(left,   BorderLayout.WEST);
        row.add(spinner, BorderLayout.EAST);
        return row;
    }

    private void recalcTotal() {
        double total = 0;
        for (int i = 0; i < categories.size(); i++) {
            int qty = (int) spinners.get(i).getValue();
            total += categories.get(i).getHarga() * qty;
        }
        savedTotal = total;
        totalLabel.setText(rupiah.format(total));
    }

    private void handleOrder() {
        // Validate at least 1 ticket selected
        boolean anySelected = spinners.stream().anyMatch(s -> (int) s.getValue() > 0);
        if (!anySelected) {
            JOptionPane.showMessageDialog(this, "Pilih minimal 1 tiket untuk melanjutkan.", "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        recalcTotal();

        // Build Order + OrderDetails
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalHarga(savedTotal);

        List<OrderDetail> details = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            int qty = (int) spinners.get(i).getValue();
            if (qty > 0) {
                OrderDetail od = new OrderDetail();
                od.setCategoryId(categories.get(i).getCategoryId());
                od.setJumlahTiket(qty);
                od.setSubtotal(categories.get(i).getHarga() * qty);
                details.add(od);
            }
        }

        boolean ok = txCtrl.createOrder(order, details);
        if (ok) {
            // Retrieve last inserted orderId via a workaround — store it from controller
            // (controller returns generated key; here we fetch from DB via simple approach)
            savedOrderId = fetchLastOrderId();
            cardLayout.show(cardPanel, "payment");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal membuat pesanan. Coba lagi.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Fetch the latest order id for current user (simple approach). */
    private int fetchLastOrderId() {
        try (var conn = com.konserin.utils.DBConnection.getConnection();
             var stmt = conn.prepareStatement(
                 "SELECT order_id FROM orders WHERE user_id = ? ORDER BY order_id DESC LIMIT 1")) {
            stmt.setInt(1, user.getId());
            var rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("order_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  PAGE 2: Payment
    // ──────────────────────────────────────────────────────────────────────────
    private JPanel buildPaymentPage() {
        JPanel page = UITheme.makeDarkPanel(new BorderLayout());
        page.setBorder(new EmptyBorder(24, 40, 24, 40));

        JLabel title = UITheme.makeLabel("Konfirmasi Pembayaran", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        page.add(title, BorderLayout.NORTH);

        JPanel form = UITheme.makeCardPanel(null);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Order Summary
        JLabel orderSummaryTitle = UITheme.makeLabel("Ringkasan Pesanan", UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        JLabel concertLbl = UITheme.makeLabel("Konser: " + concert.getNamaKonser(), UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        JLabel totalLbl   = UITheme.makeLabel("Total: " + rupiah.format(savedTotal), UITheme.FONT_HEADING, UITheme.ACCENT_PRIMARY);

        // Method selection
        JLabel methodLbl = UITheme.makeLabel("Metode Pembayaran", UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        String[] methods = {"Transfer Bank (BCA)", "Transfer Bank (Mandiri)", "GoPay", "OVO", "DANA", "Kartu Kredit"};
        JComboBox<String> methodCombo = new JComboBox<>(methods);
        UITheme.styleComboBox(methodCombo);
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Buttons
        JButton payBtn    = UITheme.makePrimaryButton("Bayar Sekarang");
        JButton cancelBtn = UITheme.makeDangerButton("Batalkan Pesanan");
        payBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        cancelBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        payBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        payBtn.addActionListener(e -> {
            String method = (String) methodCombo.getSelectedItem();
            handlePayment(method);
        });
        cancelBtn.addActionListener(e -> {
            cancelOrder();
            cardLayout.show(cardPanel, "detail");
        });

        for (Component c : new Component[]{orderSummaryTitle, Box.createVerticalStrut(4),
                                           concertLbl, Box.createVerticalStrut(2),
                                           totalLbl,   Box.createVerticalStrut(20),
                                           sep,        Box.createVerticalStrut(16),
                                           methodLbl,  Box.createVerticalStrut(8),
                                           methodCombo, Box.createVerticalStrut(24),
                                           payBtn,     Box.createVerticalStrut(10),
                                           cancelBtn}) {
            if (c instanceof JComponent jc) {
                jc.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            form.add(c);
        }

        page.add(form, BorderLayout.CENTER);
        return page;
    }

    private void handlePayment(String method) {
        if (savedOrderId == -1) {
            JOptionPane.showMessageDialog(this, "Order ID tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Payment payment = new Payment();
        payment.setOrderId(savedOrderId);
        payment.setMetodePembayaran(method);
        payment.setJumlahBayar(savedTotal);

        boolean success = payCtrl.processPayment(payment);
        if (success) {
            JOptionPane.showMessageDialog(this,
                "✅ Pembayaran Berhasil!\n\nE-Tiket telah diterbitkan.\nLihat di halaman Riwayat Transaksi.",
                "Pembayaran Berhasil", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            int choice = JOptionPane.showConfirmDialog(this,
                "❌ Pembayaran Gagal!\n\nApakah Anda ingin mencoba membayar ulang?",
                "Pembayaran Gagal", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (choice == JOptionPane.NO_OPTION) {
                cancelOrder();
                dispose();
            }
        }
    }

    private void cancelOrder() {
        if (savedOrderId != -1) {
            try (var conn = com.konserin.utils.DBConnection.getConnection();
                 var stmt = conn.prepareStatement(
                     "UPDATE orders SET status_order = 'CANCELED' WHERE order_id = ?")) {
                stmt.setInt(1, savedOrderId);
                stmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
