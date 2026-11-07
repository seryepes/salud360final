package com.salud.citas.application.port.out;

import com.salud.citas.domain.model.Paciente;
import com.salud.citas.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    /** Guarda el usuario y le asigna un id si es nuevo. Devuelve el usuario guardado. */
    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(int idUsuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    boolean existePorCorreo(String correo);

    Optional<Paciente> buscarPacientePorId(int idPaciente);
}
