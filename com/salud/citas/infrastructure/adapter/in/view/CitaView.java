package com.salud.citas.infrastructure.adapter.in.view;

import com.salud.citas.application.port.in.CitaUseCase;
import com.salud.citas.application.port.in.ProfesionalUseCase;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.Especialidad;
import com.salud.citas.domain.model.Profesional;
import com.salud.citas.domain.model.Usuario;
import com.salud.citas.util.FormatoHelper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CitaView {

    private final CitaUseCase citaUseCase;
    private final ProfesionalUseCase profesionalUseCase;
    private final SelectorCita selector;
    private final LectorConsola lector;

    public CitaView(CitaUseCase citaUseCase, ProfesionalUseCase profesionalUseCase,
                    SelectorCita selector, LectorConsola lector) {
        this.citaUseCase = citaUseCase;
        this.profesionalUseCase = profesionalUseCase;
        this.selector = selector;
        this.lector = lector;
    }

    public void agendar(Usuario usuario) {
        System.out.println("\n--- AGENDAR CITA ---");

        // 1. Especialidad
        List<Especialidad> especialidades = profesionalUseCase.listarEspecialidades();
        System.out.println("Especialidades disponibles:");
        for (Especialidad e : especialidades) {
            System.out.println("  " + e.getIdEspecialidad() + ". " + e.getNombre());
        }
        int idEspecialidad = 0;
        while (idEspecialidad == 0) {
            int id = lector.leerEntero("Elige la especialidad (0 para volver): ");
            if (id == 0) {
                return;
            }
            for (Especialidad e : especialidades) {
                if (e.getIdEspecialidad() == id) {
                    idEspecialidad = id;
                }
            }
            if (idEspecialidad == 0) {
                System.out.println("[!] Esa especialidad no existe.");
            }
        }

        // 2. Profesional
        List<Profesional> profesionales = profesionalUseCase.listarProfesionalesPorEspecialidad(idEspecialidad);
        if (profesionales.isEmpty()) {
            System.out.println("No hay profesionales para esa especialidad.");
            return;
        }
        System.out.println("Profesionales disponibles:");
        for (Profesional p : profesionales) {
            System.out.println("  " + p.getIdProfesional() + ". " + p.getNombre() + " " + p.getApellido());
        }
        int idProfesional = 0;
        while (idProfesional == 0) {
            int id = lector.leerEntero("Elige el profesional (0 para volver): ");
            if (id == 0) {
                return;
            }
            for (Profesional p : profesionales) {
                if (p.getIdProfesional() == id) {
                    idProfesional = id;
                }
            }
            if (idProfesional == 0) {
                System.out.println("[!] Ese profesional no está en la lista.");
            }
        }

        // 3. Datos de la cita
        LocalDate fecha = lector.leerFecha("Fecha de la cita");
        String hora = lector.leerHora("Hora");
        String motivo = lector.leerTexto("Motivo de la consulta: ");
        System.out.println("Modalidad: 1. Presencial  2. Virtual");
        int opcion = 0;
        while (opcion != 1 && opcion != 2) {
            opcion = lector.leerEntero("Elige la modalidad: ");
        }
        String modalidad = (opcion == 1) ? "Presencial" : "Virtual";

        Cita cita = citaUseCase.agendarCita(usuario.getPaciente().getIdPaciente(),
                idProfesional, fecha, hora, motivo, modalidad);
        System.out.println("[OK] Cita agendada:");
        System.out.println(FormatoHelper.resumenCita(cita));
        System.out.println("Recuerda: debes pagarla para poder confirmarla.");
    }

    public void listar(Usuario usuario) {
        List<Cita> citas = citaUseCase.consultarCitasPorPaciente(usuario.getPaciente().getIdPaciente());
        System.out.println("\n--- MIS CITAS ---");
        if (citas.isEmpty()) {
            System.out.println("No tienes citas registradas.");
            return;
        }
        for (Cita c : citas) {
            System.out.println(FormatoHelper.resumenCita(c));
            if (c.getConfirmacion() != null) {
                System.out.println("     Código de confirmación: " + c.getConfirmacion().getCodigoConfirmacion());
            }
        }
    }

    public void reprogramar(Usuario usuario) {
        Optional<Cita> elegida = selector.elegir(usuario, "REPROGRAMAR CITA");
        if (!elegida.isPresent()) {
            return;
        }
        LocalDate nuevaFecha = lector.leerFecha("Nueva fecha");
        String nuevaHora = lector.leerHora("Nueva hora");

        Cita cita = citaUseCase.reprogramarCita(elegida.get().getIdCita(), nuevaFecha, nuevaHora);
        System.out.println("[OK] Cita reprogramada:");
        System.out.println(FormatoHelper.resumenCita(cita));
    }

    public void confirmar(Usuario usuario) {
        Optional<Cita> elegida = selector.elegir(usuario, "CONFIRMAR CITA");
        if (!elegida.isPresent()) {
            return;
        }
        Cita cita = citaUseCase.confirmarCita(elegida.get().getIdCita());
        System.out.println("[OK] Cita confirmada. Código: " + cita.getConfirmacion().getCodigoConfirmacion());
    }

    public void cancelar(Usuario usuario) {
        Optional<Cita> elegida = selector.elegir(usuario, "CANCELAR CITA");
        if (!elegida.isPresent()) {
            return;
        }
        if (!lector.confirmar("¿Seguro que deseas cancelar esta cita? (s/n): ")) {
            System.out.println("Operación cancelada, la cita sigue igual.");
            return;
        }
        citaUseCase.cancelarCita(elegida.get().getIdCita());
        System.out.println("[OK] Cita cancelada.");
    }
}
