package com.hotelbooking.view.admin;

import com.hotelbooking.model.Room;
import com.hotelbooking.service.RoomService;
import com.hotelbooking.util.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageRoomsPanel extends JPanel {
    private final RoomService roomService = new RoomService();
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField roomNumberField;
    private JComboBox<Room.RoomType> typeCombo;
    private JComboBox<Room.RoomStatus> statusCombo;
    private JTextField priceField;
    private JTextField descriptionField;
    private int selectedRoomId = -1;

    public ManageRoomsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));
        initComponents();
        loadRooms();
    }

    private void initComponents() {
        // Header
        JLabel header = new JLabel("Room Management");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(30, 60, 120));
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Room #", "Type", "Price/Night", "Status", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(30, 60, 120));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedRoom());

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form panel
        add(createFormPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Room Details"));
        formPanel.setBackground(Color.WHITE);

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        roomNumberField = new JTextField(8);
        typeCombo = new JComboBox<>(Room.RoomType.values());
        priceField = new JTextField(8);
        statusCombo = new JComboBox<>(Room.RoomStatus.values());
        descriptionField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        fields.add(new JLabel("Room #:"), gbc);
        gbc.gridx = 1;
        fields.add(roomNumberField, gbc);
        gbc.gridx = 2;
        fields.add(new JLabel("Type:"), gbc);
        gbc.gridx = 3;
        fields.add(typeCombo, gbc);
        gbc.gridx = 4;
        fields.add(new JLabel("Price/Night:"), gbc);
        gbc.gridx = 5;
        fields.add(priceField, gbc);
        gbc.gridx = 6;
        fields.add(new JLabel("Status:"), gbc);
        gbc.gridx = 7;
        fields.add(statusCombo, gbc);
        gbc.gridx = 8;
        fields.add(new JLabel("Description:"), gbc);
        gbc.gridx = 9;
        fields.add(descriptionField, gbc);

        formPanel.add(fields, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttons.setOpaque(false);

        JButton addBtn = new JButton("Add Room");
        addBtn.addActionListener(e -> addRoom());
        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> updateRoom());
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteRoom());
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearForm());

        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);
        formPanel.add(buttons, BorderLayout.SOUTH);

        return formPanel;
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        for (Room room : roomService.getAllRooms()) {
            tableModel.addRow(new Object[]{
                room.getId(), room.getRoomNumber(), room.getRoomType(),
                String.format("$%.2f", room.getPricePerNight()),
                room.getStatus(), room.getDescription()
            });
        }
    }

    private void loadSelectedRoom() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            selectedRoomId = id;
            roomNumberField.setText((String) tableModel.getValueAt(row, 1));
            typeCombo.setSelectedItem(Room.RoomType.valueOf((String) tableModel.getValueAt(row, 2)));
            String priceStr = ((String) tableModel.getValueAt(row, 3)).replace("$", "");
            priceField.setText(priceStr);
            statusCombo.setSelectedItem(Room.RoomStatus.valueOf((String) tableModel.getValueAt(row, 4)));
            descriptionField.setText((String) tableModel.getValueAt(row, 5));
        }
    }

    private void addRoom() {
        try {
            Room room = buildRoomFromForm();
            roomService.createRoom(room);
            JOptionPane.showMessageDialog(this, "Room added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadRooms();
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoom() {
        if (selectedRoomId < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Room room = buildRoomFromForm();
            room.setId(selectedRoomId);
            roomService.updateRoom(room);
            JOptionPane.showMessageDialog(this, "Room updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadRooms();
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        if (selectedRoomId < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this room?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roomService.deleteRoom(selectedRoomId);
                JOptionPane.showMessageDialog(this, "Room deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadRooms();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Room buildRoomFromForm() {
        double price = Double.parseDouble(priceField.getText().trim());
        return new Room(
            roomNumberField.getText().trim(),
            (Room.RoomType) typeCombo.getSelectedItem(),
            price,
            (Room.RoomStatus) statusCombo.getSelectedItem(),
            descriptionField.getText().trim()
        );
    }

    private void clearForm() {
        roomNumberField.setText("");
        priceField.setText("");
        descriptionField.setText("");
        typeCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        selectedRoomId = -1;
        table.clearSelection();
    }
}
