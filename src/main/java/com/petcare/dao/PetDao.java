package com.petcare.dao;

import com.petcare.exception.PetCareException;
import com.petcare.model.Pet;
import java.util.List;

// Contract for pet persistence, implementation details live in dao.impl
public interface PetDao {

    // Saves a new pet and returns the generated id
    int save(Pet pet) throws PetCareException;

    // Updates an existing pet by its id
    void update(Pet pet) throws PetCareException;

    // Looks up a pet by its unique medical record number
    Pet findByMedicalRecordNumber(String medicalRecordNumber) throws PetCareException;

    // Looks up a pet by its primary key
    Pet findById(int id) throws PetCareException;

    // Returns every pet that belongs to a given owner
    List<Pet> findByOwnerId(int ownerId) throws PetCareException;
}