package com.konserin.views;

import com.konserin.controllers.ConcertController;
import com.konserin.models.Concert;
import com.konserin.models.User;
import com.konserin.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * UserDashboardView — Halaman utama user yang menampilkan daftar konser.
 * Setiap kartu konser bisa diklik untuk membuka ConcertDetailView.
 */
public class UserDashboardView extends JFrame {

    private final User                user;
    private final ConcertController   concertCtrl = new ConcertController();
    private final SimpleDateFormat    sdf         = new SimpleDateFormat("dd MMM yyyy");

    public UserDashboardView(User user) {
        this.user = user;
        setTitle("KONSERIN — Hai, " + user.getNama() + "!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 640);
        setLocationRelativeTo(null);

        JPanel root = UITheme.makeDarkPanel(new BorderLayout());
        setContentPane(root);

        root.add(buildNavBar(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.BG_CARD);
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(14, 24, 14, 24)
        ));

        JLabel logo = UITheme.makeLabel("KONSERIN", UITheme.FONT_HEADING, UITheme.ACCENT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel userName = UITheme.makeLabel(user.getNama(), UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        JButton riwayat  = UITheme.makeGhostButton("Riwayat");
        JButton logout   = UITheme.makeDangerButton("Keluar");

        riwayat.addActionListener(e -> new RiwayatTransaksiView(user).setVisible(true));
        logout.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        right.add(userName);
        right.add(riwayat);
        right.add(logout);

        nav.add(logo, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    // ── Main Content ──────────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel outer = UITheme.makeDarkPanel(new BorderLayout());
        outer.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Section Title
        JLabel sectionTitle = UITheme.makeLabel("Konser Tersedia", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        outer.add(sectionTitle, BorderLayout.NORTH);

        // Concert Grid in a scroll pane
        JPanel grid = UITheme.makeDarkPanel(new GridLayout(0, 3, 16, 16));
        grid.setBorder(new EmptyBorder(16, 0, 0, 0));

        List<Concert> concerts = concertCtrl.getAllConcerts();
        if (concerts.isEmpty()) {
            JLabel empty = UITheme.makeLabel("Belum ada konser tersedia.", UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            outer.add(empty, BorderLayout.CENTER);
        } else {
            for (Concert c : concerts) {
                grid.add(buildConcertCard(c));
            }
            JScrollPane sp = UITheme.makeScrollPane(grid);
            sp.setBorder(null);
            sp.getViewport().setBackground(UITheme.BG_DARK);
            sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            outer.add(sp, BorderLayout.CENTER);
        }

        return outer;
    }

    // ── Concert Card ──────────────────────────────────────────────────────────
    private JPanel buildConcertCard(Concert c) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Status badge
        Color badgeColor = switch (c.getStatus()) {
            case "UPCOMING"  -> UITheme.ACCENT_PRIMARY;
            case "ONGOING"   -> UITheme.ACCENT_SUCCESS;
            case "COMPLETED" -> UITheme.TEXT_SECONDARY;
            default          -> UITheme.ACCENT_DANGER;
        };
        JLabel statusBadge = makeStatusBadge(c.getStatus(), badgeColor);

        // Top row: status
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRow.setOpaque(false);
        topRow.add(statusBadge);

        // Concert info
        JLabel namaLbl   = UITheme.makeLabel("<html><b>" + c.getNamaKonser() + "</b></html>",
                                              UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        JLabel lokasiLbl = UITheme.makeLabel("Lokasi: " + c.getLokasi(), UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        JLabel tglLbl    = UITheme.makeLabel("Tanggal: " + sdf.format(c.getTanggalKonser()), UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);

        JButton detailBtn = UITheme.makePrimaryButton("Lihat Detail →");
        detailBtn.addActionListener(e -> new ConcertDetailView(user, c).setVisible(true));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        namaLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lokasiLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        tglLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(namaLbl);
        info.add(Box.createVerticalStrut(6));
        info.add(lokasiLbl);
        info.add(Box.createVerticalStrut(4));
        info.add(tglLbl);
        info.add(Box.createVerticalStrut(14));
        info.add(detailBtn);

        card.add(topRow, BorderLayout.NORTH);
        card.add(info,   BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.ACCENT_PRIMARY, 1, true),
                    new EmptyBorder(15, 15, 15, 15)
                ));
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(16, 16, 16, 16));
            }
        });

        return card;
    }

    private JLabel makeStatusBadge(String text, Color bg) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        return lbl;
    }
}
