package com.salud.citas.infrastructure.adapter.out.pasarela;

import com.salud.citas.application.port.out.PasarelaPagoPort;
import com.salud.citas.domain.model.EstadoPago;

/**
 * Simula una pasarela de pago real (PSE, tarjeta, etc.).
 * Aprueba si el monto es positivo y el método es válido.
 */
public class PasarelaPagoSimulada implements PasarelaPagoPort {

    @Override
    public EstadoPago cobrar(double monto, String metodoPago) {
        if (monto <= 0 || metodoPago == null) {
            return EstadoPago.RECHAZADO;
        }
        switch (metodoPago.toUpperCase()) {
            case "TARJETA":
            case "PSE":
            case "EFECTIVO":
                return EstadoPago.APROBADO;
            default:
                return EstadoPago.RECHAZADO;
        }
    }
}
