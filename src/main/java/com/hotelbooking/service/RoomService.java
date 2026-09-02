package com.hotelbooking.service;

import com.hotelbooking.dao.RoomDAO;
import com.hotelbooking.model.Room;
import com.hotelbooking.util.ValidationException;

import java.util.List;

public class RoomService {
    private final RoomDAO roomDAO = new RoomDAO();

    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomDAO.findAvailable();
    }

    public List<Room> getRoomsByType(Room.RoomType type) {
        return roomDAO.findByType(type);
    }

    public Room getRoomById(int id) {
        return roomDAO.findById(id);
    }

    public Room createRoom(Room room) {
        validateRoom(room);
        // Check for duplicate room number
        if (roomDAO.findByRoomNumber(room.getRoomNumber()) != null) {
            throw new ValidationException("Room number '" + room.getRoomNumber() + "' already exists.");
        }
        return roomDAO.create(room);
    }

    public void updateRoom(Room room) {
        validateRoom(room);
        // Check duplicate room number (excluding current room)
        Room existing = roomDAO.findByRoomNumber(room.getRoomNumber());
        if (existing != null && existing.getId() != room.getId()) {
            throw new ValidationException("Room number '" + room.getRoomNumber() + "' already exists.");
        }
        roomDAO.update(room);
    }

    public void deleteRoom(int roomId) {
        roomDAO.delete(roomId);
    }

    public void updateStatus(int roomId, Room.RoomStatus status) {
        Room room = roomDAO.findById(roomId);
        if (room == null) {
            throw new ValidationException("Room not found.");
        }
        room.setStatus(status);
        roomDAO.update(room);
    }

    private void validateRoom(Room room) {
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank()) {
            throw new ValidationException("Room number is required.");
        }
        if (room.getRoomType() == null) {
            throw new ValidationException("Room type is required.");
        }
        if (room.getPricePerNight() <= 0) {
            throw new ValidationException("Price per night must be greater than 0.");
        }
    }
}
