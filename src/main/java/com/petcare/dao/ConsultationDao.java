package com.petcare.dao;

import com.petcare.enums.QueryStatus;
import com.petcare.exception.PetCareException;
import com.petcare.model.Consultation;
import java.util.List;

// Contract for consultation persistence, implementation details live in dao.impl
public interface ConsultationDao {

    // Registers a new consultation together with its medications, this runs as one transaction
    int save(Consultation consultation) throws PetCareException;

    // Simple state transition, e.g. REGISTRADA -> EN_ATENCION or -> CANCELADA
    void updateStatus(int consultationId, QueryStatus newStatus) throws PetCareException;

    // Closes out a consultation: sets FINALIZADA, saves the final diagnosis/treatment and totals the cost
    void finalizeConsultation(int consultationId, String diagnosis, String treatment) throws PetCareException;

    // Looks up a consultation by its primary key, including its medications
    Consultation findById(int id) throws PetCareException;

    // Returns the consultation history for a given pet
    List<Consultation> findByPetId(int petId) throws PetCareException;
}