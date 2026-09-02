package com.hotelbooking.service;

import com.hotelbooking.model.User;

public class SessionManager {
    private static User currentUser;

    private SessionManager() {}

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}
