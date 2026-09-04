package com.petcare.service.impl;

import com.petcare.dao.UserDao;
import com.petcare.dao.impl.UserDaoImpl;
import com.petcare.enums.StateUser;
import com.petcare.enums.RoleUser;
import com.petcare.exception.InvalidCredentialsException;
import com.petcare.exception.PetCareException;
import com.petcare.model.User;
import com.petcare.service.UserCreator;
import com.petcare.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoImpl();

    // Wrapping the basic creator with the decorator, this is the only place that matters:
    // everyone who registers a user through this service gets the default properties applied
    private final UserCreator userCreator = new DefaultPropertiesUserDecorator(new BasicUserCreator());

    @Override
    public int register(User user) throws PetCareException {
        return userCreator.create(user);
    }

    @Override
    public void update(User user) throws PetCareException {
        User existing = userDao.findById(user.getId());
        if (existing == null) {
            throw new PetCareException("User not found with id " + user.getId());
        }

        userDao.update(user);
    }

    @Override
    public void deleteUser(int userId, User requestingUser) throws PetCareException {
        // Business rule straight from the requirements: RECEPCIONISTA cannot delete users
        if (requestingUser.getRole() != RoleUser.ADMIN) {
            throw new PetCareException("Only an ADMIN can delete users");
        }

        User existing = userDao.findById(userId);
        if (existing == null) {
            throw new PetCareException("User not found with id " + userId);
        }

        userDao.delete(userId);
    }

    @Override
    public User login(String username, String password) throws PetCareException {
        User user = userDao.findByUsername(username);

        // Not revealing whether the username or the password was wrong, just that credentials are invalid
        if (user == null || !user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (user.getStatus() != StateUser.ACTIVE) {
            throw new InvalidCredentialsException("User is inactive: " + username);
        }

        return user;
    }

    @Override
    public List<User> listAll() throws PetCareException {
        return userDao.findAll();
    }
}