package com.petcare.view;

import com.petcare.enums.RoleUser;
import com.petcare.model.User;

import javax.swing.JOptionPane;

// Root menu shown after login, options differ depending on the logged in user's role
public class MainMenuView {

    private final User loggedUser;

    private final MedicationView medicationView = new MedicationView();
    private final OwnerView ownerView = new OwnerView();
    private final PetView petView = new PetView();
    private final UserView userView;
    private final ConsultationView consultationView = new ConsultationView();

    public MainMenuView(User loggedUser) {
        this.loggedUser = loggedUser;
        // UserView needs to know who is logged in, so it can enforce the "only ADMIN deletes" rule
        this.userView = new UserView(loggedUser);
    }

    public void show() {
        boolean keepRunning = true;

        while (keepRunning) {
            String[] options = buildOptionsForRole();

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Logged in as: " + loggedUser.getName() + " (" + loggedUser.getRole() + ")",
                    "PetCare Center - Main Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            // choice is -1 when the user closes the dialog with the X button
            if (choice == -1 || options[choice].equals("Exit")) {
                keepRunning = false;
                continue;
            }

            routeSelection(options[choice]);
        }
    }

    // Building the menu differently depending on role, instead of showing options that would fail later
    private String[] buildOptionsForRole() {
        if (loggedUser.getRole() == RoleUser.ADMIN) {
            return new String[]{"Medications", "Owners", "Pets", "Consultations", "Users", "Exit"};
        }
        return new String[]{"Medications", "Owners", "Pets", "Consultations", "Exit"};
    }

    private void routeSelection(String selection) {
        switch (selection) {
            case "Medications" -> medicationView.show();
            case "Owners" -> ownerView.show();
            case "Pets" -> petView.show();
            case "Consultations" -> consultationView.show(loggedUser);
            case "Users" -> userView.show();
            default -> { /* Exit is handled in the loop above, nothing to do here */ }
        }
    }
}