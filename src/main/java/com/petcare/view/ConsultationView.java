package com.petcare.view;

import com.petcare.controller.ConsultationController;
import com.petcare.controller.MedicationController;
import com.petcare.enums.QueryStatus;
import com.petcare.exception.PetCareException;
import com.petcare.model.Consultation;
import com.petcare.model.ConsultationMedication;
import com.petcare.model.Medication;
import com.petcare.model.Owner;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.ConsoleTableHelper;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class ConsultationView {

    private final ConsultationController consultationController = new ConsultationController();
    private final MedicationController medicationController = new MedicationController();

    public void show(User loggedUser) {
        boolean keepRunning = true;

        while (keepRunning) {
            String[] options = {"Register", "Change status", "Finalize", "View by id", "History by pet", "Back"};

            int choice = JOptionPane.showOptionDialog(
                    null, "Consultations", "PetCare Center - Consultations",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (choice == -1 || options[choice].equals("Back")) {
                keepRunning = false;
                continue;
            }

            try {
                switch (options[choice]) {
                    case "Register" -> register(loggedUser);
                    case "Change status" -> changeStatus();
                    case "Finalize" -> finalizeConsultation();
                    case "View by id" -> viewById();
                    case "History by pet" -> historyByPet();
                }
            } catch (PetCareException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void register(User loggedUser) throws PetCareException {
        int ownerId = Integer.parseInt(JOptionPane.showInputDialog("Owner id:"));
        int petId = Integer.parseInt(JOptionPane.showInputDialog("Pet id:"));
        String reason = JOptionPane.showInputDialog("Reason for the consultation:");

        // Only carrying the id here, ConsultationService looks up and validates the real Owner and Pet
        Owner owner = new Owner();
        owner.setId(ownerId);

        Pet pet = new Pet();
        pet.setId(petId);

        Consultation consultation = new Consultation();
        consultation.setOwner(owner);
        consultation.setPet(pet);
        // Keeping it simple: the logged in user is treated as the attending veterinarian
        consultation.setVeterinarian(loggedUser);
        consultation.setReason(reason);
        consultation.setMedicationsUsed(collectMedications());

        int id = consultationController.registerConsultation(consultation);
        JOptionPane.showMessageDialog(null, "Consultation registered with id " + id);
    }

    // Repeatedly asks for a medication code and quantity until the user is done adding
    private List<ConsultationMedication> collectMedications() throws PetCareException {
        List<ConsultationMedication> details = new ArrayList<>();

        boolean addingMore = true;
        while (addingMore) {
            String code = JOptionPane.showInputDialog("Medication code to add:");
            if (code == null) break;

            // Only searching among medications that currently have stock available
            Medication medication = medicationController.listWithAvailableStock().stream()
                    .filter(m -> m.getMedicationCode().equalsIgnoreCase(code))
                    .findFirst()
                    .orElse(null);

            if (medication == null) {
                JOptionPane.showMessageDialog(null, "Medication code not found or out of stock", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int quantity = Integer.parseInt(JOptionPane.showInputDialog("Quantity:"));

                ConsultationMedication detail = new ConsultationMedication();
                detail.setMedication(medication);
                detail.setQuantity(quantity);
                // Unit price and subtotal get calculated by ConsultationServiceImpl, not trusted from here
                details.add(detail);
            }

            int more = JOptionPane.showConfirmDialog(null, "Add another medication?", "Continue", JOptionPane.YES_NO_OPTION);
            addingMore = more == JOptionPane.YES_OPTION;
        }

        return details;
    }

    private void changeStatus() throws PetCareException {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Consultation id:"));

        // FINALIZADA is handled through its own dedicated flow, not offered here
        String[] statusOptions = {"EN_ATENCION", "CANCELADA"};
        int choice = JOptionPane.showOptionDialog(null, "New status:", "Status",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, statusOptions, statusOptions[0]);

        if (choice == -1) return;

        int confirm = JOptionPane.showConfirmDialog(null, "Confirm status change?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        consultationController.changeStatus(id, QueryStatus.valueOf(statusOptions[choice]));
        JOptionPane.showMessageDialog(null, "Status updated");
    }

    private void finalizeConsultation() throws PetCareException {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Consultation id to finalize:"));
        String diagnosis = JOptionPane.showInputDialog("Final diagnosis:");
        String treatment = JOptionPane.showInputDialog("Final treatment:");

        int confirm = JOptionPane.showConfirmDialog(null, "Finalize this consultation?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        consultationController.finalizeConsultation(id, diagnosis, treatment);
        JOptionPane.showMessageDialog(null, "Consultation finalized");
    }

    private void viewById() throws PetCareException {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Consultation id:"));
        Consultation consultation = consultationController.getById(id);
        JOptionPane.showMessageDialog(null, buildConsultationSummary(consultation));
    }

    private void historyByPet() throws PetCareException {
        int petId = Integer.parseInt(JOptionPane.showInputDialog("Pet id:"));
        List<Consultation> history = consultationController.getHistoryByPet(petId);

        String[] headers = {"ID", "Date", "Reason", "Status", "Total"};
        String[][] rows = new String[history.size()][headers.length];

        for (int i = 0; i < history.size(); i++) {
            Consultation c = history.get(i);
            rows[i][0] = String.valueOf(c.getId());
            rows[i][1] = String.valueOf(c.getConsultationDate());
            rows[i][2] = c.getReason();
            rows[i][3] = c.getStatus().toString();
            rows[i][4] = String.valueOf(c.getTotalCost());
        }

        String table = ConsoleTableHelper.buildTable(headers, rows);
        JOptionPane.showMessageDialog(null, table.isEmpty() ? "No consultations found for this pet" : table);
    }

    private String buildConsultationSummary(Consultation c) {
        StringBuilder summary = new StringBuilder();
        summary.append("Owner: ").append(c.getOwner().getName()).append("\n");
        summary.append("Pet: ").append(c.getPet().getName()).append("\n");
        summary.append("Veterinarian: ").append(c.getVeterinarian().getName()).append("\n");
        summary.append("Status: ").append(c.getStatus()).append("\n");
        summary.append("Reason: ").append(c.getReason()).append("\n");
        summary.append("Diagnosis: ").append(c.getDiagnosis()).append("\n");
        summary.append("Treatment: ").append(c.getTreatment()).append("\n");
        summary.append("Total cost: ").append(c.getTotalCost()).append("\n\n");
        summary.append("Medications used:\n");

        for (ConsultationMedication detail : c.getMedicationsUsed()) {
            summary.append("- ").append(detail.getMedication().getName())
                    .append(" x").append(detail.getQuantity())
                    .append(" = ").append(detail.getSubtotal()).append("\n");
        }

        return summary.toString();
    }
}