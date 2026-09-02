package com.hotelbooking.view.admin;

import com.hotelbooking.dao.BookingDAO;
import com.hotelbooking.dao.RoomDAO;
import com.hotelbooking.dao.UserDAO;
import com.hotelbooking.model.Room;
import com.hotelbooking.service.SessionManager;
import com.hotelbooking.util.DateHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    private final RoomDAO roomDAO = new RoomDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final UserDAO userDAO = new UserDAO();

    public AdminDashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));
        initComponents();
    }

    private void initComponents() {
        // Header
        JLabel header = new JLabel("Admin Dashboard");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(30, 60, 120));
        add(header, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setOpaque(false);

        // Stats cards
        mainContent.add(createStatsPanel(), BorderLayout.NORTH);

        // Recent bookings table
        mainContent.add(createRecentBookingsPanel(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        statsPanel.setOpaque(false);

        int totalRooms = roomDAO.countAll();
        int available = roomDAO.countByStatus(Room.RoomStatus.AVAILABLE);
        int occupied = roomDAO.countByStatus(Room.RoomStatus.OCCUPIED);
        int activeBookings = bookingDAO.countActiveBookings();
        int totalUsers = userDAO.countAll();

        statsPanel.add(createStatCard("Total Rooms", String.valueOf(totalRooms), new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Available", String.valueOf(available), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Occupied", String.valueOf(occupied), new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Active Bookings", String.valueOf(activeBookings), new Color(243, 156, 18)));
        statsPanel.add(createStatCard("Total Users", String.valueOf(totalUsers), new Color(155, 89, 182)));

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRecentBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Recent Bookings"));
        panel.setBackground(Color.WHITE);

        String[] columns = {"ID", "Guest", "Room", "Type", "Check-in", "Check-out", "Total", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        var bookings = bookingDAO.findAll();
        int show = Math.min(bookings.size(), 10);
        for (int i = 0; i < show; i++) {
            var b = bookings.get(i);
            model.addRow(new Object[]{
                b.getId(), b.getGuestName(), b.getRoomNumber(), b.getRoomType(),
                DateHelper.formatDisplay(b.getCheckInDate()),
                DateHelper.formatDisplay(b.getCheckOutDate()),
                String.format("$%.2f", b.getTotalPrice()),
                b.getStatus()
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(30, 60, 120));
        table.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
