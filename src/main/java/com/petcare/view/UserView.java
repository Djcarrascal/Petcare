package com.petcare.view;

import com.petcare.controller.UserController;
import com.petcare.enums.StateUser;
import com.petcare.enums.RoleUser;
import com.petcare.exception.PetCareException;
import com.petcare.model.User;
import com.petcare.util.ConsoleTableHelper;

import javax.swing.JOptionPane;
import java.util.List;

public class UserView {

    private final UserController userController = new UserController();
    private final User loggedUser;

    public UserView(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void show() {
        boolean keepRunning = true;

        while (keepRunning) {
            // Delete only appears for an ADMIN, matching the restriction enforced again in the service
            String[] options = loggedUser.getRole() == RoleUser.ADMIN
                    ? new String[]{"Register", "List all", "Update", "Delete", "Back"}
                    : new String[]{"Register", "List all", "Update", "Back"};

            int choice = JOptionPane.showOptionDialog(
                    null, "Users", "PetCare Center - Users",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (choice == -1 || options[choice].equals("Back")) {
                keepRunning = false;
                continue;
            }

            try {
                switch (options[choice]) {
                    case "Register" -> register();
                    case "List all" -> listAll();
                    case "Update" -> update();
                    case "Delete" -> delete();
                }
            } catch (PetCareException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void register() throws PetCareException {
        String username = JOptionPane.showInputDialog("Username:");
        if (username == null) return;

        String password = JOptionPane.showInputDialog("Password:");
        String name = JOptionPane.showInputDialog("Full name:");

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);

        // Only an ADMIN gets to explicitly pick a role; otherwise role stays null and the
        // DefaultPropertiesUserDecorator fills in RECEPCIONISTA automatically
        if (loggedUser.getRole() == RoleUser.ADMIN) {
            int isAdmin = JOptionPane.showConfirmDialog(null, "Should this user be an ADMIN?", "Role", JOptionPane.YES_NO_OPTION);
            if (isAdmin == JOptionPane.YES_OPTION) {
                user.setRole(RoleUser.ADMIN);
            }
        }

        int id = userController.register(user);
        JOptionPane.showMessageDialog(null, "User registered with id " + id);
    }

    private void listAll() throws PetCareException {
        List<User> users = userController.listAll();

        String[] headers = {"ID", "Username", "Name", "Role", "Status"};
        String[][] rows = new String[users.size()][headers.length];

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            rows[i][0] = String.valueOf(u.getId());
            rows[i][1] = u.getUsername();
            rows[i][2] = u.getName();
            rows[i][3] = u.getRole().toString();
            rows[i][4] = u.getStatus().toString();
        }

        String table = ConsoleTableHelper.buildTable(headers, rows);
        JOptionPane.showMessageDialog(null, table.isEmpty() ? "No users found" : table);
    }

    private void update() throws PetCareException {
        int id = Integer.parseInt(JOptionPane.showInputDialog("User id to update:"));
        String name = JOptionPane.showInputDialog("New full name:");

        String[] roleOptions = {"ADMIN", "RECEPCIONISTA"};
        int roleChoice = JOptionPane.showOptionDialog(null, "New role:", "Role",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, roleOptions, roleOptions[0]);

        String[] statusOptions = {"ACTIVO", "INACTIVO"};
        int statusChoice = JOptionPane.showOptionDialog(null, "New status:", "Status",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, statusOptions, statusOptions[0]);

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setRole(RoleUser.valueOf(roleOptions[roleChoice]));
        user.setStatus(StateUser.valueOf(statusOptions[statusChoice]));

        userController.update(user);
        JOptionPane.showMessageDialog(null, "User updated");
    }

    private void delete() throws PetCareException {
        int id = Integer.parseInt(JOptionPane.showInputDialog("User id to delete:"));

        // Confirming before a destructive action, as the requirement asks for sensitive operations
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        userController.deleteUser(id, loggedUser);
        JOptionPane.showMessageDialog(null, "User deleted");
    }
}