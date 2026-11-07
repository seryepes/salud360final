package com.salud.citas.application.port.out;

import com.salud.citas.domain.model.Especialidad;

import java.util.List;

public interface EspecialidadRepositoryPort {

    List<Especialidad> listarTodas();
}
