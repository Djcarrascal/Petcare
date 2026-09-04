package com.petcare.controller;

import com.petcare.exception.PetCareException;
import com.petcare.model.Owner;
import com.petcare.service.OwnerService;
import com.petcare.service.impl.OwnerServiceImpl;
import com.petcare.util.HttpTraceLogger;

import java.util.List;

// Sits between the view and the service, no SQL and no business rules live here
public class OwnerController {

    private final OwnerService ownerService = new OwnerServiceImpl();

    public int register(Owner owner) throws PetCareException {
        try {
            int id = ownerService.register(owner);
            HttpTraceLogger.trace("POST", "/owners", 201);
            return id;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/owners", e);
            throw e;
        }
    }

    public void update(Owner owner) throws PetCareException {
        try {
            ownerService.update(owner);
            HttpTraceLogger.trace("PATCH", "/owners/" + owner.getId(), 200);

        } catch (PetCareException e) {
            HttpTraceLogger.error("/owners/" + owner.getId(), e);
            throw e;
        }
    }

    public List<Owner> listAll() throws PetCareException {
        try {
            List<Owner> owners = ownerService.listAll();
            HttpTraceLogger.trace("GET", "/owners", 200);
            return owners;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/owners", e);
            throw e;
        }
    }
}