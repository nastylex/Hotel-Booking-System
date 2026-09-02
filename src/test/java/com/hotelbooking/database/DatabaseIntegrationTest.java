package com.hotelbooking.database;

import com.hotelbooking.dao.UserDAO;
import com.hotelbooking.dao.RoomDAO;
import com.hotelbooking.model.User;
import com.hotelbooking.model.Room;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class DatabaseIntegrationTest {

    private static boolean initialized = false;

    @BeforeAll
    static void setUpDatabase() {
        // Use an in-memory database for testing
        System.setProperty("hotel.db.url", "jdbc:sqlite::memory:");
        if (!initialized) {
            SchemaInitializer.initialize();
            initialized = true;
        }
    }

    @Test
    void testDefaultAdminCreated() {
        UserDAO userDAO = new UserDAO();
        User admin = userDAO.findByUsername("admin");
        assertNotNull(admin, "Default admin user should exist");
        assertEquals("System Administrator", admin.getFullName());
        assertEquals(User.Role.ADMIN, admin.getRole());
    }

    @Test
    void testSampleRoomsCreated() {
        RoomDAO roomDAO = new RoomDAO();
        List<Room> rooms = roomDAO.findAll();
        assertFalse(rooms.isEmpty(), "Sample rooms should be created");
        assertTrue(rooms.size() >= 10, "Should have at least 10 sample rooms");
    }

    @Test
    void testCreateAndFindUser() {
        UserDAO userDAO = new UserDAO();
        User testUser = new User("testuser_" + System.currentTimeMillis(),
            "hash123", "Test User", User.Role.GUEST);
        User created = userDAO.create(testUser);
        assertNotNull(created.getId(), "Created user should have an ID");

        User found = userDAO.findById(created.getId());
        assertNotNull(found, "Should find user by ID");
        assertEquals("Test User", found.getFullName());
    }

    @Test
    void testCreateAndFindRoom() {
        RoomDAO roomDAO = new RoomDAO();
        String uniqueNumber = "T" + System.currentTimeMillis();
        Room testRoom = new Room(uniqueNumber, Room.RoomType.SINGLE, 99.99,
            Room.RoomStatus.AVAILABLE, "Test room");
        Room created = roomDAO.create(testRoom);
        assertNotNull(created.getId(), "Created room should have an ID");

        Room found = roomDAO.findById(created.getId());
        assertNotNull(found, "Should find room by ID");
        assertEquals(Room.RoomType.SINGLE, found.getRoomType());
    }

    @Test
    void testRoomCounts() {
        RoomDAO roomDAO = new RoomDAO();
        int total = roomDAO.countAll();
        int available = roomDAO.countByStatus(Room.RoomStatus.AVAILABLE);
        int maintenance = roomDAO.countByStatus(Room.RoomStatus.MAINTENANCE);
        assertEquals(total, available + maintenance, "Counts should be consistent");
    }
}
