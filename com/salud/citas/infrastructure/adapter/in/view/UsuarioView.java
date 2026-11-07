package com.salud.citas.infrastructure.adapter.in.view;

import com.salud.citas.application.port.in.UsuarioUseCase;
import com.salud.citas.domain.model.Paciente;
import com.salud.citas.domain.model.Usuario;

import java.time.LocalDate;
import java.util.Optional;

public class UsuarioView {

    private final UsuarioUseCase usuarioUseCase;
    private final LectorConsola lector;

    public UsuarioView(UsuarioUseCase usuarioUseCase, LectorConsola lector) {
        this.usuarioUseCase = usuarioUseCase;
        this.lector = lector;
    }

    public void registrar() {
        System.out.println("\n--- REGISTRO ---");
        String nombre = lector.leerTexto("Nombre: ");
        String apellido = lector.leerTexto("Apellido: ");
        String correo = lector.leerCorreo("Correo: ");
        String contrasena = lector.leerContrasena("Contraseña (mínimo 6 caracteres): ");
        String telefono = lector.leerTelefono("Teléfono (10 dígitos): ");
        String documento = lector.leerDocumento("Número de documento: ");
        LocalDate fechaNacimiento = lector.leerFechaNacimiento("Fecha de nacimiento");
        String direccion = lector.leerTexto("Dirección: ");
        String eps = lector.leerTexto("EPS: ");

        Usuario usuario = usuarioUseCase.registrarse(nombre, apellido, correo, contrasena,
                telefono, documento, fechaNacimiento, direccion, eps);
        System.out.println("[OK] Registro exitoso. Bienvenido(a), " + usuario.getNombre()
                + ". Ya puedes iniciar sesión.");
    }

    public Optional<Usuario> iniciarSesion() {
        System.out.println("\n--- INICIAR SESIÓN ---");
        String correo = lector.leerTexto("Correo: ");
        String contrasena = lector.leerTexto("Contraseña: ");

        Optional<Usuario> usuario = usuarioUseCase.iniciarSesion(correo, contrasena);
        if (!usuario.isPresent()) {
            System.out.println("[!] Correo o contraseña incorrectos.");
        }
        return usuario;
    }

    public void cerrarSesion(Usuario usuario) {
        usuarioUseCase.cerrarSesion(usuario.getIdUsuario());
        System.out.println("[OK] Sesión cerrada. ¡Hasta pronto, " + usuario.getNombre() + "!");
    }

    public void gestionarPerfil(Usuario usuario) {
        Paciente p = usuario.getPaciente();
        System.out.println("\n--- MI PERFIL ---");
        System.out.println("Nombre:           " + usuario.getNombre() + " " + usuario.getApellido());
        System.out.println("Correo:           " + usuario.getCorreo());
        System.out.println("Teléfono:         " + usuario.getTelefono());
        System.out.println("Documento:        " + p.getDocumento());
        System.out.println("Fecha nacimiento: " + p.getFechaNacimiento());
        System.out.println("Dirección:        " + p.getDireccion());
        System.out.println("EPS:              " + p.getEps());

        if (lector.confirmar("\n¿Deseas actualizar tus datos? (s/n): ")) {
            String nombre = lector.leerTexto("Nombre: ");
            String apellido = lector.leerTexto("Apellido: ");
            String telefono = lector.leerTelefono("Teléfono (10 dígitos): ");
            String direccion = lector.leerTexto("Dirección: ");
            usuarioUseCase.actualizarPerfil(usuario.getIdUsuario(), nombre, apellido, telefono, direccion);
            System.out.println("[OK] Perfil actualizado.");
        }
    }
}
