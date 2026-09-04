package com.petcare.exception;

public class MedicationNotFoundException extends PetCareException {
    public MedicationNotFoundException(String message) {
        super(message);
    }
}