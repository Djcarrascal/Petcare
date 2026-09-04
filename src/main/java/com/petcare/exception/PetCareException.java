package com.petcare.exception;

public class PetCareException extends Exception {

    public PetCareException(String message) {
        super(message);
    }

    public PetCareException(String message, Throwable cause) {
        super(message, cause);
    }
}