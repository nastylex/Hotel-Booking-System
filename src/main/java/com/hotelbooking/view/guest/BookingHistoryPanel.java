package com.hotelbooking.view.guest;

import com.hotelbooking.model.Booking;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.SessionManager;
import com.hotelbooking.util.DateHelper;
import com.hotelbooking.util.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BookingHistoryPanel extends JPanel {
    private final BookingService bookingService = new BookingService();
    private DefaultTableModel tableModel;

    public BookingHistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));
        initComponents();
        loadBookings();
    }

    private void initComponents() {
        JLabel header = new JLabel("My Booking History");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(30, 60, 120));
        add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Room", "Type", "Check-in", "Check-out", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(30, 60, 120));
        table.getTableHeader().setForeground(Color.WHITE);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel Selected Booking");
        cancelBtn.addActionListener(e -> cancelBooking(table));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBookings());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        for (Booking b : bookingService.getUserBookings(SessionManager.getCurrentUserId())) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getRoomNumber(), b.getRoomType(),
                DateHelper.formatDisplay(b.getCheckInDate()),
                DateHelper.formatDisplay(b.getCheckOutDate()),
                String.format("$%.2f", b.getTotalPrice()),
                b.getStatus()
            });
        }
    }

    private void cancelBooking(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookingId = (int) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 6);
        if (!"CONFIRMED".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only confirmed bookings can be cancelled.",
                    "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking #" + bookingId + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookingService.cancelBooking(bookingId);
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully.",
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
