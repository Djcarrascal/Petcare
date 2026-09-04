package com.petcare.service;

import com.petcare.exception.PetCareException;
import com.petcare.model.User;

// Small contract just for the "create user" operation, this is what gets decorated
public interface UserCreator {
    int create(User user) throws PetCareException;
}