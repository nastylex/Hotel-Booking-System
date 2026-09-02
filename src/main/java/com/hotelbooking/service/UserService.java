package com.hotelbooking.service;

import com.hotelbooking.dao.UserDAO;
import com.hotelbooking.model.User;
import com.hotelbooking.util.PasswordHasher;
import com.hotelbooking.util.ValidationException;

import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ValidationException("Username and password are required.");
        }
        String hash = PasswordHasher.hash(password);
        User user = userDAO.authenticate(username.trim(), hash);
        if (user == null) {
            throw new ValidationException("Invalid username or password.");
        }
        return user;
    }

    public User register(String username, String password, String fullName, User.Role role) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Username is required.");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new ValidationException("Full name is required.");
        }
        if (role == null) {
            role = User.Role.GUEST;
        }
        // Check if username already exists
        if (userDAO.findByUsername(username.trim()) != null) {
            throw new ValidationException("Username '" + username + "' is already taken.");
        }

        User user = new User(username.trim(), PasswordHasher.hash(password), fullName.trim(), role);
        return userDAO.create(user);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User getUserById(int id) {
        return userDAO.findById(id);
    }
}
