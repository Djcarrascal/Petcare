package com.petcare.view;

import com.petcare.controller.OwnerController;
import com.petcare.exception.PetCareException;
import com.petcare.model.Owner;
import com.petcare.util.ConsoleTableHelper;

import javax.swing.JOptionPane;
import java.util.List;

public class OwnerView {

    private final OwnerController ownerController = new OwnerController();

    public void show() {
        boolean keepRunning = true;

        while (keepRunning) {
            String[] options = {"Register", "List all", "Update", "Back"};

            int choice = JOptionPane.showOptionDialog(
                    null, "Owners", "PetCare Center - Owners",
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
                }
            } catch (PetCareException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void register() throws PetCareException {
        String name = JOptionPane.showInputDialog("Name:");
        if (name == null) return;

        String document = JOptionPane.showInputDialog("Document:");
        String phone = JOptionPane.showInputDialog("Phone:");
        String email = JOptionPane.showInputDialog("Email:");
        String address = JOptionPane.showInputDialog("Address:");

        Owner owner = new Owner();
        owner.setName(name);
        owner.setDocument(document);
        owner.setPhone(phone);
        owner.setEmail(email);
        owner.setAddress(address);

        int id = ownerController.register(owner);
        JOptionPane.showMessageDialog(null, "Owner registered with id " + id);
    }

    private void listAll() throws PetCareException {
        List<Owner> owners = ownerController.listAll();

        String[] headers = {"ID", "Name", "Document", "Phone", "Email", "Active"};
        String[][] rows = new String[owners.size()][headers.length];

        for (int i = 0; i < owners.size(); i++) {
            Owner o = owners.get(i);
            rows[i][0] = String.valueOf(o.getId());
            rows[i][1] = o.getName();
            rows[i][2] = o.getDocument();
            rows[i][3] = o.getPhone();
            rows[i][4] = o.getEmail();
            rows[i][5] = o.isActive() ? "[ACTIVE]" : "[INACTIVE]";
        }

        String table = ConsoleTableHelper.buildTable(headers, rows);
        JOptionPane.showMessageDialog(null, table.isEmpty() ? "No owners found" : table);
    }

    private void update() throws PetCareException {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Owner id to update:"));
        String name = JOptionPane.showInputDialog("New name:");
        String phone = JOptionPane.showInputDialog("New phone:");
        String email = JOptionPane.showInputDialog("New email:");
        String address = JOptionPane.showInputDialog("New address:");

        int confirm = JOptionPane.showConfirmDialog(null, "Save changes?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Owner owner = new Owner();
        owner.setId(id);
        owner.setName(name);
        owner.setPhone(phone);
        owner.setEmail(email);
        owner.setAddress(address);
        owner.setActive(true);

        ownerController.update(owner);
        JOptionPane.showMessageDialog(null, "Owner updated");
    }
}