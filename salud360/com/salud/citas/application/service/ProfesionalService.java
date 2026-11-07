package com.salud.citas.application.service;

import com.salud.citas.application.port.in.ProfesionalUseCase;
import com.salud.citas.application.port.out.CitaRepositoryPort;
import com.salud.citas.application.port.out.EspecialidadRepositoryPort;
import com.salud.citas.application.port.out.ProfesionalRepositoryPort;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.Especialidad;
import com.salud.citas.domain.model.Profesional;

import java.util.List;

public class ProfesionalService implements ProfesionalUseCase {

    private final ProfesionalRepositoryPort profesionalRepository;
    private final EspecialidadRepositoryPort especialidadRepository;
    private final CitaRepositoryPort citaRepository;

    public ProfesionalService(ProfesionalRepositoryPort profesionalRepository,
                              EspecialidadRepositoryPort especialidadRepository,
                              CitaRepositoryPort citaRepository) {
        this.profesionalRepository = profesionalRepository;
        this.especialidadRepository = especialidadRepository;
        this.citaRepository = citaRepository;
    }

    @Override
    public List<Especialidad> listarEspecialidades() {
        return especialidadRepository.listarTodas();
    }

    @Override
    public List<Profesional> listarProfesionalesPorEspecialidad(int idEspecialidad) {
        return profesionalRepository.buscarPorEspecialidad(idEspecialidad);
    }

    @Override
    public List<Cita> consultarAgenda(int idProfesional) {
        return citaRepository.buscarPorProfesional(idProfesional);
    }
}
