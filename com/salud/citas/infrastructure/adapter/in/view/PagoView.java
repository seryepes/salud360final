package com.salud.citas.infrastructure.adapter.in.view;

import com.salud.citas.application.port.in.PagoUseCase;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.EstadoPago;
import com.salud.citas.domain.model.Pago;
import com.salud.citas.domain.model.Usuario;

import java.util.Optional;

public class PagoView {

    // Valor fijo de una consulta (simplificación para el proyecto)
    private static final double TARIFA_CONSULTA = 80000;

    private final PagoUseCase pagoUseCase;
    private final SelectorCita selector;
    private final LectorConsola lector;

    public PagoView(PagoUseCase pagoUseCase, SelectorCita selector, LectorConsola lector) {
        this.pagoUseCase = pagoUseCase;
        this.selector = selector;
        this.lector = lector;
    }

    public void pagar(Usuario usuario) {
        Optional<Cita> elegida = selector.elegir(usuario, "PAGAR CITA");
        if (!elegida.isPresent()) {
            return;
        }

        System.out.println(String.format("Valor de la consulta: $%,.0f", TARIFA_CONSULTA));
        System.out.println("Método de pago: 1. Tarjeta  2. PSE  3. Efectivo");
        int opcion = 0;
        while (opcion < 1 || opcion > 3) {
            opcion = lector.leerEntero("Elige el método: ");
        }
        String metodo;
        if (opcion == 1) {
            metodo = "TARJETA";
        } else if (opcion == 2) {
            metodo = "PSE";
        } else {
            metodo = "EFECTIVO";
        }

        if (!lector.confirmar("¿Confirmas el pago con " + metodo + "? (s/n): ")) {
            System.out.println("Pago cancelado.");
            return;
        }

        Pago pago = pagoUseCase.procesarPago(elegida.get().getIdCita(), TARIFA_CONSULTA, metodo);
        if (pago.getEstadoPago() == EstadoPago.APROBADO) {
            System.out.println("[OK] Pago aprobado. Ya puedes confirmar tu cita.");
        } else {
            System.out.println("[!] Pago rechazado. Intenta de nuevo.");
        }
    }
}
