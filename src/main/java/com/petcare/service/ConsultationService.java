package com.petcare.service;

import com.petcare.enums.QueryStatus;
import com.petcare.exception.PetCareException;
import com.petcare.model.Consultation;
import java.util.List;

// Business rules for veterinary consultations live behind this contract
public interface ConsultationService {

    // Validates owner/pet/medications and delegates the transactional insert to the DAO
    int registerConsultation(Consultation consultation) throws PetCareException;

    // Validates the state transition is allowed before delegating to the DAO
    void changeStatus(int consultationId, QueryStatus newStatus) throws PetCareException;

    // Closes out a consultation, only allowed from EN_ATENCION
    void finalizeConsultation(int consultationId, String diagnosis, String treatment) throws PetCareException;

    Consultation getById(int consultationId) throws PetCareException;

    List<Consultation> getHistoryByPet(int petId) throws PetCareException;
}