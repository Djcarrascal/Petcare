package com.petcare.service;

import com.petcare.exception.PetCareException;
import com.petcare.model.User;
import java.util.List;

// Business rules for users, login and permissions live behind this contract
public interface UserService {

    // Uses the decorated UserCreator under the hood
    int register(User user) throws PetCareException;

    void update(User user) throws PetCareException;

    // Only an ADMIN acting as requestingUser is allowed to delete another user
    void deleteUser(int userId, User requestingUser) throws PetCareException;

    // Validates credentials and that the user is active, returns the logged in user
    User login(String username, String password) throws PetCareException;

    List<User> listAll() throws PetCareException;
}