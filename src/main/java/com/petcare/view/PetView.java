package com.petcare.view;

import com.petcare.controller.PetController;
import com.petcare.exception.PetCareException;
import com.petcare.model.Pet;
import com.petcare.util.ConsoleTableHelper;

import javax.swing.JOptionPane;
import java.util.List;

public class PetView {

    private final PetController petController = new PetController();

    public void show() {
        boolean keepRunning = true;

        while (keepRunning) {
            String[] options = {"Register", "List by owner", "Update", "Back"};

            int choice = JOptionPane.showOptionDialog(
                    null, "Pets", "PetCare Center - Pets",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (choice == -1 || options[choice].equals("Back")) {
                keepRunning = false;
                continue;
            }

            try {
                switch (options[choice]) {
                    case "Register" -> register();
                    case "List by owner" -> listByOwner();
                    case "Update" -> update();
                }
            } catch (PetCareException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void register() throws PetCareException {
        int ownerId = Integer.parseInt(JOptionPane.showInputDialog("Owner id:"));
        String medicalRecordNumber = JOptionPane.showInputDialog("Medical record number:");
        String name = JOptionPane.showInputDialog("Pet name:");
        String species = JOptionPane.showInputDialog("Species:");
        String breed = JOptionPane.showInputDialog("Breed:");
        int age = Integer.parseInt(JOptionPane.showInputDialog("Age:"));

        Pet pet = new Pet();
        pet.setMedicalRecordNumber(medicalRecordNumber);
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setAge(age);

        // The owner id travels separately, PetService is the one that fetches and validates the full Owner
        int id = petController.register(pet, ownerId);
        JOptionPane.showMessageDialog(null, "Pet registered with id " + id);
    }

    private void listByOwner() throws PetCareException {
        int ownerId = Integer.parseInt(JOptionPane.showInputDialog("Owner id:"));
        List<Pet> pets = petController.listByOwnerId(ownerId);

        String[] headers = {"ID", "Record #", "Name", "Species", "Breed", "Age", "Active"};
        String[][] rows = new String[pets.size()][headers.length];

        for (int i = 0; i < pets.size(); i++) {
            Pet p = pets.get(i);
            rows[i][0] = String.valueOf(p.getId());
            rows[i][1] = p.getMedicalRecordNumber();
            rows[i][2] = p.getName();
            rows[i][3] = p.getSpecies();
            rows[i][4] = p.getBreed();
            rows[i][5] = String.valueOf(p.getAge());
            rows[i][6] = p.isActive() ? "[ACTIVE]" : "[INACTIVE]";
        }

        String table = ConsoleTableHelper.buildTable(headers, rows);
        JOptionPane.showMessageDialog(null, table.isEmpty() ? "No pets found for this owner" : table);
    }

    private void update() throws PetCareException {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Pet id to update:"));
        String name = JOptionPane.showInputDialog("New name:");
        String species = JOptionPane.showInputDialog("New species:");
        String breed = JOptionPane.showInputDialog("New breed:");
        int age = Integer.parseInt(JOptionPane.showInputDialog("New age:"));

        int confirm = JOptionPane.showConfirmDialog(null, "Save changes?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setAge(age);
        pet.setActive(true);

        petController.update(pet);
        JOptionPane.showMessageDialog(null, "Pet updated");
    }
}