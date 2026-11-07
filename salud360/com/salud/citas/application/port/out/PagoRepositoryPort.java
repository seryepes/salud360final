package com.salud.citas.application.port.out;

import com.salud.citas.domain.model.Pago;

import java.util.Optional;

public interface PagoRepositoryPort {

    /** Guarda el pago y le asigna un id si es nuevo. */
    Pago guardar(Pago pago);

    Optional<Pago> buscarPorId(int idPago);
}
