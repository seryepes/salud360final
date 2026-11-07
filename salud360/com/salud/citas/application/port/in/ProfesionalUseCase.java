package com.salud.citas.application.port.in;

import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.Especialidad;
import com.salud.citas.domain.model.Profesional;

import java.util.List;

public interface ProfesionalUseCase {

    List<Especialidad> listarEspecialidades();

    List<Profesional> listarProfesionalesPorEspecialidad(int idEspecialidad);

    List<Cita> consultarAgenda(int idProfesional);
}
