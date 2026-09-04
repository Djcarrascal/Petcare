package com.petcare.view;

import com.petcare.controller.MedicationController;
import com.petcare.exception.PetCareException;
import com.petcare.model.Medication;
import com.petcare.util.ConsoleTableHelper;

import javax.swing.JOptionPane;
import java.util.List;

public class MedicationView {

    private final MedicationController medicationController = new MedicationController();

    public void show() {
        boolean keepRunning = true;

        while (keepRunning) {
            String[] options = {"Register", "List all", "Filter by category/lab", "List with stock", "Update", "Back"};

            int choice = JOptionPane.showOptionDialog(
                    null, "Medications", "PetCare Center - Medications",
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
                    case "Filter by category/lab" -> filter();
                    case "List with stock" -> listWithStock();
                    case "Update" -> update();
                }
            } catch (PetCareException e) {
                // The technical detail was already logged by the controller, here we just show the friendly part
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void register() throws PetCareException {
        String code = JOptionPane.showInputDialog("Medication code:");
        if (code == null) return; // user cancelled

        String name = JOptionPane.showInputDialog("Name:");
        String category = JOptionPane.showInputDialog("Category:");
        String laboratory = JOptionPane.showInputDialog("Laboratory:");
        int totalStock = Integer.parseInt(JOptionPane.showInputDialog("Total stock:"));
        double unitPrice = Double.parseDouble(JOptionPane.showInputDialog("Unit price:"));

        Medication medication = new Medication();
        medication.setMedicationCode(code);
        medication.setName(name);
        medication.setCategory(category);
        medication.setLaboratory(laboratory);
        medication.setTotalStock(totalStock);
        // Available stock starts equal to total stock when a medication is first registered
        medication.setAvailableStock(totalStock);
        medication.setUnitPrice(unitPrice);

        int id = medicationController.register(medication);
        JOptionPane.showMessageDialog(null, "Medication registered with id " + id);
    }

    private void listAll() throws PetCareException {
        showMedicationsTable(medicationController.listAll());
    }

    private void filter() throws PetCareException {
        String category = JOptionPane.showInputDialog("Category (leave empty to skip):");
        String laboratory = JOptionPane.showInputDialog("Laboratory (leave empty to skip):");
        showMedicationsTable(medicationController.listByCategoryOrLaboratory(category, laboratory));
    }

    private void listWithStock() throws PetCareException {
        showMedicationsTable(medicationController.listWithAvailableStock());
    }

    private void update() throws PetCareException {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Medication id to update:"));
        String name = JOptionPane.showInputDialog("New name:");
        String category = JOptionPane.showInputDialog("New category:");
        String laboratory = JOptionPane.showInputDialog("New laboratory:");
        int totalStock = Integer.parseInt(JOptionPane.showInputDialog("New total stock:"));
        int availableStock = Integer.parseInt(JOptionPane.showInputDialog("New available stock:"));
        double unitPrice = Double.parseDouble(JOptionPane.showInputDialog("New unit price:"));

        int confirm = JOptionPane.showConfirmDialog(null, "Save changes?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Medication medication = new Medication();
        medication.setId(id);
        medication.setName(name);
        medication.setCategory(category);
        medication.setLaboratory(laboratory);
        medication.setTotalStock(totalStock);
        medication.setAvailableStock(availableStock);
        medication.setUnitPrice(unitPrice);
        medication.setActive(true);

        medicationController.update(medication);
        JOptionPane.showMessageDialog(null, "Medication updated");
    }

    private void showMedicationsTable(List<Medication> medications) {
        String[] headers = {"ID", "Code", "Name", "Category", "Stock", "Price", "Active"};
        String[][] rows = new String[medications.size()][headers.length];

        for (int i = 0; i < medications.size(); i++) {
            Medication m = medications.get(i);
            rows[i][0] = String.valueOf(m.getId());
            rows[i][1] = m.getMedicationCode();
            rows[i][2] = m.getName();
            rows[i][3] = m.getCategory();
            rows[i][4] = String.valueOf(m.getAvailableStock());
            rows[i][5] = String.valueOf(m.getUnitPrice());
            rows[i][6] = m.isActive() ? "[ACTIVE]" : "[INACTIVE]";
        }

        String table = ConsoleTableHelper.buildTable(headers, rows);
        JOptionPane.showMessageDialog(null, table.isEmpty() ? "No medications found" : table);
    }
}