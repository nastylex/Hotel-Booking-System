package com.hotelbooking.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class DateHelperTest {

    @Test
    void testFormatDisplay() {
        LocalDate date = LocalDate.of(2024, 3, 15);
        String formatted = DateHelper.formatDisplay(date);
        assertTrue(formatted.contains("Mar") || formatted.contains("03"), "Should format month");
        assertTrue(formatted.contains("15"), "Should contain day");
        assertTrue(formatted.contains("2024"), "Should contain year");
    }

    @Test
    void testFormatDisplayNull() {
        assertEquals("", DateHelper.formatDisplay(null), "Null date should return empty string");
    }

    @Test
    void testFormatDb() {
        LocalDate date = LocalDate.of(2024, 3, 15);
        assertEquals("2024-03-15", DateHelper.formatDb(date));
    }

    @Test
    void testParseDb() {
        LocalDate date = DateHelper.parseDb("2024-03-15");
        assertEquals(LocalDate.of(2024, 3, 15), date);
    }

    @Test
    void testParseDbNull() {
        assertNull(DateHelper.parseDb(null));
    }

    @Test
    void testParseDbBlank() {
        assertNull(DateHelper.parseDb("  "));
    }

    @Test
    void testDaysBetween() {
        LocalDate start = LocalDate.of(2024, 3, 10);
        LocalDate end = LocalDate.of(2024, 3, 15);
        assertEquals(5, DateHelper.daysBetween(start, end));
    }

    @Test
    void testIsFuture() {
        assertTrue(DateHelper.isFuture(LocalDate.now().plusDays(1)));
        assertFalse(DateHelper.isFuture(LocalDate.now().minusDays(1)));
        assertFalse(DateHelper.isFuture(null));
    }

    @Test
    void testIsCheckOutAfterCheckIn() {
        LocalDate checkIn = LocalDate.of(2024, 3, 10);
        LocalDate checkOut = LocalDate.of(2024, 3, 15);
        assertTrue(DateHelper.isCheckOutAfterCheckIn(checkIn, checkOut));
        assertFalse(DateHelper.isCheckOutAfterCheckIn(checkOut, checkIn));
    }
}
