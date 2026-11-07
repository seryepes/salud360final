package com.salud.citas.util;

import java.time.LocalDate;

/** Reglas de validación reutilizables (la vista las usa para avisar rápido y los services para proteger el negocio). */
public class Validaciones {

    private Validaciones() {
    }

    public static boolean noVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static boolean esCorreoValido(String correo) {
        return correo != null && correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && contrasena.length() >= 6;
    }

    public static boolean esDocumentoValido(String documento) {
        return documento != null && documento.matches("\\d{6,12}");
    }

    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && telefono.matches("\\d{10}");
    }

    public static boolean esFechaNacimientoValida(LocalDate fecha) {
        return fecha != null
                && fecha.isBefore(LocalDate.now())
                && fecha.isAfter(LocalDate.now().minusYears(120));
    }

    /** Formato HH:mm, entre 08:00 y 17:30, en punto o y media. */
    public static boolean esHoraValida(String hora) {
        if (hora == null || !hora.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        int h = Integer.parseInt(hora.substring(0, 2));
        int m = Integer.parseInt(hora.substring(3, 5));
        return h >= 8 && h <= 17 && (m == 0 || m == 30);
    }
}
