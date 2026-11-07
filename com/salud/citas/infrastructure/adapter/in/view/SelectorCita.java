package com.salud.citas.infrastructure.adapter.in.view;

import com.salud.citas.application.port.in.CitaUseCase;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.Usuario;
import com.salud.citas.util.FormatoHelper;

import java.util.List;
import java.util.Optional;

/** Muestra las citas del usuario y le pide que elija una (solo puede elegir las suyas). */
public class SelectorCita {

    private final CitaUseCase citaUseCase;
    private final LectorConsola lector;

    public SelectorCita(CitaUseCase citaUseCase, LectorConsola lector) {
        this.citaUseCase = citaUseCase;
        this.lector = lector;
    }

    public Optional<Cita> elegir(Usuario usuario, String titulo) {
        List<Cita> citas = citaUseCase.consultarCitasPorPaciente(usuario.getPaciente().getIdPaciente());
        if (citas.isEmpty()) {
            System.out.println("No tienes citas registradas.");
            return Optional.empty();
        }

        System.out.println("\n--- " + titulo + " ---");
        for (Cita c : citas) {
            System.out.println(FormatoHelper.resumenCita(c));
        }

        while (true) {
            int id = lector.leerEntero("Número de la cita (0 para volver): ");
            if (id == 0) {
                return Optional.empty();
            }
            for (Cita c : citas) {
                if (c.getIdCita() == id) {
                    return Optional.of(c);
                }
            }
            System.out.println("[!] Esa cita no existe o no es tuya.");
        }
    }
}
