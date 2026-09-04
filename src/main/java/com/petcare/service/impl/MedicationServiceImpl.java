package com.petcare.service.impl;

import com.petcare.dao.MedicationDao;
import com.petcare.dao.impl.MedicationDaoImpl;
import com.petcare.exception.DuplicateMedicationCodeException;
import com.petcare.exception.MedicationNotFoundException;
import com.petcare.exception.PetCareException;
import com.petcare.model.Medication;
import com.petcare.service.MedicationService;

import java.time.LocalDateTime;
import java.util.List;

public class MedicationServiceImpl implements MedicationService {

    // Depending on the interface, not the concrete class, even though we build it directly here
    private final MedicationDao medicationDao = new MedicationDaoImpl();

    @Override
    public int register(Medication medication) throws PetCareException {
        // Business rule: the medication code must be unique before we insert
        Medication existing = medicationDao.findByCode(medication.getMedicationCode());
        if (existing != null) {
            throw new DuplicateMedicationCodeException(
                    "Medication code already exists: " + medication.getMedicationCode());
        }

        // Business rule: stock cannot start negative
        if (medication.getTotalStock() < 0 || medication.getAvailableStock() < 0) {
            throw new PetCareException("Stock values cannot be negative");
        }

        medication.setActive(true);
        medication.setCreatedAt(LocalDateTime.now());

        return medicationDao.save(medication);
    }

    @Override
    public void update(Medication medication) throws PetCareException {
        // Business rule: cannot update something that does not exist
        Medication existing = medicationDao.findById(medication.getId());
        if (existing == null) {
            throw new MedicationNotFoundException("Medication not found with id " + medication.getId());
        }

        if (medication.getTotalStock() < 0 || medication.getAvailableStock() < 0) {
            throw new PetCareException("Stock values cannot be negative");
        }

        medicationDao.update(medication);
    }

    @Override
    public List<Medication> listAll() throws PetCareException {
        return medicationDao.findAll();
    }

    @Override
    public List<Medication> listByCategoryOrLaboratory(String category, String laboratory) throws PetCareException {
        return medicationDao.findByCategoryOrLaboratory(category, laboratory);
    }

    @Override
    public List<Medication> listWithAvailableStock() throws PetCareException {
        return medicationDao.findWithAvailableStock();
    }
}