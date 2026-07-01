package com.konserin.views;

import com.konserin.controllers.AuthController;
import com.konserin.models.User;
import com.konserin.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * RegisterView — Form registrasi akun baru untuk User/Pembeli Tiket.
 */
public class RegisterView extends JFrame {

    private final AuthController authController = new AuthController();

    private JTextField     namaField, emailField, noTelpField;
    private JPasswordField passField, confirmPassField;

    public RegisterView() {
        setTitle("KONSERIN — Registrasi Akun");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = UITheme.makeDarkPanel(new BorderLayout());
        setContentPane(root);

        // Gradient stripe
        JPanel stripe = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UITheme.ACCENT_PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        stripe.setPreferredSize(new Dimension(8, 0));
        root.add(stripe, BorderLayout.WEST);

        JPanel card = UITheme.makeCardPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        root.add(card, BorderLayout.CENTER);

        // Header
        JPanel header = UITheme.makeCardPanel(new BorderLayout());
        JLabel title = UITheme.makeLabel("Buat Akun Baru", UITheme.FONT_TITLE, UITheme.ACCENT_PRIMARY);
        JLabel sub   = UITheme.makeLabel("Bergabunglah dan nikmati konser favoritmu!", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        header.add(title, BorderLayout.NORTH);
        header.add(sub,   BorderLayout.CENTER);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
        card.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = UITheme.makeCardPanel(null);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        buildForm(form);
        card.add(form, BorderLayout.CENTER);

        // Footer
        JPanel footer = UITheme.makeCardPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));
        JLabel haveAcc = UITheme.makeLabel("Sudah punya akun?", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        JLabel loginLink = UITheme.makeLabel("Login di sini", UITheme.FONT_SMALL, UITheme.ACCENT_PRIMARY);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginView().setVisible(true);
            }
            @Override public void mouseEntered(MouseEvent e) { loginLink.setForeground(UITheme.ACCENT_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { loginLink.setForeground(UITheme.ACCENT_PRIMARY); }
        });
        footer.add(haveAcc);
        footer.add(loginLink);
        card.add(footer, BorderLayout.SOUTH);
    }

    private void buildForm(JPanel form) {
        // Nama
        addLabel(form, "Nama Lengkap");
        namaField = UITheme.makeTextField(20);
        namaField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        namaField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(namaField);
        form.add(Box.createVerticalStrut(14));

        // Email
        addLabel(form, "Email");
        emailField = UITheme.makeTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(emailField);
        form.add(Box.createVerticalStrut(14));

        // No Telp
        addLabel(form, "No. Telepon");
        noTelpField = UITheme.makeTextField(20);
        noTelpField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        noTelpField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(noTelpField);
        form.add(Box.createVerticalStrut(14));

        // Password
        addLabel(form, "Password");
        passField = UITheme.makePasswordField(20);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passField);
        form.add(Box.createVerticalStrut(14));

        // Confirm Password
        addLabel(form, "Konfirmasi Password");
        confirmPassField = UITheme.makePasswordField(20);
        confirmPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        confirmPassField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(confirmPassField);
        form.add(Box.createVerticalStrut(26));

        // Register Button
        JButton regBtn = UITheme.makePrimaryButton("Daftar Sekarang →");
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        regBtn.addActionListener(e -> handleRegister());
        form.add(regBtn);
    }

    private void addLabel(JPanel form, String text) {
        JLabel lbl = UITheme.makeLabel(text, UITheme.FONT_BODY, UITheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lbl);
        form.add(Box.createVerticalStrut(6));
    }

    private void handleRegister() {
        String nama      = namaField.getText().trim();
        String email     = emailField.getText().trim();
        String noTelp    = noTelpField.getText().trim();
        String pass      = new String(passField.getPassword());
        String confirm   = new String(confirmPassField.getPassword());

        if (nama.isEmpty() || email.isEmpty() || noTelp.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi.", "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Password dan konfirmasi tidak cocok.", "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid.", "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = new User();
        newUser.setNama(nama);
        newUser.setEmail(email);
        newUser.setPassword(pass);
        newUser.setNoTelp(noTelp);

        boolean success = authController.registerUser(newUser);
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Akun berhasil dibuat! Silakan login.", "Registrasi Berhasil", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginView().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Registrasi gagal. Email mungkin sudah terdaftar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
