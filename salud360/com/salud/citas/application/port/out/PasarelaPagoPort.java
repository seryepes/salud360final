package com.salud.citas.application.port.out;

import com.salud.citas.domain.model.EstadoPago;

public interface PasarelaPagoPort {

    /** Cobra el monto con el método indicado y devuelve APROBADO o RECHAZADO. */
    EstadoPago cobrar(double monto, String metodoPago);
}
