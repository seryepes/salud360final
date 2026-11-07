package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.UsuarioRepositoryPort;
import com.salud.citas.domain.model.Paciente;
import com.salud.citas.domain.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepositoryMemoria implements UsuarioRepositoryPort {

    private final List<Usuario> usuarios = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario.getIdUsuario() == 0) {
            // Usuario nuevo: se le asigna id y se agrega a la lista
            usuario.setIdUsuario(siguienteId++);
            if (usuario.getPaciente() != null) {
                // Cada usuario tiene un solo paciente, así que comparten el mismo número
                usuario.getPaciente().setIdPaciente(usuario.getIdUsuario());
            }
            usuarios.add(usuario);
        } else {
            // Usuario existente: se reemplaza el que tenga el mismo id
            for (int i = 0; i < usuarios.size(); i++) {
                if (usuarios.get(i).getIdUsuario() == usuario.getIdUsuario()) {
                    usuarios.set(i, usuario);
                    break;
                }
            }
        }
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        for (Usuario u : usuarios) {
            if (u.getIdUsuario() == idUsuario) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        for (Usuario u : usuarios) {
            if (u.getCorreo().equalsIgnoreCase(correo)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return buscarPorCorreo(correo).isPresent();
    }

    @Override
    public Optional<Paciente> buscarPacientePorId(int idPaciente) {
        for (Usuario u : usuarios) {
            if (u.getPaciente() != null && u.getPaciente().getIdPaciente() == idPaciente) {
                return Optional.of(u.getPaciente());
            }
        }
        return Optional.empty();
    }
}
