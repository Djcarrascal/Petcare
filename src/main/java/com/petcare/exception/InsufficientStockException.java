package com.petcare.exception;

public class InsufficientStockException extends PetCareException {
    public InsufficientStockException(String message) {
        super(message);
    }
}