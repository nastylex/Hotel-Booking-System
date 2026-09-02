package com.hotelbooking;

import com.hotelbooking.database.SchemaInitializer;
import com.hotelbooking.view.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default if system L&F unavailable
        }

        // Initialize database schema
        try {
            SchemaInitializer.initialize();
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Failed to initialize database: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Launch login screen
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
