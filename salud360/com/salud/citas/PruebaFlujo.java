package com.salud.citas;

import com.salud.citas.application.port.in.CitaUseCase;
import com.salud.citas.application.port.in.PagoUseCase;
import com.salud.citas.application.port.in.ProfesionalUseCase;
import com.salud.citas.application.port.in.UsuarioUseCase;
import com.salud.citas.application.port.out.CitaRepositoryPort;
import com.salud.citas.application.port.out.EspecialidadRepositoryPort;
import com.salud.citas.application.port.out.PagoRepositoryPort;
import com.salud.citas.application.port.out.ProfesionalRepositoryPort;
import com.salud.citas.application.port.out.UsuarioRepositoryPort;
import com.salud.citas.application.service.CitaService;
import com.salud.citas.application.service.PagoService;
import com.salud.citas.application.service.ProfesionalService;
import com.salud.citas.application.service.UsuarioService;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.Especialidad;
import com.salud.citas.domain.model.Pago;
import com.salud.citas.domain.model.Profesional;
import com.salud.citas.domain.model.Usuario;
import com.salud.citas.infrastructure.adapter.out.pasarela.PasarelaPagoSimulada;
import com.salud.citas.infrastructure.adapter.out.repository.CitaRepositoryMemoria;
import com.salud.citas.infrastructure.adapter.out.repository.EspecialidadRepositoryMemoria;
import com.salud.citas.infrastructure.adapter.out.repository.PagoRepositoryMemoria;
import com.salud.citas.infrastructure.adapter.out.repository.ProfesionalRepositoryMemoria;
import com.salud.citas.infrastructure.adapter.out.repository.UsuarioRepositoryMemoria;

import java.time.LocalDate;

/**
 * PRUEBA AUTOMÁTICA: recorre todo el flujo sin menús (útil para verificar la lógica).
 * Se ejecuta aparte del programa real (Main).
 */
public class PruebaFlujo {

    public static void main(String[] args) {
        // 1. Repositories (puertos de salida -> implementaciones con ArrayList)
        UsuarioRepositoryPort usuarioRepo = new UsuarioRepositoryMemoria();
        CitaRepositoryPort citaRepo = new CitaRepositoryMemoria();
        PagoRepositoryPort pagoRepo = new PagoRepositoryMemoria();
        EspecialidadRepositoryPort especialidadRepo = new EspecialidadRepositoryMemoria();
        ProfesionalRepositoryPort profesionalRepo = new ProfesionalRepositoryMemoria(especialidadRepo);

        // 2. Services (puertos de entrada), con sus dependencias inyectadas
        UsuarioUseCase usuarios = new UsuarioService(usuarioRepo);
        CitaUseCase citas = new CitaService(citaRepo, usuarioRepo, profesionalRepo);
        PagoUseCase pagos = new PagoService(citaRepo, pagoRepo, new PasarelaPagoSimulada());
        ProfesionalUseCase profesionales = new ProfesionalService(profesionalRepo, especialidadRepo, citaRepo);

        // 3. Probar el flujo
        System.out.println("=== ESPECIALIDADES Y PROFESIONALES ===");
        for (Especialidad e : profesionales.listarEspecialidades()) {
            System.out.println(e.getIdEspecialidad() + ". " + e.getNombre());
            for (Profesional p : profesionales.listarProfesionalesPorEspecialidad(e.getIdEspecialidad())) {
                System.out.println("     - [" + p.getIdProfesional() + "] " + p.getNombre() + " " + p.getApellido());
            }
        }

        System.out.println("\n=== REGISTRO E INICIO DE SESIÓN ===");
        Usuario usuario = usuarios.registrarse("Ana", "Ruiz", "ana@mail.com", "123456",
                "3001234567", "1234567890", LocalDate.of(2000, 5, 20), "Calle 10 #20-30", "Sura");
        System.out.println("Registrada: " + usuario.getNombre() + " (id " + usuario.getIdUsuario() + ")");
        System.out.println("Login correcto: " + usuarios.iniciarSesion("ana@mail.com", "123456").isPresent());
        System.out.println("Login con clave mala: " + usuarios.iniciarSesion("ana@mail.com", "xxx").isPresent());

        int idPaciente = usuario.getPaciente().getIdPaciente();
        LocalDate fecha = LocalDate.now().plusDays(3);

        System.out.println("\n=== AGENDAR CITA ===");
        Cita cita = citas.agendarCita(idPaciente, 1, fecha, "10:00", "Control general", "Presencial");
        System.out.println("Cita " + cita.getIdCita() + " -> " + cita.getEstado());

        try {
            citas.agendarCita(idPaciente, 1, fecha, "10:00", "Otra", "Presencial");
        } catch (RuntimeException e) {
            System.out.println("Error esperado (mismo horario): " + e.getMessage());
        }

        System.out.println("\n=== CONFIRMAR SIN PAGAR ===");
        try {
            citas.confirmarCita(cita.getIdCita());
        } catch (RuntimeException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

        System.out.println("\n=== PAGO ===");
        Pago rechazado = pagos.procesarPago(cita.getIdCita(), 50000, "BITCOIN");
        System.out.println("Pago con método inválido: " + rechazado.getEstadoPago());
        Pago aprobado = pagos.procesarPago(cita.getIdCita(), 50000, "PSE");
        System.out.println("Pago con PSE: " + aprobado.getEstadoPago());

        System.out.println("\n=== REPROGRAMAR Y CONFIRMAR ===");
        cita = citas.reprogramarCita(cita.getIdCita(), fecha.plusDays(1), "11:00");
        System.out.println("Reprogramada: " + cita.getFecha() + " " + cita.getHora() + " -> " + cita.getEstado());
        cita = citas.confirmarCita(cita.getIdCita());
        System.out.println("Confirmada: " + cita.getEstado()
                + " | código " + cita.getConfirmacion().getCodigoConfirmacion());

        System.out.println("\n=== CANCELAR ===");
        cita = citas.cancelarCita(cita.getIdCita());
        System.out.println("Estado final: " + cita.getEstado());

        System.out.println("\nCitas de la paciente: " + citas.consultarCitasPorPaciente(idPaciente).size());
    }
}
