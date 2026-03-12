package com.reservas.sistema_reservas.controller;

import com.reservas.sistema_reservas.exception.ReservaException;
import com.reservas.sistema_reservas.model.Reserva;
import com.reservas.sistema_reservas.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing reservations.
 * Exposes endpoints for listing, creating, and canceling reservations.
 */
@CrossOrigin(origins = "http://localhost:4200") // Allow CORS for Angular frontend
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    /**
     * Constructs a new ReservaController with the given service.
     *
     * @param reservaService the service for reservation business logic
     */
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * Retrieves all reservations.
     *
     * @return a ResponseEntity containing a list of all reservations with HTTP 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<Reserva>> getAllReservas() {
        List<Reserva> reservas = reservaService.getAllReservas();
        return ResponseEntity.ok(reservas);
    }

    /**
     * Creates a new reservation.
     * Returns HTTP 201 Created if successful, or HTTP 409 Conflict if a reservation
     * already exists for the given date and time.
     *
     * @param reserva the reservation to create
     * @return a ResponseEntity containing the created reservation with HTTP 201 Created status
     * @throws ReservaException if a reservation already exists for the given date and time
     */
    @PostMapping
    public ResponseEntity<?> createReserva(@RequestBody Reserva reserva) {
        try {
            Reserva createdReserva = reservaService.createReserva(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReserva);
        } catch (ReservaException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Updates a reservation by its ID.
     * Returns HTTP 200 OK if successful, or HTTP 404 Not Found / HTTP 409 Conflict
     * if the reservation is not found or a conflicting reservation exists.
     *
     * @param id the ID of the reservation to update
     * @param reserva the updated reservation data
     * @return a ResponseEntity with HTTP 200 OK status if successful
     * @throws ReservaException if the reservation is not found or conflicting
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReserva(@PathVariable Long id, @RequestBody Reserva reserva) {
        try {
            Reserva updated = reservaService.updateReserva(id, reserva);
            return ResponseEntity.ok(updated);
        } catch (ReservaException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Deletes a reservation by its ID.
     * Returns HTTP 204 No Content if successful, or HTTP 404 Not Found
     * if the reservation is not found.
     *
     * @param id the ID of the reservation to delete
     * @return a ResponseEntity with HTTP 204 No Content status if successful
     * @throws ReservaException if the reservation is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        try {
            reservaService.deleteReserva(id);
            return ResponseEntity.noContent().build();
        } catch (ReservaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Cancels a reservation by its ID using a PATCH endpoint.
     * Mirrors the frontend expectation of /api/reservas/{id}/cancelar.
     *
     * @param id the ID of the reservation to cancel
     * @return a ResponseEntity with HTTP 204 No Content status if successful
     * @throws ReservaException if the reservation is not found or already cancelled
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelReservaPatch(@PathVariable Long id) {
        try {
            reservaService.cancelReserva(id);
            return ResponseEntity.noContent().build();
        } catch (ReservaException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
