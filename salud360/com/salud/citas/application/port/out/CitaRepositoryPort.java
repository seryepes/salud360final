package com.salud.citas.application.port.out;

import com.salud.citas.domain.model.Cita;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CitaRepositoryPort {

    /** Guarda (si es nueva) o actualiza (si ya existe) la cita. */
    Cita guardar(Cita cita);

    Optional<Cita> buscarPorId(int idCita);

    List<Cita> buscarPorPaciente(int idPaciente);

    List<Cita> buscarPorProfesional(int idProfesional);

    /** Sirve para evitar dos citas del mismo profesional a la misma hora. */
    boolean existeCitaActiva(int idProfesional, LocalDate fecha, String hora);
}
