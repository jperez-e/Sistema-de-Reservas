package com.reservas.sistema_reservas.exception;

/**
 * Custom exception for business rule violations in the reservation system.
 */
public class ReservaException extends RuntimeException {

    /**
     * Constructs a new ReservaException with the specified detail message.
     *
     * @param message the detail message
     */
    public ReservaException(String message) {
        super(message);
    }
}