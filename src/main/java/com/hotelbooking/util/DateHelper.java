package com.hotelbooking.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateHelper {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter DB_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateHelper() {}

    public static String formatDisplay(LocalDate date) {
        return date != null ? date.format(DISPLAY_FORMAT) : "";
    }

    public static String formatDb(LocalDate date) {
        return date != null ? date.format(DB_FORMAT) : "";
    }

    public static LocalDate parseDb(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        return LocalDate.parse(dateStr, DB_FORMAT);
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static boolean isFuture(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    public static boolean isCheckOutAfterCheckIn(LocalDate checkIn, LocalDate checkOut) {
        return checkIn != null && checkOut != null && checkOut.isAfter(checkIn);
    }
}
