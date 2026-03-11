package com.reservas.sistema_reservas.repository;

import com.reservas.sistema_reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Repositorio para la entidad Reserva.
 * 
 * Proporciona operaciones CRUD básicas y métodos personalizados
 * para gestionar reservas en el sistema.
 * 
 * @author Sistema de Reservas
 * @version 1.0
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    /**
     * Verifica si existe una reserva para una fecha y hora específicas.
     * 
     * @param fecha La fecha de la reserva
     * @param hora La hora de la reserva
     * @return true si existe una reserva para esa fecha y hora, false en caso contrario
     */
    boolean existsByFechaAndHora(LocalDate fecha, LocalTime hora);

    boolean existsByFechaAndHoraAndIdNot(LocalDate fecha, LocalTime hora, Long id);
}
