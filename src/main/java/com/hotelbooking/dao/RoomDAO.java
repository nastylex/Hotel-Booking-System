package com.hotelbooking.dao;

import com.hotelbooking.database.DatabaseConnection;
import com.hotelbooking.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find room: " + e.getMessage(), e);
        }
        return null;
    }

    public Room findByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find room: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list rooms: " + e.getMessage(), e);
        }
        return rooms;
    }

    public List<Room> findAvailable() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = 'AVAILABLE' ORDER BY room_number";
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available rooms: " + e.getMessage(), e);
        }
        return rooms;
    }

    public List<Room> findByType(Room.RoomType type) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_type = ? ORDER BY room_number";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find rooms by type: " + e.getMessage(), e);
        }
        return rooms;
    }

    public List<Room> findAvailableByType(Room.RoomType type) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_type = ? AND status = 'AVAILABLE' ORDER BY room_number";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available rooms by type: " + e.getMessage(), e);
        }
        return rooms;
    }

    public Room create(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, status, description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType().name());
            stmt.setDouble(3, room.getPricePerNight());
            stmt.setString(4, room.getStatus().name());
            stmt.setString(5, room.getDescription());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) room.setId(keys.getInt(1));
            return room;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create room: " + e.getMessage(), e);
        }
    }

    public void update(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, price_per_night = ?, status = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType().name());
            stmt.setDouble(3, room.getPricePerNight());
            stmt.setString(4, room.getStatus().name());
            stmt.setString(5, room.getDescription());
            stmt.setInt(6, room.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update room: " + e.getMessage(), e);
        }
    }

    public void delete(int roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete room: " + e.getMessage(), e);
        }
    }

    public int countByStatus(Room.RoomStatus status) {
        String sql = "SELECT COUNT(*) FROM rooms WHERE status = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count rooms: " + e.getMessage(), e);
        }
        return 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM rooms";
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count rooms: " + e.getMessage(), e);
        }
        return 0;
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
            rs.getInt("id"),
            rs.getString("room_number"),
            Room.RoomType.valueOf(rs.getString("room_type")),
            rs.getDouble("price_per_night"),
            Room.RoomStatus.valueOf(rs.getString("status")),
            rs.getString("description")
        );
    }
}
