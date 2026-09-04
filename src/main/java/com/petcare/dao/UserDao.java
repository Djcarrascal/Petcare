package com.petcare.dao;

import com.petcare.exception.PetCareException;
import com.petcare.model.User;
import java.util.List;

// Contract for user persistence, implementation details live in dao.impl
public interface UserDao {

    // Saves a new user and returns the generated id
    int save(User user) throws PetCareException;

    // Updates an existing user by its id
    void update(User user) throws PetCareException;

    // Deletes a user by its id, only ADMIN should be allowed to call this from the service layer
    void delete(int id) throws PetCareException;

    // Looks up a user by its username, used both for login and uniqueness checks
    User findByUsername(String username) throws PetCareException;

    // Looks up a user by its primary key
    User findById(int id) throws PetCareException;

    // Returns every user in the system
    List<User> findAll() throws PetCareException;
}