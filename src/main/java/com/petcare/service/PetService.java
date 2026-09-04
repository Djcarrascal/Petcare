package com.petcare.service;

import com.petcare.exception.PetCareException;
import com.petcare.model.Pet;
import java.util.List;

// Business rules for pets live behind this contract, controllers only talk to this
public interface PetService {

    // Validates the owner is active and the medical record number is unique
    int register(Pet pet, int ownerId) throws PetCareException;

    void update(Pet pet) throws PetCareException;

    List<Pet> listByOwnerId(int ownerId) throws PetCareException;

    // Used by ConsultationService to make sure the pet exists before registering a consultation
    Pet getPetOrThrow(int petId) throws PetCareException;
}