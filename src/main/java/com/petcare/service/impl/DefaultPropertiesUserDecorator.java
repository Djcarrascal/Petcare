package com.petcare.service.impl;

import com.petcare.enums.StateUser;
import com.petcare.enums.RoleUser;
import com.petcare.exception.PetCareException;
import com.petcare.model.User;
import com.petcare.service.UserCreator;

import java.time.LocalDateTime;

// Wraps another UserCreator and adds default properties before delegating,
// the wrapped creator's own logic is never touched
public class DefaultPropertiesUserDecorator implements UserCreator {

    private final UserCreator wrapped;

    public DefaultPropertiesUserDecorator(UserCreator wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int create(User user) throws PetCareException {
        // Applying the defaults the requirement asks for, only if they were not set already
        if (user.getRole() == null) {
            user.setRole(RoleUser.RECEPCIONISTA);
        }
        if (user.getStatus() == null) {
            user.setStatus(StateUser.ACTIVE);
        }
        user.setCreatedAt(LocalDateTime.now());

        // Delegating to whatever creator this decorator is wrapping
        return wrapped.create(user);
    }
}