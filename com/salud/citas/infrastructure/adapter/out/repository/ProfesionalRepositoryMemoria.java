package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.EspecialidadRepositoryPort;
import com.salud.citas.application.port.out.ProfesionalRepositoryPort;
import com.salud.citas.domain.model.Especialidad;
import com.salud.citas.domain.model.Profesional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfesionalRepositoryMemoria implements ProfesionalRepositoryPort {

    private final List<Profesional> profesionales = new ArrayList<>();

    // Recibe el puerto (la interfaz), no la clase concreta
    public ProfesionalRepositoryMemoria(EspecialidadRepositoryPort especialidadRepository) {
        List<Especialidad> esp = especialidadRepository.listarTodas();
        Especialidad general = esp.get(0);
        Especialidad pediatria = esp.get(1);
        Especialidad cardiologia = esp.get(2);
        Especialidad odontologia = esp.get(3);

        // Datos de prueba
        profesionales.add(crear(1, "Laura", "Gómez", "laura.gomez@clinica.com", "3001112233", "1010101010",
                List.of(general)));
        profesionales.add(crear(2, "Carlos", "Ramírez", "carlos.ramirez@clinica.com", "3002223344", "2020202020",
                List.of(pediatria)));
        profesionales.add(crear(3, "Andrea", "Torres", "andrea.torres@clinica.com", "3003334455", "3030303030",
                List.of(cardiologia)));
        profesionales.add(crear(4, "Julián", "Herrera", "julian.herrera@clinica.com", "3004445566", "4040404040",
                List.of(odontologia, general)));
    }

    private Profesional crear(int id, String nombre, String apellido, String correo,
                              String telefono, String documento, List<Especialidad> especialidades) {
        Profesional p = new Profesional();
        p.setIdProfesional(id);
        p.setNombre(nombre);
        p.setApellido(apellido);
        p.setCorreo(correo);
        p.setTelefono(telefono);
        p.setDocumento(documento);
        p.setEspecialidades(especialidades);
        return p;
    }

    @Override
    public Optional<Profesional> buscarPorId(int idProfesional) {
        for (Profesional p : profesionales) {
            if (p.getIdProfesional() == idProfesional) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Profesional> buscarPorEspecialidad(int idEspecialidad) {
        List<Profesional> resultado = new ArrayList<>();
        for (Profesional p : profesionales) {
            for (Especialidad e : p.getEspecialidades()) {
                if (e.getIdEspecialidad() == idEspecialidad) {
                    resultado.add(p);
                    break;
                }
            }
        }
        return resultado;
    }
}
