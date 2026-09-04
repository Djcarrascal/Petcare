package com.petcare.controller;

import com.petcare.exception.PetCareException;
import com.petcare.model.User;
import com.petcare.service.UserService;
import com.petcare.service.impl.UserServiceImpl;
import com.petcare.util.HttpTraceLogger;

import java.util.List;

// Sits between the view and the service, no SQL and no business rules live here
public class UserController {

    private final UserService userService = new UserServiceImpl();

    public int register(User user) throws PetCareException {
        try {
            int id = userService.register(user);
            HttpTraceLogger.trace("POST", "/users", 201);
            return id;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/users", e);
            throw e;
        }
    }

    public void update(User user) throws PetCareException {
        try {
            userService.update(user);
            HttpTraceLogger.trace("PATCH", "/users/" + user.getId(), 200);

        } catch (PetCareException e) {
            HttpTraceLogger.error("/users/" + user.getId(), e);
            throw e;
        }
    }

    public void deleteUser(int userId, User requestingUser) throws PetCareException {
        try {
            userService.deleteUser(userId, requestingUser);
            HttpTraceLogger.trace("DELETE", "/users/" + userId, 200);

        } catch (PetCareException e) {
            HttpTraceLogger.error("/users/" + userId, e);
            throw e;
        }
    }

    public User login(String username, String password) throws PetCareException {
        try {
            User user = userService.login(username, password);
            // Logging the username, never the password, same rule as we followed in User.toString()
            HttpTraceLogger.trace("POST", "/auth/login (" + username + ")", 200);
            return user;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/auth/login (" + username + ")", e);
            throw e;
        }
    }

    public List<User> listAll() throws PetCareException {
        try {
            List<User> users = userService.listAll();
            HttpTraceLogger.trace("GET", "/users", 200);
            return users;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/users", e);
            throw e;
        }
    }
}