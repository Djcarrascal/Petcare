package com.petcare.exception;

public class OwnerNotFoundException extends PetCareException {
    public OwnerNotFoundException(String message) {
        super(message);
    }
}