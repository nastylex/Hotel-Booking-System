package com.hotelbooking.view;

import com.hotelbooking.model.User;
import com.hotelbooking.service.SessionManager;
import com.hotelbooking.view.admin.AdminDashboardPanel;
import com.hotelbooking.view.admin.ManageBookingsPanel;
import com.hotelbooking.view.admin.ManageRoomsPanel;
import com.hotelbooking.view.admin.ManageUsersPanel;
import com.hotelbooking.view.guest.BookingHistoryPanel;
import com.hotelbooking.view.guest.MakeBookingPanel;
import com.hotelbooking.view.guest.ViewRoomsPanel;
import com.hotelbooking.view.reports.BookingReportPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final User currentUser;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    // Card names
    public static final String DASHBOARD = "DASHBOARD";
    public static final String MANAGE_ROOMS = "MANAGE_ROOMS";
    public static final String MANAGE_BOOKINGS = "MANAGE_BOOKINGS";
    public static final String MANAGE_USERS = "MANAGE_USERS";
    public static final String VIEW_ROOMS = "VIEW_ROOMS";
    public static final String MAKE_BOOKING = "MAKE_BOOKING";
    public static final String BOOKING_HISTORY = "BOOKING_HISTORY";
    public static final String REPORTS = "REPORTS";

    public MainFrame() {
        this.currentUser = SessionManager.getCurrentUser();
        setTitle("Hotel Booking System - " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        // Menu bar
        setJMenuBar(createMenuBar());

        // Content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        initializePanels();
        add(contentPanel, BorderLayout.CENTER);

        // Status bar
        add(createStatusBar(), BorderLayout.SOUTH);

        // Show appropriate initial view
        if (currentUser.isAdmin()) {
            cardLayout.show(contentPanel, DASHBOARD);
        } else {
            cardLayout.show(contentPanel, VIEW_ROOMS);
        }
    }

    private void initializePanels() {
        if (currentUser.isAdmin()) {
            contentPanel.add(new AdminDashboardPanel(), DASHBOARD);
            contentPanel.add(new ManageRoomsPanel(), MANAGE_ROOMS);
            contentPanel.add(new ManageBookingsPanel(), MANAGE_BOOKINGS);
            contentPanel.add(new ManageUsersPanel(), MANAGE_USERS);
        } else {
            contentPanel.add(new ViewRoomsPanel(), VIEW_ROOMS);
            contentPanel.add(new MakeBookingPanel(), MAKE_BOOKING);
            contentPanel.add(new BookingHistoryPanel(), BOOKING_HISTORY);
        }
        contentPanel.add(new BookingReportPanel(), REPORTS);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 60, 120));
        menuBar.setForeground(Color.WHITE);

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("Logout", e -> logout()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", e -> System.exit(0)));
        menuBar.add(fileMenu);

        if (currentUser.isAdmin()) {
            // Admin menus
            JMenu dashboardMenu = new JMenu("Dashboard");
            dashboardMenu.add(createMenuItem("View Dashboard", e -> cardLayout.show(contentPanel, DASHBOARD)));
            menuBar.add(dashboardMenu);

            JMenu roomsMenu = new JMenu("Rooms");
            roomsMenu.add(createMenuItem("Manage Rooms", e -> cardLayout.show(contentPanel, MANAGE_ROOMS)));
            menuBar.add(roomsMenu);

            JMenu bookingsMenu = new JMenu("Bookings");
            bookingsMenu.add(createMenuItem("All Bookings", e -> cardLayout.show(contentPanel, MANAGE_BOOKINGS)));
            bookingsMenu.addSeparator();
            bookingsMenu.add(createMenuItem("Booking Reports", e -> cardLayout.show(contentPanel, REPORTS)));
            menuBar.add(bookingsMenu);

            JMenu usersMenu = new JMenu("Users");
            usersMenu.add(createMenuItem("Manage Users", e -> cardLayout.show(contentPanel, MANAGE_USERS)));
            menuBar.add(usersMenu);
        } else {
            // Guest menus
            JMenu roomsMenu = new JMenu("Rooms");
            roomsMenu.add(createMenuItem("View Available Rooms", e -> cardLayout.show(contentPanel, VIEW_ROOMS)));
            roomsMenu.add(createMenuItem("Make a Booking", e -> cardLayout.show(contentPanel, MAKE_BOOKING)));
            menuBar.add(roomsMenu);

            JMenu myBookingsMenu = new JMenu("My Bookings");
            myBookingsMenu.add(createMenuItem("Booking History", e -> cardLayout.show(contentPanel, BOOKING_HISTORY)));
            menuBar.add(myBookingsMenu);
        }

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("About", e -> showAbout()));
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JMenuItem createMenuItem(String text, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        return item;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.add(new JLabel("Logged in as: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")"));
        return statusBar;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Hotel Room Booking System\nVersion 1.0.0\n\nA fully local Java Swing desktop application\nfor managing hotel room bookings.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
