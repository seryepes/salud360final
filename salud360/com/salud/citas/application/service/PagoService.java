package com.salud.citas.application.service;

import com.salud.citas.application.port.in.PagoUseCase;
import com.salud.citas.application.port.out.CitaRepositoryPort;
import com.salud.citas.application.port.out.PagoRepositoryPort;
import com.salud.citas.application.port.out.PasarelaPagoPort;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.EstadoCita;
import com.salud.citas.domain.model.EstadoPago;
import com.salud.citas.domain.model.Pago;

import java.time.LocalDateTime;
import java.util.Optional;

public class PagoService implements PagoUseCase {

    private final CitaRepositoryPort citaRepository;
    private final PagoRepositoryPort pagoRepository;
    private final PasarelaPagoPort pasarelaPago;

    public PagoService(CitaRepositoryPort citaRepository,
                       PagoRepositoryPort pagoRepository,
                       PasarelaPagoPort pasarelaPago) {
        this.citaRepository = citaRepository;
        this.pagoRepository = pagoRepository;
        this.pasarelaPago = pasarelaPago;
    }

    @Override
    public Pago procesarPago(int idCita, double monto, String metodoPago) {
        Cita cita = citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalStateException("No se puede pagar una cita cancelada");
        }
        if (cita.getPago() != null && cita.getPago().getEstadoPago() == EstadoPago.APROBADO) {
            throw new IllegalStateException("Esta cita ya fue pagada");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }

        // La pasarela decide si el cobro se aprueba o se rechaza
        EstadoPago resultado = pasarelaPago.cobrar(monto, metodoPago);

        Pago pago = new Pago();
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());
        pago.setEstadoPago(resultado);
        pagoRepository.guardar(pago);

        cita.setPago(pago);
        citaRepository.guardar(cita);
        return pago;
    }

    @Override
    public Optional<Pago> consultarPagoPorCita(int idCita) {
        Optional<Cita> cita = citaRepository.buscarPorId(idCita);
        if (cita.isPresent() && cita.get().getPago() != null) {
            return Optional.of(cita.get().getPago());
        }
        return Optional.empty();
    }
}
