package com.petcare.exception;

public class DuplicateMedicationCodeException extends PetCareException {
    public DuplicateMedicationCodeException(String message) {
        super(message);
    }
}