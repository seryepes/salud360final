package com.salud.citas.application.port.in;

import com.salud.citas.domain.model.Cita;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CitaUseCase {

    Cita agendarCita(int idPaciente, int idProfesional, LocalDate fecha,
                     String hora, String motivo, String modalidad);

    Cita reprogramarCita(int idCita, LocalDate nuevaFecha, String nuevaHora);

    Cita confirmarCita(int idCita);

    Cita cancelarCita(int idCita);

    Optional<Cita> consultarCita(int idCita);

    List<Cita> consultarCitasPorPaciente(int idPaciente);
}
