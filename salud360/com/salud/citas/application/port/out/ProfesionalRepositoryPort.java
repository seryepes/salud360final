package com.salud.citas.application.port.out;

import com.salud.citas.domain.model.Profesional;

import java.util.List;
import java.util.Optional;

public interface ProfesionalRepositoryPort {

    Optional<Profesional> buscarPorId(int idProfesional);

    List<Profesional> buscarPorEspecialidad(int idEspecialidad);
}
