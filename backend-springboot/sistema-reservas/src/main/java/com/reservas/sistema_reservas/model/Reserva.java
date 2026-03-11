package com.reservas.sistema_reservas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad JPA que representa una reserva en el sistema.
 * 
 * Una reserva contiene la información de un cliente, fecha, hora,
 * tipo de servicio y su estado actual.
 * 
 * @author Sistema de Reservas
 * @version 1.0
 */
@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {
    
    /**
     * Identificador único de la reserva.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre del cliente que realiza la reserva.
     */
    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    @Column(nullable = false, length = 255)
    private String nombreCliente;
    
    /**
     * Fecha de la reserva.
     */
    @NotNull(message = "La fecha de la reserva no puede ser nula")
    @Column(nullable = false)
    private LocalDate fecha;
    
    /**
     * Hora de la reserva.
     */
    @NotNull(message = "La hora de la reserva no puede ser nula")
    @Column(nullable = false)
    private LocalTime hora;
    
    /**
     * Tipo de servicio a reservar.
     */
    @NotBlank(message = "El servicio no puede estar vacío")
    @Column(nullable = false, length = 255)
    private String servicio;
    
    /**
     * Estado actual de la reserva.
     * Valores posibles: ACTIVA, CANCELADA
     */
    @NotNull(message = "El estado de la reserva no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;
}
