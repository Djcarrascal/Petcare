package com.petcare.dao;

import com.petcare.exception.PetCareException;
import com.petcare.model.Medication;
import java.util.List;

// Contract for medication persistence, implementation details live in dao.impl
public interface MedicationDao {

    // Saves a new medication and returns the generated id
    int save(Medication medication) throws PetCareException;

    // Updates an existing medication by its id
    void update(Medication medication) throws PetCareException;

    // Looks up a medication by its unique code, used for the uniqueness check
    Medication findByCode(String code) throws PetCareException;

    // Looks up a medication by its primary key
    Medication findById(int id) throws PetCareException;

    // Returns every medication in the system
    List<Medication> findAll() throws PetCareException;

    // Returns medications filtered by category or laboratory
    List<Medication> findByCategoryOrLaboratory(String category, String laboratory) throws PetCareException;

    // Returns only medications that currently have stock available
    List<Medication> findWithAvailableStock() throws PetCareException;
}