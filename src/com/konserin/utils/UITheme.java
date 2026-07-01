package com.konserin.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Centralised UI Theme constants and helper factories for KONSERIN.
 * All Views must use these constants to keep the design consistent.
 */
public class UITheme {

    // ── Colour Palette (Clean Light Theme) ───────────────────────────────────
    public static final Color BG_DARK        = new Color(243, 244, 246);  // Light grey background (gray-100)
    public static final Color BG_CARD        = new Color(255, 255, 255);  // Pure white card / panel bg
    public static final Color BG_SURFACE     = new Color(255, 255, 255);  // Pure white input / table surface
    public static final Color ACCENT_PRIMARY = new Color(79,  70,  229);  // Indigo-600
    public static final Color ACCENT_HOVER   = new Color(67,  56,  202);  // Indigo-700 (hover)
    public static final Color ACCENT_SUCCESS = new Color(16,  185, 129);  // Emerald-500
    public static final Color ACCENT_DANGER  = new Color(239, 68,  68);   // Rose-500
    public static final Color ACCENT_WARN    = new Color(245, 158, 11);   // Amber-500
    public static final Color TEXT_PRIMARY   = new Color(17,  24,  39);   // Gray-900 (almost black)
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);  // Gray-500 (muted grey)
    public static final Color BORDER_COLOR   = new Color(229, 231, 235);  // Gray-200 (subtle border)

    // ── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Borders ──────────────────────────────────────────────────────────────
    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(16, 20, 16, 20)
        );
    }

    public static Border inputBorder() {
        return new LineBorder(BORDER_COLOR, 1, true);
    }

    // ── Factory: Label ───────────────────────────────────────────────────────
    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setOpaque(false);
        return lbl;
    }

    // ── Factory: Primary Button ──────────────────────────────────────────────
    public static JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? ACCENT_HOVER : ACCENT_PRIMARY;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 36, 38);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Factory: Danger Button ───────────────────────────────────────────────
    public static JButton makeDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? ACCENT_DANGER.brighter() : ACCENT_DANGER;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 36, 34);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Factory: Ghost/Secondary Button ─────────────────────────────────────
    public static JButton makeGhostButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BG_DARK : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 36, 38);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_SECONDARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Factory: Text Field ──────────────────────────────────────────────────
    public static JTextField makeTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_SURFACE);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    // ── Factory: Password Field ──────────────────────────────────────────────
    public static JPasswordField makePasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_INPUT);
        pf.setForeground(TEXT_PRIMARY);
        pf.setBackground(BG_SURFACE);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    // ── Factory: Table ───────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(BG_SURFACE);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(30);
        table.setSelectionBackground(ACCENT_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(BG_CARD);
        table.getTableHeader().setForeground(ACCENT_PRIMARY);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.getTableHeader().setBorder(new LineBorder(BORDER_COLOR));
    }

    // ── Factory: Scroll Pane ─────────────────────────────────────────────────
    public static JScrollPane makeScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_SURFACE);
        sp.setBorder(new LineBorder(BORDER_COLOR, 1));
        sp.getVerticalScrollBar().setBackground(BG_CARD);
        return sp;
    }

    // ── Factory: ComboBox ────────────────────────────────────────────────────
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(BG_SURFACE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(FONT_INPUT);
        combo.setBorder(new LineBorder(BORDER_COLOR, 1, true));
    }

    // ── Util: Dark panel ────────────────────────────────────────────────────
    public static JPanel makeDarkPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_DARK);
        return p;
    }

    public static JPanel makeCardPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_CARD);
        return p;
    }
}
