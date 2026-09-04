package com.petcare.service;

import com.petcare.exception.PetCareException;
import com.petcare.model.Medication;
import java.util.List;

// Business rules for medications live behind this contract, controllers only talk to this
public interface MedicationService {

    // Validates uniqueness before delegating the insert to the DAO
    int register(Medication medication) throws PetCareException;

    // Validates the medication exists before delegating the update
    void update(Medication medication) throws PetCareException;

    List<Medication> listAll() throws PetCareException;

    List<Medication> listByCategoryOrLaboratory(String category, String laboratory) throws PetCareException;

    List<Medication> listWithAvailableStock() throws PetCareException;
}