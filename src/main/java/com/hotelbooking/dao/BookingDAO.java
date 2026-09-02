package com.hotelbooking.dao;

import com.hotelbooking.database.DatabaseConnection;
import com.hotelbooking.model.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public Booking findById(int id) {
        String sql = """
            SELECT b.*, u.full_name AS guest_name, r.room_number, r.room_type
            FROM bookings b
            JOIN users u ON b.user_id = u.id
            JOIN rooms r ON b.room_id = r.id
            WHERE b.id = ?
        """;
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find booking: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, u.full_name AS guest_name, r.room_number, r.room_type
            FROM bookings b
            JOIN users u ON b.user_id = u.id
            JOIN rooms r ON b.room_id = r.id
            ORDER BY b.created_at DESC
        """;
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) bookings.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list bookings: " + e.getMessage(), e);
        }
        return bookings;
    }

    public List<Booking> findByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, u.full_name AS guest_name, r.room_number, r.room_type
            FROM bookings b
            JOIN users u ON b.user_id = u.id
            JOIN rooms r ON b.room_id = r.id
            WHERE b.user_id = ?
            ORDER BY b.created_at DESC
        """;
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) bookings.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list user bookings: " + e.getMessage(), e);
        }
        return bookings;
    }

    public List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, u.full_name AS guest_name, r.room_number, r.room_type
            FROM bookings b
            JOIN users u ON b.user_id = u.id
            JOIN rooms r ON b.room_id = r.id
            WHERE b.check_in_date >= ? AND b.check_out_date <= ?
            ORDER BY b.check_in_date
        """;
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, startDate.toString());
            stmt.setString(2, endDate.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) bookings.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find bookings by date range: " + e.getMessage(), e);
        }
        return bookings;
    }

    public boolean isRoomAvailableForDates(int roomId, LocalDate checkIn, LocalDate checkOut, int excludeBookingId) {
        String sql = """
            SELECT COUNT(*) FROM bookings
            WHERE room_id = ? AND status = 'CONFIRMED'
            AND check_in_date < ? AND check_out_date > ?
            AND id != ?
        """;
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setString(2, checkOut.toString());
            stmt.setString(3, checkIn.toString());
            stmt.setInt(4, excludeBookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check availability: " + e.getMessage(), e);
        }
        return false;
    }

    public Booking create(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setString(3, booking.getCheckInDate().toString());
            stmt.setString(4, booking.getCheckOutDate().toString());
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setString(6, booking.getStatus().name());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) booking.setId(keys.getInt(1));
            return booking;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create booking: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int bookingId, Booking.BookingStatus status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update booking status: " + e.getMessage(), e);
        }
    }

    public int countTodayCheckIns() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE check_in_date = ? AND status = 'CONFIRMED'";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, LocalDate.now().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count check-ins: " + e.getMessage(), e);
        }
        return 0;
    }

    public int countTodayCheckOuts() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE check_out_date = ? AND status = 'CONFIRMED'";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, LocalDate.now().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count check-outs: " + e.getMessage(), e);
        }
        return 0;
    }

    public int countActiveBookings() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status = 'CONFIRMED'";
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count active bookings: " + e.getMessage(), e);
        }
        return 0;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking booking = new Booking(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("room_id"),
            LocalDate.parse(rs.getString("check_in_date")),
            LocalDate.parse(rs.getString("check_out_date")),
            rs.getDouble("total_price"),
            Booking.BookingStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        );
        booking.setGuestName(rs.getString("guest_name"));
        booking.setRoomNumber(rs.getString("room_number"));
        booking.setRoomType(rs.getString("room_type"));
        return booking;
    }
}
