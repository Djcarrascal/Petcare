package com.petcare.service.impl;

import com.petcare.dao.ConsultationDao;
import com.petcare.dao.impl.ConsultationDaoImpl;
import com.petcare.enums.QueryStatus;
import com.petcare.exception.InvalidConsultationException;
import com.petcare.exception.ConsultationNotFoundException;
import com.petcare.exception.PetCareException;
import com.petcare.exception.InsufficientStockException;
import com.petcare.model.Consultation;
import com.petcare.model.ConsultationMedication;
import com.petcare.model.Medication;
import com.petcare.model.Owner;
import com.petcare.model.Pet;
import com.petcare.service.ConsultationService;
import com.petcare.service.OwnerService;
import com.petcare.service.PetService;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationDao consultationDao = new ConsultationDaoImpl();

    // Reusing the other services instead of talking to their DAOs directly
    private final OwnerService ownerService = new OwnerServiceImpl();
    private final PetService petService = new PetServiceImpl();

    // Mapping each state to the set of states it is allowed to move into
    private static final Map<QueryStatus, EnumSet<QueryStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(QueryStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(QueryStatus.REGISTERED, EnumSet.of(QueryStatus.IN_CONSULTATION, QueryStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(QueryStatus.IN_CONSULTATION, EnumSet.of(QueryStatus.COMPLETED, QueryStatus.CANCELLED));
        // FINALIZADA and CANCELADA are terminal, they are not added here so their allowed set is empty
    }

    @Override
    public int registerConsultation(Consultation consultation) throws PetCareException {
        // Making sure the owner and pet are valid before touching anything else
        Owner owner = ownerService.getActiveOwnerOrThrow(consultation.getOwner().getId());
        Pet pet = petService.getPetOrThrow(consultation.getPet().getId());

        if (consultation.getMedicationsUsed().isEmpty()) {
            throw new InvalidConsultationException("A consultation must include at least one medication");
        }

        // Checking every medication is active and has enough stock, and filling in unit price/subtotal
        for (ConsultationMedication detail : consultation.getMedicationsUsed()) {
            Medication medication = detail.getMedication();

            if (!medication.isActive()) {
                throw new PetCareException("Medication is not active: " + medication.getMedicationCode());
            }
            if (medication.getAvailableStock() < detail.getQuantity()) {
                throw new InsufficientStockException(
                        "Not enough stock for medication " + medication.getMedicationCode());
            }

            // The service decides the price and subtotal, not the view, so a stale price cannot slip in
            detail.setUnitPrice(medication.getUnitPrice());
            detail.setSubtotal(medication.getUnitPrice() * detail.getQuantity());
        }

        consultation.setOwner(owner);
        consultation.setPet(pet);
        consultation.setStatus(QueryStatus.REGISTERED);
        consultation.setConsultationDate(LocalDateTime.now());
        consultation.setCreatedAt(LocalDateTime.now());

        // The actual insert plus stock discount happens inside one transaction in the DAO
        return consultationDao.save(consultation);
    }

    @Override
    public void changeStatus(int consultationId, QueryStatus newStatus) throws PetCareException {
        Consultation consultation = getById(consultationId);

        validateTransition(consultation.getStatus(), newStatus);

        consultationDao.updateStatus(consultationId, newStatus);
    }

    @Override
    public void finalizeConsultation(int consultationId, String diagnosis, String treatment) throws PetCareException {
        Consultation consultation = getById(consultationId);

        // Finalizing is just a specific case of a state transition, reusing the same validation
        validateTransition(consultation.getStatus(), QueryStatus.COMPLETED);

        consultationDao.finalizeConsultation(consultationId, diagnosis, treatment);
    }

    @Override
    public Consultation getById(int consultationId) throws PetCareException {
        Consultation consultation = consultationDao.findById(consultationId);
        if (consultation == null) {
            throw new ConsultationNotFoundException("Consultation not found with id " + consultationId);
        }
        return consultation;
    }

    @Override
    public List<Consultation> getHistoryByPet(int petId) throws PetCareException {
        // Making sure the pet exists before returning its (possibly empty) history
        petService.getPetOrThrow(petId);
        return consultationDao.findByPetId(petId);
    }

    // Centralized place for the "can this status change happen" rule
    private void validateTransition(QueryStatus currentStatus, QueryStatus newStatus) throws InvalidConsultationException {
        EnumSet<QueryStatus> allowedNextStates = ALLOWED_TRANSITIONS.get(currentStatus);

        if (allowedNextStates == null || !allowedNextStates.contains(newStatus)) {
            throw new InvalidConsultationException(
                    "Cannot move consultation from " + currentStatus + " to " + newStatus);
        }
    }
}