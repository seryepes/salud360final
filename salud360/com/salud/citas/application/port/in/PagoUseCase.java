package com.salud.citas.application.port.in;

import com.salud.citas.domain.model.Pago;

import java.util.Optional;

public interface PagoUseCase {

    /** Procesa el pago de una cita a través de la pasarela. */
    Pago procesarPago(int idCita, double monto, String metodoPago);

    Optional<Pago> consultarPagoPorCita(int idCita);
}
