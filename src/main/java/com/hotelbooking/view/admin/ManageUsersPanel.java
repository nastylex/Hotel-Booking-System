package com.hotelbooking.view.admin;

import com.hotelbooking.model.User;
import com.hotelbooking.service.UserService;
import com.hotelbooking.util.DateHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageUsersPanel extends JPanel {
    private final UserService userService = new UserService();
    private DefaultTableModel tableModel;

    public ManageUsersPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        JLabel header = new JLabel("User Management");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(30, 60, 120));
        add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "Full Name", "Role", "Registered"};
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

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadUsers());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        for (User u : userService.getAllUsers()) {
            tableModel.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getFullName(),
                u.getRole(),
                u.getCreatedAt() != null ? DateHelper.formatDisplay(u.getCreatedAt().toLocalDate()) : "N/A"
            });
        }
    }
}
