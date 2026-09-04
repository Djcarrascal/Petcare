package com.petcare.service.impl;

import com.petcare.dao.UserDao;
import com.petcare.dao.impl.UserDaoImpl;
import com.petcare.exception.PetCareException;
import com.petcare.model.User;
import com.petcare.service.UserCreator;

// This is the "undecorated" logic: just validate uniqueness and save, nothing else
public class BasicUserCreator implements UserCreator {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public int create(User user) throws PetCareException {
        // Business rule: username must be unique, same idea as the other uniqueness checks
        User existing = userDao.findByUsername(user.getUsername());
        if (existing != null) {
            throw new PetCareException("Username already exists: " + user.getUsername());
        }

        return userDao.save(user);
    }
}