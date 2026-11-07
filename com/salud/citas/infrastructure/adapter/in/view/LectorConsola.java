package com.salud.citas.infrastructure.adapter.in.view;

import com.salud.citas.util.Validaciones;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Único lugar que usa Scanner: lee datos de la consola y repite la pregunta si el dato no es válido. */
public class LectorConsola {

    private final Scanner scanner = new Scanner(System.in);

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (Validaciones.noVacio(texto)) {
                return texto;
            }
            System.out.println("[!] Este campo no puede estar vacío.");
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Escribe un número entero válido.");
            }
        }
    }

    public LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (formato AAAA-MM-DD): ");
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("[!] Fecha inválida. Ejemplo: 2026-11-25");
            }
        }
    }

    public String leerCorreo(String mensaje) {
        while (true) {
            String correo = leerTexto(mensaje);
            if (Validaciones.esCorreoValido(correo)) {
                return correo;
            }
            System.out.println("[!] Correo inválido. Ejemplo: nombre@correo.com");
        }
    }

    public String leerContrasena(String mensaje) {
        while (true) {
            String contrasena = leerTexto(mensaje);
            if (Validaciones.esContrasenaValida(contrasena)) {
                return contrasena;
            }
            System.out.println("[!] La contraseña debe tener al menos 6 caracteres.");
        }
    }

    public String leerDocumento(String mensaje) {
        while (true) {
            String documento = leerTexto(mensaje);
            if (Validaciones.esDocumentoValido(documento)) {
                return documento;
            }
            System.out.println("[!] El documento debe tener entre 6 y 12 dígitos, sin puntos ni espacios.");
        }
    }

    public String leerTelefono(String mensaje) {
        while (true) {
            String telefono = leerTexto(mensaje);
            if (Validaciones.esTelefonoValido(telefono)) {
                return telefono;
            }
            System.out.println("[!] El teléfono debe tener 10 dígitos.");
        }
    }

    public LocalDate leerFechaNacimiento(String mensaje) {
        while (true) {
            LocalDate fecha = leerFecha(mensaje);
            if (Validaciones.esFechaNacimientoValida(fecha)) {
                return fecha;
            }
            System.out.println("[!] La fecha de nacimiento debe ser una fecha pasada.");
        }
    }

    public String leerHora(String mensaje) {
        while (true) {
            String hora = leerTexto(mensaje + " (HH:mm, entre 08:00 y 17:30, en punto o y media): ");
            if (Validaciones.esHoraValida(hora)) {
                return hora;
            }
            System.out.println("[!] Hora inválida. Ejemplos: 08:00, 14:30");
        }
    }

    public boolean confirmar(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String r = scanner.nextLine().trim().toLowerCase();
            if (r.equals("s") || r.equals("si") || r.equals("sí")) {
                return true;
            }
            if (r.equals("n") || r.equals("no")) {
                return false;
            }
            System.out.println("[!] Responde s o n.");
        }
    }
}
