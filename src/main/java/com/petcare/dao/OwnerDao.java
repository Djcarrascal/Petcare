package com.petcare.dao;

import com.petcare.exception.PetCareException;
import com.petcare.model.Owner;
import java.util.List;

// Contract for owner persistence, implementation details live in dao.impl
public interface OwnerDao {

    // Saves a new owner and returns the generated id
    int save(Owner owner) throws PetCareException;

    // Updates an existing owner by its id
    void update(Owner owner) throws PetCareException;

    // Looks up an owner by its unique document, used for validations
    Owner findByDocument(String document) throws PetCareException;

    // Looks up an owner by its primary key
    Owner findById(int id) throws PetCareException;

    // Returns every owner in the system
    List<Owner> findAll() throws PetCareException;
}