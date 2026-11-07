package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.PagoRepositoryPort;
import com.salud.citas.domain.model.Pago;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PagoRepositoryMemoria implements PagoRepositoryPort {

    private final List<Pago> pagos = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public Pago guardar(Pago pago) {
        if (pago.getIdPago() == 0) {
            pago.setIdPago(siguienteId++);
            pagos.add(pago);
        } else {
            for (int i = 0; i < pagos.size(); i++) {
                if (pagos.get(i).getIdPago() == pago.getIdPago()) {
                    pagos.set(i, pago);
                    break;
                }
            }
        }
        return pago;
    }

    @Override
    public Optional<Pago> buscarPorId(int idPago) {
        for (Pago p : pagos) {
            if (p.getIdPago() == idPago) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }
}
