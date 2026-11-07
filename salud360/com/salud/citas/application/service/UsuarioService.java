package com.salud.citas.application.service;

import com.salud.citas.application.port.in.UsuarioUseCase;
import com.salud.citas.application.port.out.UsuarioRepositoryPort;
import com.salud.citas.domain.model.Paciente;
import com.salud.citas.domain.model.Usuario;
import com.salud.citas.util.Validaciones;

import java.time.LocalDate;
import java.util.Optional;

public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public UsuarioService(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario registrarse(String nombre, String apellido, String correo, String contrasena,
                               String telefono, String documento, LocalDate fechaNacimiento,
                               String direccion, String eps) {
        validarDatosRegistro(nombre, apellido, correo, contrasena, telefono, documento, fechaNacimiento);

        if (usuarioRepository.existePorCorreo(correo)) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese correo");
        }

        // El service crea los objetos del dominio con los datos que llegan de la vista
        Paciente paciente = new Paciente();
        paciente.setDocumento(documento);
        paciente.setFechaNacimiento(fechaNacimiento);
        paciente.setDireccion(direccion);
        paciente.setEps(eps);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setTelefono(telefono);
        usuario.setPaciente(paciente);

        return usuarioRepository.guardar(usuario);
    }

    @Override
    public Optional<Usuario> iniciarSesion(String correo, String contrasena) {
        Optional<Usuario> encontrado = usuarioRepository.buscarPorCorreo(correo);
        if (encontrado.isPresent() && encontrado.get().getContrasena().equals(contrasena)) {
            return encontrado;
        }
        return Optional.empty();
    }

    @Override
    public void cerrarSesion(int idUsuario) {
        usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        // En consola no hay sesión guardada en el servidor: la vista simplemente "olvida" al usuario.
    }

    @Override
    public Usuario actualizarPerfil(int idUsuario, String nombre, String apellido,
                                    String telefono, String direccion) {
        Usuario usuario = usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        if (usuario.getPaciente() != null) {
            usuario.getPaciente().setDireccion(direccion);
        }
        return usuarioRepository.guardar(usuario);
    }

    private void validarDatosRegistro(String nombre, String apellido, String correo, String contrasena,
                                      String telefono, String documento, LocalDate fechaNacimiento) {
        if (!Validaciones.noVacio(nombre) || !Validaciones.noVacio(apellido)) {
            throw new IllegalArgumentException("El nombre y el apellido son obligatorios");
        }
        if (!Validaciones.esCorreoValido(correo)) {
            throw new IllegalArgumentException("El correo no tiene un formato válido");
        }
        if (!Validaciones.esContrasenaValida(contrasena)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (!Validaciones.esTelefonoValido(telefono)) {
            throw new IllegalArgumentException("El teléfono debe tener 10 dígitos");
        }
        if (!Validaciones.esDocumentoValido(documento)) {
            throw new IllegalArgumentException("El documento debe tener entre 6 y 12 dígitos");
        }
        if (!Validaciones.esFechaNacimientoValida(fechaNacimiento)) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida");
        }
    }
}
