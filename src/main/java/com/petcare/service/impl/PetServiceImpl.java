package com.petcare.service.impl;

import com.petcare.dao.PetDao;
import com.petcare.dao.impl.PetDaoImpl;
import com.petcare.exception.DuplicateMedicalRecordException;
import com.petcare.exception.PetNotFoundException;
import com.petcare.exception.PetCareException;
import com.petcare.model.Owner;
import com.petcare.model.Pet;
import com.petcare.service.OwnerService;
import com.petcare.service.PetService;

import java.time.LocalDateTime;
import java.util.List;

public class PetServiceImpl implements PetService {

    private final PetDao petDao = new PetDaoImpl();

    // Reusing OwnerService instead of duplicating the "owner active" check here
    private final OwnerService ownerService = new OwnerServiceImpl();

    @Override
    public int register(Pet pet, int ownerId) throws PetCareException {
        // This throws OwnerNotFoundException or InactiveOwnerException on its own if something is wrong
        Owner owner = ownerService.getActiveOwnerOrThrow(ownerId);

        // Business rule: medical record number must be unique
        Pet existing = petDao.findByMedicalRecordNumber(pet.getMedicalRecordNumber());
        if (existing != null) {
            throw new DuplicateMedicalRecordException(
                    "Medical record number already exists: " + pet.getMedicalRecordNumber());
        }

        pet.setOwner(owner);
        pet.setActive(true);
        pet.setCreatedAt(LocalDateTime.now());

        return petDao.save(pet);
    }

    @Override
    public void update(Pet pet) throws PetCareException {
        Pet existing = petDao.findById(pet.getId());
        if (existing == null) {
            throw new PetNotFoundException("Pet not found with id " + pet.getId());
        }

        petDao.update(pet);
    }

    @Override
    public List<Pet> listByOwnerId(int ownerId) throws PetCareException {
        // Making sure the owner actually exists before listing, avoids returning an empty list silently
        ownerService.getActiveOwnerOrThrow(ownerId);
        return petDao.findByOwnerId(ownerId);
    }

    @Override
    public Pet getPetOrThrow(int petId) throws PetCareException {
        Pet pet = petDao.findById(petId);
        if (pet == null) {
            throw new PetNotFoundException("Pet not found with id " + petId);
        }
        return pet;
    }
}