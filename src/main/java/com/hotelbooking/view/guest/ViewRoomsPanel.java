package com.hotelbooking.view.guest;

import com.hotelbooking.model.Room;
import com.hotelbooking.service.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewRoomsPanel extends JPanel {
    private final RoomService roomService = new RoomService();
    private DefaultTableModel tableModel;
    private JComboBox<Room.RoomType> filterType;

    public ViewRoomsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));
        initComponents();
        loadRooms(null);
    }

    private void initComponents() {
        // Header with filter
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false);

        JLabel header = new JLabel("Available Rooms");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(30, 60, 120));
        headerPanel.add(header);

        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(new JLabel("Filter by type:"));
        filterType = new JComboBox<>();
        filterType.addItem(null);
        filterType.addItem(Room.RoomType.SINGLE);
        filterType.addItem(Room.RoomType.DOUBLE);
        filterType.addItem(Room.RoomType.SUITE);
        filterType.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value == null) setText("All Types");
                return this;
            }
        });
        filterType.addActionListener(e -> {
            Room.RoomType type = (Room.RoomType) filterType.getSelectedItem();
            loadRooms(type);
        });
        headerPanel.add(filterType);

        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Room #", "Type", "Price/Night", "Description"};
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
    }

    private void loadRooms(Room.RoomType type) {
        tableModel.setRowCount(0);
        var rooms = type != null ? roomService.getRoomsByType(type) : roomService.getAvailableRooms();
        for (Room r : rooms) {
            if (r.getStatus() == Room.RoomStatus.AVAILABLE) {
                tableModel.addRow(new Object[]{
                    r.getRoomNumber(), r.getRoomType(),
                    String.format("$%.2f", r.getPricePerNight()), r.getDescription()
                });
            }
        }
    }
}
