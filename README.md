# Hotel Room Booking System

A fully local Java Swing desktop application for managing hotel room bookings with user authentication, room management, and reporting.

## Features

### Admin Features
- **Dashboard** — View statistics (total rooms, available, occupied, active bookings, users)
- **Room Management** — Add, edit, delete, and view all rooms
- **Booking Management** — View and cancel any booking
- **User Management** — View all registered users

### Guest Features
- **Browse Rooms** — View available rooms with type filtering
- **Make Bookings** — Select room, choose dates, see price calculation
- **Booking History** — View personal bookings and cancel if needed

### Reports
- **Booking Reports** — Filter by date range, view revenue summary

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17 |
| UI Framework | Java Swing |
| Database | SQLite (embedded via sqlite-jdbc) |
| Build Tool | Apache Maven |
| Test Framework | JUnit 5 |

## Prerequisites

- **Java 17** or later
- **Apache Maven** 3.6+

## Setup & Installation

### 1. Clone or download the project

### 2. Build the project
```bash
./build.sh
```
Or manually:
```bash
mvn clean package
```

### 3. Run the application
```bash
./run.sh
```
Or manually:
```bash
java -jar target/hotel-room-booking-1.0.0.jar
```

## Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |

## Project Structure

```
hotel-room-booking/
├── pom.xml                          # Maven configuration
├── build.sh                         # Build script
├── run.sh                           # Run script
├── src/main/java/com/hotelbooking/
│   ├── Main.java                    # Application entry point
│   ├── model/
│   │   ├── User.java                # User data model
│   │   ├── Room.java                # Room data model
│   │   └── Booking.java             # Booking data model
│   ├── database/
│   │   ├── DatabaseConnection.java  # SQLite connection manager
│   │   └── SchemaInitializer.java   # Database schema creation
│   ├── dao/
│   │   ├── UserDAO.java             # User CRUD operations
│   │   ├── RoomDAO.java             # Room CRUD operations
│   │   └── BookingDAO.java          # Booking CRUD operations
│   ├── service/
│   │   ├── SessionManager.java      # User session management
│   │   ├── UserService.java         # User business logic
│   │   ├── RoomService.java         # Room business logic
│   │   └── BookingService.java      # Booking business logic
│   ├── util/
│   │   ├── PasswordHasher.java      # SHA-256 password hashing
│   │   ├── DateHelper.java          # Date formatting utilities
│   │   └── ValidationException.java # Custom validation exception
│   └── view/
│       ├── LoginFrame.java          # Login screen
│       ├── MainFrame.java           # Main application frame
│       ├── admin/
│       │   ├── AdminDashboardPanel.java    # Admin dashboard
│       │   ├── ManageRoomsPanel.java       # Room management
│       │   ├── ManageBookingsPanel.java    # Booking management
│       │   └── ManageUsersPanel.java       # User management
│       ├── guest/
│       │   ├── ViewRoomsPanel.java         # Browse rooms
│       │   ├── MakeBookingPanel.java       # Make a booking
│       │   └── BookingHistoryPanel.java    # Booking history
│       └── reports/
│           └── BookingReportPanel.java     # Booking reports
```

## Database

The application uses an embedded SQLite database (`hotel_booking.db`) that is automatically created on first run with:
- Default admin user (admin/admin123)
- 10 sample rooms across Single, Double, and Suite types

## Project Documentation (for Report)

This project satisfies the following requirements:

1. **OOP Principles** — Encapsulation, classes/objects, constructors, methods, enums, access modifiers
2. **Database Integration** — SQLite with JDBC, full CRUD operations
3. **Data Validation** — Required fields, date validation, duplicate checks, overlapping booking prevention
4. **Exception Handling** — Custom exceptions, database error handling, graceful error messages
5. **UI Components** — Forms, buttons, tables (JTable), combo boxes, text fields, password fields, dialogs, menus
6. **Role-Based Access** — Admin and Guest roles with different views
7. **Search & Reports** — Date-range filtered booking reports with revenue totals
8. **Security** — Password hashing (SHA-256), session management, role-based access control
