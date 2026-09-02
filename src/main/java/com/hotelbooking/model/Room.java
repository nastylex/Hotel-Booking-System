package com.hotelbooking.model;

public class Room {
    public enum RoomType {
        SINGLE, DOUBLE, SUITE
    }

    public enum RoomStatus {
        AVAILABLE, OCCUPIED, MAINTENANCE
    }

    private int id;
    private String roomNumber;
    private RoomType roomType;
    private double pricePerNight;
    private RoomStatus status;
    private String description;

    public Room() {}

    public Room(String roomNumber, RoomType roomType, double pricePerNight, RoomStatus status, String description) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.description = description;
    }

    public Room(int id, String roomNumber, RoomType roomType, double pricePerNight, RoomStatus status, String description) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + ") - $" + pricePerNight + "/night";
    }
}
