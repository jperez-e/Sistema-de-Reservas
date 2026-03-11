 package com.reservas.sistema_reservas.service;

import com.reservas.sistema_reservas.exception.ReservaException;
import com.reservas.sistema_reservas.model.EstadoReserva;
import com.reservas.sistema_reservas.model.Reserva;
import com.reservas.sistema_reservas.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class containing the business logic for the reservation system.
 * Handles creation and cancellation of reservations with validation.
 */
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    /**
     * Constructs a new ReservaService with the given repository.
     *
     * @param reservaRepository the repository for Reserva entities
     */
    @Autowired
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    /**
     * Creates a new reservation if no reservation exists for the same date and time.
     * Throws ReservaException if a reservation already exists.
     *
     * @param reserva the reservation to create
     * @return the created reservation
     * @throws ReservaException if a reservation already exists for the given date and time
     */
    public Reserva createReserva(Reserva reserva) {
        if (reservaRepository.existsByFechaAndHora(reserva.getFecha(), reserva.getHora())) {
            throw new ReservaException("A reservation already exists for the given date and time.");
        }
        return reservaRepository.save(reserva);
    }

    /**
     * Retrieves all reservations from the database.
     *
     * @return a list of all reservations
     */
    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    /**
     * Cancels a reservation by its ID.
     * Throws ReservaException if the reservation is not found or already cancelled.
     *
     * @param id the ID of the reservation to cancel
     * @throws ReservaException if the reservation is not found or already cancelled
     */
    public void cancelReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaException("Reservation not found."));
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReservaException("Reservation is already cancelled.");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }
}