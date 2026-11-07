package com.salud.citas.util;

import com.salud.citas.domain.model.Cita;

public class FormatoHelper {

    private FormatoHelper() {
    }

    public static String resumenCita(Cita c) {
        String pago = (c.getPago() == null) ? "SIN PAGO" : c.getPago().getEstadoPago().toString();
        return String.format("[#%d] %s %s | %s %s | %s | %s | Estado: %s | Pago: %s",
                c.getIdCita(), c.getFecha(), c.getHora(),
                c.getProfesional().getNombre(), c.getProfesional().getApellido(),
                c.getMotivo(), c.getModalidad(), c.getEstado(), pago);
    }
}
