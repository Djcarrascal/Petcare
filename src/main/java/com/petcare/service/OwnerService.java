package com.petcare.service;

import com.petcare.exception.PetCareException;
import com.petcare.model.Owner;
import java.util.List;

// Business rules for owners live behind this contract, controllers only talk to this
public interface OwnerService {

    // Validates uniqueness of the document before delegating the insert to the DAO
    int register(Owner owner) throws PetCareException;

    void update(Owner owner) throws PetCareException;

    List<Owner> listAll() throws PetCareException;

    // Used by PetService to check an owner is active before a pet gets registered
    Owner getActiveOwnerOrThrow(int ownerId) throws PetCareException;
}