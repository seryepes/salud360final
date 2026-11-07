package com.salud.citas.infrastructure.config;

import java.sql.SQLException;

/**
 * Error de acceso a la base de datos. Extiende IllegalStateException para que
 * las vistas lo muestren como un mensaje "[!]" en vez de cerrar el programa.
 */
public class RepositorioException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    public RepositorioException(String mensaje, SQLException causa) {
        super(mensaje + ": " + causa.getMessage(), causa);
    }
}
