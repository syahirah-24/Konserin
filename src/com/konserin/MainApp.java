package com.konserin;

import com.konserin.views.LoginView;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Set FlatLaf/Nimbus-inspired modern look using UIManager
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Custom global UI tweaks for a clean light feel
        UIManager.put("Panel.background",          new java.awt.Color(243, 244, 246));
        UIManager.put("OptionPane.background",     new java.awt.Color(255, 255, 255));
        UIManager.put("OptionPane.messageForeground", new java.awt.Color(17, 24, 39));

        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}
