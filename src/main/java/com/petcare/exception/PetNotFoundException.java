package com.petcare.exception;

public class PetNotFoundException extends PetCareException {
    public PetNotFoundException(String message) {
        super(message);
    }
}