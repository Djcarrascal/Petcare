package com.petcare.controller;

import com.petcare.exception.PetCareException;
import com.petcare.model.Pet;
import com.petcare.service.PetService;
import com.petcare.service.impl.PetServiceImpl;
import com.petcare.util.HttpTraceLogger;

import java.util.List;

// Sits between the view and the service, no SQL and no business rules live here
public class PetController {

    private final PetService petService = new PetServiceImpl();

    public int register(Pet pet, int ownerId) throws PetCareException {
        try {
            int id = petService.register(pet, ownerId);
            HttpTraceLogger.trace("POST", "/pets", 201);
            return id;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/pets", e);
            throw e;
        }
    }

    public void update(Pet pet) throws PetCareException {
        try {
            petService.update(pet);
            HttpTraceLogger.trace("PATCH", "/pets/" + pet.getId(), 200);

        } catch (PetCareException e) {
            HttpTraceLogger.error("/pets/" + pet.getId(), e);
            throw e;
        }
    }

    public List<Pet> listByOwnerId(int ownerId) throws PetCareException {
        try {
            List<Pet> pets = petService.listByOwnerId(ownerId);
            HttpTraceLogger.trace("GET", "/owners/" + ownerId + "/pets", 200);
            return pets;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/owners/" + ownerId + "/pets", e);
            throw e;
        }
    }
}