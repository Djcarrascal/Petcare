package com.petcare.controller;

import com.petcare.enums.QueryStatus;
import com.petcare.exception.PetCareException;
import com.petcare.model.Consultation;
import com.petcare.service.ConsultationService;
import com.petcare.service.impl.ConsultationServiceImpl;
import com.petcare.util.HttpTraceLogger;

import java.util.List;

// Sits between the view and the service, no SQL and no business rules live here
public class ConsultationController {

    private final ConsultationService consultationService = new ConsultationServiceImpl();

    public int registerConsultation(Consultation consultation) throws PetCareException {
        try {
            int id = consultationService.registerConsultation(consultation);
            HttpTraceLogger.trace("POST", "/consultations", 201);
            return id;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/consultations", e);
            throw e;
        }
    }

    public void changeStatus(int consultationId, QueryStatus newStatus) throws PetCareException {
        try {
            consultationService.changeStatus(consultationId, newStatus);
            HttpTraceLogger.trace("PATCH", "/consultations/" + consultationId + "/status", 200);

        } catch (PetCareException e) {
            HttpTraceLogger.error("/consultations/" + consultationId + "/status", e);
            throw e;
        }
    }

    public void finalizeConsultation(int consultationId, String diagnosis, String treatment) throws PetCareException {
        try {
            consultationService.finalizeConsultation(consultationId, diagnosis, treatment);
            HttpTraceLogger.trace("PATCH", "/consultations/" + consultationId + "/finalize", 200);

        } catch (PetCareException e) {
            HttpTraceLogger.error("/consultations/" + consultationId + "/finalize", e);
            throw e;
        }
    }

    public Consultation getById(int consultationId) throws PetCareException {
        try {
            Consultation consultation = consultationService.getById(consultationId);
            HttpTraceLogger.trace("GET", "/consultations/" + consultationId, 200);
            return consultation;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/consultations/" + consultationId, e);
            throw e;
        }
    }

    public List<Consultation> getHistoryByPet(int petId) throws PetCareException {
        try {
            List<Consultation> history = consultationService.getHistoryByPet(petId);
            HttpTraceLogger.trace("GET", "/pets/" + petId + "/consultations", 200);
            return history;

        } catch (PetCareException e) {
            HttpTraceLogger.error("/pets/" + petId + "/consultations", e);
            throw e;
        }
    }
}