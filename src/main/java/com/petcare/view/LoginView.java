package com.petcare.view;

import com.petcare.controller.UserController;
import com.petcare.exception.PetCareException;
import com.petcare.model.User;

import javax.swing.JOptionPane;

// Entry point screen: asks for credentials and hands control to MainMenuView on success
public class LoginView {

    private final UserController userController = new UserController();

    // Returns the logged in user, or null if the person closed the dialog instead of logging in
    public User show() {
        while (true) {
            String username = JOptionPane.showInputDialog(null, "Username:", "PetCare Center - Login", JOptionPane.PLAIN_MESSAGE);

            // Null means the user clicked Cancel or closed the window, so we stop asking
            if (username == null) {
                return null;
            }

            String password = JOptionPane.showInputDialog(null, "Password:", "PetCare Center - Login", JOptionPane.PLAIN_MESSAGE);

            if (password == null) {
                return null;
            }

            try {
                User user = userController.login(username, password);
                JOptionPane.showMessageDialog(null, "Welcome, " + user.getName());
                return user;

            } catch (PetCareException e) {
                // Showing the friendly message here, the technical detail was already logged by the controller
                JOptionPane.showMessageDialog(null, e.getMessage(), "Login failed", JOptionPane.ERROR_MESSAGE);
                // Looping back to ask again instead of crashing the app on a wrong password
            }
        }
    }
}