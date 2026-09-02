package com.hotelbooking.database;

import com.hotelbooking.util.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

    public static void initialize() {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {
            // Create users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    full_name TEXT NOT NULL,
                    role TEXT NOT NULL CHECK (role IN ('ADMIN', 'GUEST')),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create rooms table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    room_number TEXT UNIQUE NOT NULL,
                    room_type TEXT NOT NULL CHECK (room_type IN ('SINGLE', 'DOUBLE', 'SUITE')),
                    price_per_night REAL NOT NULL,
                    status TEXT NOT NULL CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE')) DEFAULT 'AVAILABLE',
                    description TEXT
                )
            """);

            // Create bookings table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bookings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    room_id INTEGER NOT NULL,
                    check_in_date DATE NOT NULL,
                    check_out_date DATE NOT NULL,
                    total_price REAL NOT NULL,
                    status TEXT NOT NULL CHECK (status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED')) DEFAULT 'CONFIRMED',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (room_id) REFERENCES rooms(id)
                )
            """);

            // Insert default admin if not exists
            insertDefaultAdmin(conn);
            // Insert sample rooms if none exist
            insertSampleRooms(conn);

            System.out.println("Database schema initialized successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema: " + e.getMessage(), e);
        }
    }

    private static void insertDefaultAdmin(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             var rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, PasswordHasher.hash("admin123"));
                    insertStmt.setString(3, "System Administrator");
                    insertStmt.setString(4, "ADMIN");
                    insertStmt.executeUpdate();
                }
                System.out.println("Default admin user created (admin/admin123).");
            }
        }
    }

    private static void insertSampleRooms(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM rooms";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             var rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO rooms (room_number, room_type, price_per_night, status, description) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    Object[][] rooms = {
                        {"101", "SINGLE", 89.99, "AVAILABLE", "Cozy single room with city view"},
                        {"102", "SINGLE", 89.99, "AVAILABLE", "Single room with garden view"},
                        {"103", "DOUBLE", 129.99, "AVAILABLE", "Spacious double room with balcony"},
                        {"104", "DOUBLE", 129.99, "AVAILABLE", "Double room with ocean view"},
                        {"105", "SUITE", 249.99, "AVAILABLE", "Luxury suite with living area"},
                        {"106", "SUITE", 299.99, "AVAILABLE", "Premium suite with jacuzzi"},
                        {"201", "SINGLE", 94.99, "AVAILABLE", "Single room on second floor"},
                        {"202", "DOUBLE", 139.99, "AVAILABLE", "Deluxe double room"},
                        {"203", "DOUBLE", 139.99, "MAINTENANCE", "Under renovation"},
                        {"204", "SUITE", 279.99, "AVAILABLE", "Executive suite with workspace"}
                    };
                    for (Object[] room : rooms) {
                        insertStmt.setString(1, (String) room[0]);
                        insertStmt.setString(2, (String) room[1]);
                        insertStmt.setDouble(3, (double) room[2]);
                        insertStmt.setString(4, (String) room[3]);
                        insertStmt.setString(5, (String) room[4]);
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
                System.out.println("Sample rooms inserted.");
            }
        }
    }
}
