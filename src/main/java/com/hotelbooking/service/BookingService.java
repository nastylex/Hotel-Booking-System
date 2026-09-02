package com.hotelbooking.service;

import com.hotelbooking.dao.BookingDAO;
import com.hotelbooking.dao.RoomDAO;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.Room;
import com.hotelbooking.util.DateHelper;
import com.hotelbooking.util.ValidationException;

import java.time.LocalDate;
import java.util.List;

public class BookingService {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    public Booking makeBooking(int userId, int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        // Validate inputs
        if (checkInDate == null || checkOutDate == null) {
            throw new ValidationException("Check-in and check-out dates are required.");
        }
        if (!DateHelper.isCheckOutAfterCheckIn(checkInDate, checkOutDate)) {
            throw new ValidationException("Check-out date must be after check-in date.");
        }
        if (!DateHelper.isFuture(checkInDate)) {
            throw new ValidationException("Check-in date must be in the future.");
        }

        // Check room exists and is available
        Room room = roomDAO.findById(roomId);
        if (room == null) {
            throw new ValidationException("Room not found.");
        }
        if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
            throw new ValidationException("Room is not available for booking.");
        }

        // Check no overlapping bookings
        if (!bookingDAO.isRoomAvailableForDates(roomId, checkInDate, checkOutDate, -1)) {
            throw new ValidationException("Room is already booked for the selected dates.");
        }

        // Calculate total price
        long nights = DateHelper.daysBetween(checkInDate, checkOutDate);
        double totalPrice = room.getPricePerNight() * nights;

        // Create booking
        Booking booking = new Booking(userId, roomId, checkInDate, checkOutDate, totalPrice, Booking.BookingStatus.CONFIRMED);
        return bookingDAO.create(booking);
    }

    public void cancelBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new ValidationException("Booking not found.");
        }
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new ValidationException("Only confirmed bookings can be cancelled.");
        }
        // Allow cancellation up to check-in date
        if (!booking.getCheckInDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot cancel booking on or after check-in date.");
        }
        bookingDAO.updateStatus(bookingId, Booking.BookingStatus.CANCELLED);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    public List<Booking> getUserBookings(int userId) {
        return bookingDAO.findByUserId(userId);
    }

    public List<Booking> getBookingsByDateRange(LocalDate start, LocalDate end) {
        return bookingDAO.findByDateRange(start, end);
    }
}
