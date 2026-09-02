#!/bin/bash
echo "Starting Hotel Room Booking System..."
echo ""

JAR_FILE="target/hotel-room-booking-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Application not built yet. Running build first..."
    ./build.sh
    if [ $? -ne 0 ]; then
        echo "Build failed. Cannot start application."
        exit 1
    fi
fi

java -jar "$JAR_FILE"
