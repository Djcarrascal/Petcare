package com.petcare.controller;

import com.petcare.exception.PetCareException;
import com.petcare.model.Medication;
import com.petcare.service.MedicationService;
import com.petcare.service.impl.MedicationServiceImpl;
import com.petcare.util.HttpTraceLogger;

import java.util.List;

// Sits between the view and the service, no SQL and no business rules live here
public class MedicationController {

    private final MedicationService medicationService = new MedicationServiceImpl();

    public int register(Medication medication) throws PetCareException {
        try {
            int id = medicationService.register(medication);
            // Simulating a POST call trace, same idea the requirement asks for on users
            HttpTraceLogger.trace("POST", "/medications", 201);
            return id;

        } catch (PetCareException e) {
            // Logging the technical detail here, the view will show the friendly message
            HttpTraceLogger.error("/medications", e);
            throw e;
        }
    }

    public void update(Medication medication) throws PetCareException {
        try {
            medicationService.update(medication);
            HttpTraceLogger.trace("PATCH", "/medications/" + medication.getId(), 200);

        } catch (PetCareException e) {
            HttpTraceLogger.error("/medications/" + medication.getId(), e);
            throw e;
        }
    }

    public List<Medication> listAll() throws PetCareException {
        try {
            List<Medication> medications = medicationService.listAll();
            HttpTraceLogger.trace("GET", "/medications", 200);
            return medications;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/medications", e);
            throw e;
        }
    }

    public List<Medication> listByCategoryOrLaboratory(String category, String laboratory) throws PetCareException {
        try {
            List<Medication> medications = medicationService.listByCategoryOrLaboratory(category, laboratory);
            HttpTraceLogger.trace("GET", "/medications?filter", 200);
            return medications;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/medications?filter", e);
            throw e;
        }
    }

    public List<Medication> listWithAvailableStock() throws PetCareException {
        try {
            List<Medication> medications = medicationService.listWithAvailableStock();
            HttpTraceLogger.trace("GET", "/medications?inStock=true", 200);
            return medications;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/medications?inStock=true", e);
            throw e;
        }
    }
}