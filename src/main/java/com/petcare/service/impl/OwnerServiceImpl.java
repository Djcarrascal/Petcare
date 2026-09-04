package com.petcare.service.impl;

import com.petcare.dao.OwnerDao;
import com.petcare.dao.impl.OwnerDaoImpl;
import com.petcare.exception.OwnerNotFoundException;
import com.petcare.exception.InactiveOwnerException;
import com.petcare.exception.PetCareException;
import com.petcare.model.Owner;
import com.petcare.service.OwnerService;

import java.time.LocalDateTime;
import java.util.List;

public class OwnerServiceImpl implements OwnerService {

    private final OwnerDao ownerDao = new OwnerDaoImpl();

    @Override
    public int register(Owner owner) throws PetCareException {
        // Business rule: document must be unique, same idea as the medication code check
        Owner existing = ownerDao.findByDocument(owner.getDocument());
        if (existing != null) {
            throw new PetCareException("Document already registered: " + owner.getDocument());
        }

        owner.setActive(true);
        owner.setCreatedAt(LocalDateTime.now());

        return ownerDao.save(owner);
    }

    @Override
    public void update(Owner owner) throws PetCareException {
        Owner existing = ownerDao.findById(owner.getId());
        if (existing == null) {
            throw new OwnerNotFoundException("Owner not found with id " + owner.getId());
        }

        ownerDao.update(owner);
    }

    @Override
    public List<Owner> listAll() throws PetCareException {
        return ownerDao.findAll();
    }

    @Override
    public Owner getActiveOwnerOrThrow(int ownerId) throws PetCareException {
        // Centralizing this check here so PetService (and later ConsultationService) can reuse it
        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new OwnerNotFoundException("Owner not found with id " + ownerId);
        }
        if (!owner.isActive()) {
            throw new InactiveOwnerException("Owner is inactive: " + owner.getName());
        }
        return owner;
    }
}