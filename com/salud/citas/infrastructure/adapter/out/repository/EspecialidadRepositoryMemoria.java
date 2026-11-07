package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.EspecialidadRepositoryPort;
import com.salud.citas.domain.model.Especialidad;

import java.util.ArrayList;
import java.util.List;

public class EspecialidadRepositoryMemoria implements EspecialidadRepositoryPort {

    private final List<Especialidad> especialidades = new ArrayList<>();

    public EspecialidadRepositoryMemoria() {
        // Datos de prueba: no hay pantalla para crear especialidades
        especialidades.add(crear(1, "Medicina General", "Consulta médica general y control de salud"));
        especialidades.add(crear(2, "Pediatría", "Atención médica para niños y adolescentes"));
        especialidades.add(crear(3, "Cardiología", "Diagnóstico y tratamiento del corazón"));
        especialidades.add(crear(4, "Odontología", "Salud y cuidado dental"));
    }

    private Especialidad crear(int id, String nombre, String descripcion) {
        Especialidad e = new Especialidad();
        e.setIdEspecialidad(id);
        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        return e;
    }

    @Override
    public List<Especialidad> listarTodas() {
        return new ArrayList<>(especialidades);
    }
}
