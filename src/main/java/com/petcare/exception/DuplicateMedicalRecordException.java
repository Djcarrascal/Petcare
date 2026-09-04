package com.petcare.exception;

public class DuplicateMedicalRecordException extends PetCareException {
    public DuplicateMedicalRecordException(String message) {
        super(message);
    }
}