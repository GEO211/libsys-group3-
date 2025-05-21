package com.library.main;

import javax.swing.*;
import com.library.util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Test database connection
            DatabaseConnection.getConnection();
            
            // Launch application
            SwingUtilities.invokeLater(() -> {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Failed to start: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 