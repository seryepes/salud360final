package com.salud.citas.application.service;

import com.salud.citas.application.port.in.CitaUseCase;
import com.salud.citas.application.port.out.CitaRepositoryPort;
import com.salud.citas.application.port.out.ProfesionalRepositoryPort;
import com.salud.citas.application.port.out.UsuarioRepositoryPort;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.ConfirmacionCita;
import com.salud.citas.domain.model.EstadoCita;
import com.salud.citas.domain.model.EstadoPago;
import com.salud.citas.domain.model.Paciente;
import com.salud.citas.domain.model.Profesional;
import com.salud.citas.util.Validaciones;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CitaService implements CitaUseCase {

    private final CitaRepositoryPort citaRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final ProfesionalRepositoryPort profesionalRepository;

    public CitaService(CitaRepositoryPort citaRepository,
                       UsuarioRepositoryPort usuarioRepository,
                       ProfesionalRepositoryPort profesionalRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesionalRepository = profesionalRepository;
    }

    @Override
    public Cita agendarCita(int idPaciente, int idProfesional, LocalDate fecha,
                            String hora, String motivo, String modalidad) {
        Paciente paciente = usuarioRepository.buscarPacientePorId(idPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        Profesional profesional = profesionalRepository.buscarPorId(idProfesional)
                .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado"));

        validarFechaFutura(fecha);
        validarHora(hora);
        validarHorarioLibre(idProfesional, fecha, hora);

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setProfesional(profesional);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setMotivo(motivo);
        cita.setModalidad(modalidad);
        cita.setEstado(EstadoCita.PENDIENTE);

        return citaRepository.guardar(cita);
    }

    @Override
    public Cita reprogramarCita(int idCita, LocalDate nuevaFecha, String nuevaHora) {
        Cita cita = obtenerCita(idCita);

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalStateException("No se puede reprogramar una cita cancelada");
        }
        validarFechaFutura(nuevaFecha);
        validarHora(nuevaHora);
        validarHorarioLibre(cita.getProfesional().getIdProfesional(), nuevaFecha, nuevaHora);

        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);
        cita.setEstado(EstadoCita.REPROGRAMADA);
        return citaRepository.guardar(cita);
    }

    @Override
    public Cita confirmarCita(int idCita) {
        Cita cita = obtenerCita(idCita);

        if (cita.getEstado() != EstadoCita.PENDIENTE && cita.getEstado() != EstadoCita.REPROGRAMADA) {
            throw new IllegalStateException("Solo se pueden confirmar citas pendientes o reprogramadas");
        }
        // Regla de negocio: la cita se confirma una vez pagada
        if (cita.getPago() == null || cita.getPago().getEstadoPago() != EstadoPago.APROBADO) {
            throw new IllegalStateException("La cita debe estar pagada para poder confirmarse");
        }

        ConfirmacionCita confirmacion = new ConfirmacionCita();
        confirmacion.setIdConfirmacion(cita.getIdCita());
        confirmacion.setFechaConfirmacion(LocalDateTime.now());
        confirmacion.setCodigoConfirmacion("CIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        confirmacion.setEstado("CONFIRMADA");
        confirmacion.setNotificaciones(new ArrayList<>());

        cita.setConfirmacion(confirmacion);
        cita.setEstado(EstadoCita.CONFIRMADA);
        return citaRepository.guardar(cita);
    }

    @Override
    public Cita cancelarCita(int idCita) {
        Cita cita = obtenerCita(idCita);

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalStateException("La cita ya está cancelada");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        if (cita.getConfirmacion() != null) {
            cita.getConfirmacion().setEstado("CANCELADA");
        }
        return citaRepository.guardar(cita);
    }

    @Override
    public Optional<Cita> consultarCita(int idCita) {
        return citaRepository.buscarPorId(idCita);
    }

    @Override
    public List<Cita> consultarCitasPorPaciente(int idPaciente) {
        return citaRepository.buscarPorPaciente(idPaciente);
    }

    // ---------- métodos privados de apoyo ----------

    private Cita obtenerCita(int idCita) {
        return citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
    }

    private void validarFechaFutura(LocalDate fecha) {
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede agendar una cita en una fecha pasada");
        }
    }

    private void validarHora(String hora) {
        if (!Validaciones.esHoraValida(hora)) {
            throw new IllegalArgumentException("La hora debe tener formato HH:mm, entre 08:00 y 17:30, en punto o y media");
        }
    }

    private void validarHorarioLibre(int idProfesional, LocalDate fecha, String hora) {
        if (citaRepository.existeCitaActiva(idProfesional, fecha, hora)) {
            throw new IllegalStateException("El profesional ya tiene una cita en esa fecha y hora");
        }
    }
}
