package com.hotelbooking.view.reports;

import com.hotelbooking.model.Booking;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.util.DateHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BookingReportPanel extends JPanel {
    private final BookingService bookingService = new BookingService();
    private DefaultTableModel tableModel;
    private JTextField startDateField;
    private JTextField endDateField;
    private JLabel totalLabel;

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public BookingReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));
        initComponents();
        loadAllBookings();
    }

    private void initComponents() {
        JLabel header = new JLabel("Booking Report");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(30, 60, 120));
        add(header, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Date Range Filter"));

        filterPanel.add(new JLabel("From:"));
        startDateField = new JTextField(10);
        startDateField.setText(LocalDate.now().withDayOfMonth(1).format(INPUT_FORMAT));
        filterPanel.add(startDateField);

        filterPanel.add(new JLabel("To:"));
        endDateField = new JTextField(10);
        endDateField.setText(LocalDate.now().format(INPUT_FORMAT));
        filterPanel.add(endDateField);

        JButton filterBtn = new JButton("Generate Report");
        filterBtn.addActionListener(e -> generateReport());
        filterPanel.add(filterBtn);

        JButton allBtn = new JButton("Show All");
        allBtn.addActionListener(e -> loadAllBookings());
        filterPanel.add(allBtn);

        add(filterPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Guest", "Room", "Type", "Check-in", "Check-out", "Nights", "Total", "Status"};
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

        // Summary
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setOpaque(false);
        totalLabel = new JLabel("Total Revenue: $0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        summaryPanel.add(totalLabel);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private void loadAllBookings() {
        loadTableData(bookingService.getAllBookings());
    }

    private void generateReport() {
        try {
            LocalDate start = LocalDate.parse(startDateField.getText().trim(), INPUT_FORMAT);
            LocalDate end = LocalDate.parse(endDateField.getText().trim(), INPUT_FORMAT);
            List<Booking> bookings = bookingService.getBookingsByDateRange(start, end);
            loadTableData(bookings);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter dates in MM/dd/yyyy format.",
                    "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadTableData(List<Booking> bookings) {
        tableModel.setRowCount(0);
        double totalRevenue = 0;
        for (Booking b : bookings) {
            long nights = b.getNumberOfNights();
            tableModel.addRow(new Object[]{
                b.getId(), b.getGuestName(), b.getRoomNumber(), b.getRoomType(),
                DateHelper.formatDisplay(b.getCheckInDate()),
                DateHelper.formatDisplay(b.getCheckOutDate()),
                nights,
                String.format("$%.2f", b.getTotalPrice()),
                b.getStatus()
            });
            if (b.getStatus() == Booking.BookingStatus.CONFIRMED || b.getStatus() == Booking.BookingStatus.COMPLETED) {
                totalRevenue += b.getTotalPrice();
            }
        }
        totalLabel.setText("Total Revenue: $" + String.format("%.2f", totalRevenue) + " (" + bookings.size() + " bookings)");
    }
}
