package com.konserin.views;

import com.konserin.controllers.AuthController;
import com.konserin.models.Admin;
import com.konserin.models.User;
import com.konserin.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * LoginView — Tampilan login untuk Admin dan User (Pembeli Tiket).
 * Mendukung toggle antara tab "User" dan "Admin".
 */
public class LoginView extends JFrame {

    private final AuthController authController = new AuthController();

    // Fields
    private JTextField  emailField;
    private JPasswordField passwordField;
    private JPanel      tabUser, tabAdmin;
    private boolean     isAdminMode = false;

    public LoginView() {
        setTitle("KONSERIN — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = UITheme.makeDarkPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(root);

        // ── Left gradient stripe ──────────────────────────────────────────
        JPanel stripe = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UITheme.ACCENT_PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        stripe.setPreferredSize(new Dimension(8, 0));
        root.add(stripe, BorderLayout.WEST);

        // ── Main card ─────────────────────────────────────────────────────
        JPanel card = UITheme.makeCardPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        root.add(card, BorderLayout.CENTER);

        // Header
        JPanel header = UITheme.makeDarkPanel(new BorderLayout());
        header.setBackground(UITheme.BG_CARD);
        JLabel logo = UITheme.makeLabel("KONSERIN", UITheme.FONT_TITLE, UITheme.ACCENT_PRIMARY);
        JLabel sub  = UITheme.makeLabel("Sistem Informasi Penjualan Tiket Konser", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        header.add(logo, BorderLayout.NORTH);
        header.add(sub,  BorderLayout.CENTER);
        header.setBorder(new EmptyBorder(0, 0, 28, 0));
        card.add(header, BorderLayout.NORTH);

        // Center form
        JPanel form = UITheme.makeCardPanel(null);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        buildForm(form);
        card.add(form, BorderLayout.CENTER);

        // Footer
        JPanel footer = UITheme.makeCardPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));
        JLabel noAcc = UITheme.makeLabel("Belum punya akun?", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        JLabel daftar = UITheme.makeLabel("Daftar di sini", UITheme.FONT_SMALL, UITheme.ACCENT_PRIMARY);
        daftar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        daftar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterView().setVisible(true);
            }
            @Override public void mouseEntered(MouseEvent e) { daftar.setForeground(UITheme.ACCENT_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { daftar.setForeground(UITheme.ACCENT_PRIMARY); }
        });
        footer.add(noAcc);
        footer.add(daftar);
        card.add(footer, BorderLayout.SOUTH);
    }

    private void buildForm(JPanel form) {
        // ── Role Tabs ────────────────────────────────────────────────────────
        JPanel tabs = new JPanel(new GridLayout(1, 2, 4, 0));
        tabs.setBackground(UITheme.BG_SURFACE);
        tabs.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tabs.setAlignmentX(Component.LEFT_ALIGNMENT);

        tabUser  = makeTab("User",  true);
        tabAdmin = makeTab("Admin", false);
        tabs.add(tabUser);
        tabs.add(tabAdmin);

        tabUser.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { switchTab(false); }
        });
        tabAdmin.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { switchTab(true); }
        });

        form.add(tabs);
        form.add(Box.createVerticalStrut(24));

        // ── Email ────────────────────────────────────────────────────────────
        JLabel emailLbl = UITheme.makeLabel("Email", UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(emailLbl);
        form.add(Box.createVerticalStrut(6));

        emailField = UITheme.makeTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(emailField);
        form.add(Box.createVerticalStrut(16));

        // ── Password ─────────────────────────────────────────────────────────
        JLabel passLbl = UITheme.makeLabel("Password", UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passLbl);
        form.add(Box.createVerticalStrut(6));

        passwordField = UITheme.makePasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passwordField);
        form.add(Box.createVerticalStrut(28));

        // ── Login Button ─────────────────────────────────────────────────────
        JButton loginBtn = UITheme.makePrimaryButton("Login →");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.addActionListener(e -> handleLogin());
        form.add(loginBtn);
    }

    private JPanel makeTab(String text, boolean active) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(active ? UITheme.ACCENT_PRIMARY : UITheme.BG_SURFACE);
        JLabel lbl = UITheme.makeLabel(text, UITheme.FONT_BUTTON,
                                        active ? Color.WHITE : UITheme.TEXT_SECONDARY);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        p.add(lbl, BorderLayout.CENTER);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.setBorder(new EmptyBorder(6, 0, 6, 0));
        return p;
    }

    private void switchTab(boolean adminMode) {
        isAdminMode = adminMode;
        // Repaint tabs
        tabUser.setBackground( adminMode ? UITheme.BG_SURFACE : UITheme.ACCENT_PRIMARY);
        tabAdmin.setBackground(adminMode ? UITheme.ACCENT_PRIMARY : UITheme.BG_SURFACE);
        ((JLabel) tabUser.getComponent(0)).setForeground( adminMode ? UITheme.TEXT_SECONDARY : Color.WHITE);
        ((JLabel) tabAdmin.getComponent(0)).setForeground(adminMode ? Color.WHITE : UITheme.TEXT_SECONDARY);
        emailField.setText("");
        passwordField.setText("");
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passwordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Email dan password tidak boleh kosong.");
            return;
        }

        if (isAdminMode) {
            Admin admin = authController.loginAdmin(email, pass);
            if (admin != null) {
                dispose();
                new AdminDashboardView(admin).setVisible(true);
            } else {
                showError("Email atau password admin salah.");
            }
        } else {
            User user = authController.loginUser(email, pass);
            if (user != null) {
                dispose();
                new UserDashboardView(user).setVisible(true);
            } else {
                showError("Email atau password salah. Silakan coba lagi.");
            }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Login Gagal", JOptionPane.ERROR_MESSAGE);
    }
}
