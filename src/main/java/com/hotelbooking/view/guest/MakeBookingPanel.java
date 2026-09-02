package com.hotelbooking.view.guest;

import com.hotelbooking.model.Booking;
import com.hotelbooking.model.Room;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.RoomService;
import com.hotelbooking.service.SessionManager;
import com.hotelbooking.util.DateHelper;
import com.hotelbooking.util.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MakeBookingPanel extends JPanel {
    private final RoomService roomService = new RoomService();
    private final BookingService bookingService = new BookingService();

    private JComboBox<String> roomCombo;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JLabel priceLabel;
    private JLabel nightsLabel;
    private Room selectedRoom;

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public MakeBookingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));
        initComponents();
    }

    private void initComponents() {
        JLabel header = new JLabel("Make a Booking");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(30, 60, 120));
        add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Room selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Room:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        roomCombo = new JComboBox<>();
        loadAvailableRooms();
        roomCombo.addActionListener(e -> updateSelectedRoom());
        formPanel.add(roomCombo, gbc);

        // Check-in
        gbc.gridwidth = 1; gbc.weightx = 0; gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(new JLabel("Check-in (MM/dd/yyyy):"), gbc);
        gbc.gridx = 1;
        checkInField = new JTextField(12);
        checkInField.setText(LocalDate.now().plusDays(1).format(INPUT_FORMAT));
        formPanel.add(checkInField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("(e.g., " + LocalDate.now().plusDays(1).format(INPUT_FORMAT) + ")"), gbc);

        // Check-out
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Check-out (MM/dd/yyyy):"), gbc);
        gbc.gridx = 1;
        checkOutField = new JTextField(12);
        checkOutField.setText(LocalDate.now().plusDays(2).format(INPUT_FORMAT));
        formPanel.add(checkOutField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("(e.g., " + LocalDate.now().plusDays(2).format(INPUT_FORMAT) + ")"), gbc);

        // Calculation display
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(new JLabel("Duration:"), gbc);
        gbc.gridx = 1;
        nightsLabel = new JLabel("1 night");
        nightsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(nightsLabel, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(new JLabel("Total Price:"), gbc);
        gbc.gridx = 1;
        priceLabel = new JLabel("$0.00");
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        priceLabel.setForeground(new Color(30, 120, 60));
        formPanel.add(priceLabel, gbc);

        // Book button
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 3;
        JButton bookBtn = new JButton("Book Now");
        bookBtn.setPreferredSize(new Dimension(200, 40));
        bookBtn.setBackground(new Color(30, 120, 60));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.addActionListener(e -> makeBooking());
        formPanel.add(bookBtn, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Instructions
        JLabel instructions = new JLabel("<html><i>Select a room, enter your dates (MM/dd/yyyy format), and click Book Now.</i></html>");
        instructions.setFont(new Font("SansSerif", Font.ITALIC, 11));
        instructions.setForeground(Color.GRAY);
        add(instructions, BorderLayout.SOUTH);
    }

    private void loadAvailableRooms() {
        roomCombo.removeAllItems();
        roomCombo.addItem("-- Select a room --");
        for (Room r : roomService.getAvailableRooms()) {
            roomCombo.addItem(r.getRoomNumber() + " - " + r.getRoomType() + " - $" + r.getPricePerNight() + "/night");
        }
    }

    private void updateSelectedRoom() {
        int idx = roomCombo.getSelectedIndex();
        if (idx > 0) {
            var rooms = roomService.getAvailableRooms();
            if (idx - 1 < rooms.size()) {
                selectedRoom = rooms.get(idx - 1);
                updatePriceCalculation();
            }
        } else {
            selectedRoom = null;
            priceLabel.setText("$0.00");
            nightsLabel.setText("0 nights");
        }
    }

    private void updatePriceCalculation() {
        if (selectedRoom == null) return;
        try {
            LocalDate checkIn = LocalDate.parse(checkInField.getText().trim(), INPUT_FORMAT);
            LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim(), INPUT_FORMAT);
            if (checkOut.isAfter(checkIn)) {
                long nights = DateHelper.daysBetween(checkIn, checkOut);
                double total = nights * selectedRoom.getPricePerNight();
                nightsLabel.setText(nights + " night" + (nights > 1 ? "s" : ""));
                priceLabel.setText(String.format("$%.2f", total));
            }
        } catch (DateTimeParseException e) {
            // Ignore parse errors while typing
        }
    }

    private void makeBooking() {
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select a room.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate checkIn = LocalDate.parse(checkInField.getText().trim(), INPUT_FORMAT);
            LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim(), INPUT_FORMAT);

            bookingService.makeBooking(SessionManager.getCurrentUserId(), selectedRoom.getId(), checkIn, checkOut);

            JOptionPane.showMessageDialog(this,
                "Booking confirmed!\n\nRoom: " + selectedRoom.getRoomNumber() + "\nCheck-in: " + DateHelper.formatDisplay(checkIn) +
                "\nCheck-out: " + DateHelper.formatDisplay(checkOut) + "\n\nEnjoy your stay!",
                "Booking Successful", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAvailableRooms();
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Booking Error", JOptionPane.WARNING_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter dates in MM/dd/yyyy format.",
                    "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        roomCombo.setSelectedIndex(0);
        selectedRoom = null;
        checkInField.setText(LocalDate.now().plusDays(1).format(INPUT_FORMAT));
        checkOutField.setText(LocalDate.now().plusDays(2).format(INPUT_FORMAT));
        priceLabel.setText("$0.00");
        nightsLabel.setText("1 night");
    }
}
