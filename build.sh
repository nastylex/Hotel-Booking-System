#!/bin/bash
echo "==========================================="
echo "  Hotel Room Booking System - Build Script"
echo "==========================================="
echo ""

# Check for Java
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed."
    echo "Please install Java 17 or later."
    exit 1
fi

# Check for Maven
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed."
    echo "Please install Apache Maven."
    exit 1
fi

echo "Java version:"
java -version 2>&1
echo ""
echo "Maven version:"
mvn --version 2>&1 | head -1
echo ""

echo "Building project..."
mvn clean package -q

if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful!"
    echo "JAR file: target/hotel-room-booking-1.0.0.jar"
    echo ""
    echo "To run the application:"
    echo "  java -jar target/hotel-room-booking-1.0.0.jar"
    echo ""
    echo "Or use: ./run.sh"
else
    echo ""
    echo "Build failed. Check the errors above."
    exit 1
fi
