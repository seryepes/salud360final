package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.CitaRepositoryPort;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.EstadoCita;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CitaRepositoryMemoria implements CitaRepositoryPort {

    private final List<Cita> citas = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public Cita guardar(Cita cita) {
        if (cita.getIdCita() == 0) {
            cita.setIdCita(siguienteId++);
            citas.add(cita);
        } else {
            for (int i = 0; i < citas.size(); i++) {
                if (citas.get(i).getIdCita() == cita.getIdCita()) {
                    citas.set(i, cita);
                    break;
                }
            }
        }
        return cita;
    }

    @Override
    public Optional<Cita> buscarPorId(int idCita) {
        for (Cita c : citas) {
            if (c.getIdCita() == idCita) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Cita> buscarPorPaciente(int idPaciente) {
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas) {
            if (c.getPaciente().getIdPaciente() == idPaciente) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public List<Cita> buscarPorProfesional(int idProfesional) {
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas) {
            if (c.getProfesional().getIdProfesional() == idProfesional) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public boolean existeCitaActiva(int idProfesional, LocalDate fecha, String hora) {
        for (Cita c : citas) {
            boolean mismoProfesional = c.getProfesional().getIdProfesional() == idProfesional;
            boolean mismoHorario = c.getFecha().equals(fecha) && c.getHora().equals(hora);
            boolean activa = c.getEstado() != EstadoCita.CANCELADA;
            if (mismoProfesional && mismoHorario && activa) {
                return true;
            }
        }
        return false;
    }
}
