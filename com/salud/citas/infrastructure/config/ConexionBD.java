package com.salud.citas.infrastructure.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Datos de conexión a la base de datos de XAMPP (MariaDB/MySQL).
 *
 * Valores por defecto de XAMPP: puerto 3306, usuario "root" y SIN contraseña.
 * Si en tu equipo cambiaste algo, modifica las constantes de abajo
 * (o pásalas al ejecutar: -Ddb.url=... -Ddb.user=... -Ddb.password=...).
 */
public final class ConexionBD {

    private static final String URL = System.getProperty("db.url",
            "jdbc:mariadb://localhost:3306/salud360");
    private static final String USUARIO = System.getProperty("db.user", "root");
    private static final String CONTRASENA = System.getProperty("db.password", "");

    private ConexionBD() {
    }

    /** Abre una conexión nueva. Quien la pida debe cerrarla (try-with-resources). */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    /**
     * Prueba la conexión al arrancar el programa.
     * Devuelve null si todo está bien o un mensaje explicando qué revisar.
     */
    public static String probar() {
        try (Connection c = obtener()) {
            return c.isValid(3) ? null : "La conexión se abrió pero no responde.";
        } catch (SQLException e) {
            return "No se pudo conectar a la base de datos (" + URL + ").\n"
                    + "  Revisa que: 1) XAMPP tenga MySQL en verde (Start),\n"
                    + "              2) hayas importado database/salud360.sql en phpMyAdmin,\n"
                    + "              3) el usuario/contraseña de ConexionBD sean los de tu XAMPP.\n"
                    + "  Detalle técnico: " + e.getMessage();
        }
    }
}
