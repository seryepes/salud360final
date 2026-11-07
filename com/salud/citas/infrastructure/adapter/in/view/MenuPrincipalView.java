package com.salud.citas.infrastructure.adapter.in.view;

import com.salud.citas.domain.model.Usuario;

import java.util.Optional;

public class MenuPrincipalView {

    private final UsuarioView usuarioView;
    private final CitaView citaView;
    private final PagoView pagoView;
    private final LectorConsola lector;

    public MenuPrincipalView(UsuarioView usuarioView, CitaView citaView,
                             PagoView pagoView, LectorConsola lector) {
        this.usuarioView = usuarioView;
        this.citaView = citaView;
        this.pagoView = pagoView;
        this.lector = lector;
    }

    public void iniciar() {
        System.out.println("=====================================");
        System.out.println("   SISTEMA DE CITAS MÉDICAS");
        System.out.println("=====================================");

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Registrarse");
            System.out.println("2. Iniciar sesión");
            System.out.println("0. Salir");
            int opcion = lector.leerEntero("Opción: ");

            try {
                switch (opcion) {
                    case 1:
                        usuarioView.registrar();
                        break;
                    case 2: {
                        Optional<Usuario> usuario = usuarioView.iniciarSesion();
                        if (usuario.isPresent()) {
                            menuPaciente(usuario.get());
                        }
                        break;
                    }
                    case 0:
                        salir = true;
                        System.out.println("¡Gracias por usar el sistema!");
                        break;
                    default:
                        System.out.println("[!] Opción no válida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }
    }

    private void menuPaciente(Usuario usuario) {
        boolean cerrar = false;
        while (!cerrar) {
            System.out.println("\n--- MENÚ DEL PACIENTE: " + usuario.getNombre() + " ---");
            System.out.println("1. Agendar cita");
            System.out.println("2. Ver mis citas");
            System.out.println("3. Reprogramar cita");
            System.out.println("4. Pagar cita");
            System.out.println("5. Confirmar cita");
            System.out.println("6. Cancelar cita");
            System.out.println("7. Mi perfil");
            System.out.println("0. Cerrar sesión");
            int opcion = lector.leerEntero("Opción: ");

            try {
                switch (opcion) {
                    case 1:
                        citaView.agendar(usuario);
                        break;
                    case 2:
                        citaView.listar(usuario);
                        break;
                    case 3:
                        citaView.reprogramar(usuario);
                        break;
                    case 4:
                        pagoView.pagar(usuario);
                        break;
                    case 5:
                        citaView.confirmar(usuario);
                        break;
                    case 6:
                        citaView.cancelar(usuario);
                        break;
                    case 7:
                        usuarioView.gestionarPerfil(usuario);
                        break;
                    case 0:
                        usuarioView.cerrarSesion(usuario);
                        cerrar = true;
                        break;
                    default:
                        System.out.println("[!] Opción no válida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                // Las reglas de negocio de los services llegan aquí como mensajes
                System.out.println("[!] " + e.getMessage());
            }
        }
    }
}
