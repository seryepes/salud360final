package com.salud.citas.application.port.in;

import com.salud.citas.domain.model.Usuario;

import java.time.LocalDate;
import java.util.Optional;

public interface UsuarioUseCase {

    /** Crea un usuario con su paciente asociado. Lanza IllegalArgumentException si los datos no son válidos. */
    Usuario registrarse(String nombre, String apellido, String correo, String contrasena,
                        String telefono, String documento, LocalDate fechaNacimiento,
                        String direccion, String eps);

    /** Devuelve el usuario si las credenciales son correctas; vacío si no. */
    Optional<Usuario> iniciarSesion(String correo, String contrasena);

    void cerrarSesion(int idUsuario);

    Usuario actualizarPerfil(int idUsuario, String nombre, String apellido,
                             String telefono, String direccion);
}
