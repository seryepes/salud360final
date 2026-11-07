package com.salud.citas;

import com.salud.citas.application.port.in.CitaUseCase;
import com.salud.citas.application.port.in.PagoUseCase;
import com.salud.citas.application.port.in.ProfesionalUseCase;
import com.salud.citas.application.port.in.UsuarioUseCase;
import com.salud.citas.application.port.out.CitaRepositoryPort;
import com.salud.citas.application.port.out.EspecialidadRepositoryPort;
import com.salud.citas.application.port.out.PagoRepositoryPort;
import com.salud.citas.application.port.out.PasarelaPagoPort;
import com.salud.citas.application.port.out.ProfesionalRepositoryPort;
import com.salud.citas.application.port.out.UsuarioRepositoryPort;
import com.salud.citas.application.service.CitaService;
import com.salud.citas.application.service.PagoService;
import com.salud.citas.application.service.ProfesionalService;
import com.salud.citas.application.service.UsuarioService;
import com.salud.citas.infrastructure.adapter.in.view.CitaView;
import com.salud.citas.infrastructure.adapter.in.view.LectorConsola;
import com.salud.citas.infrastructure.adapter.in.view.MenuPrincipalView;
import com.salud.citas.infrastructure.adapter.in.view.PagoView;
import com.salud.citas.infrastructure.adapter.in.view.SelectorCita;
import com.salud.citas.infrastructure.adapter.in.view.UsuarioView;
import com.salud.citas.infrastructure.adapter.out.pasarela.PasarelaPagoSimulada;
import com.salud.citas.infrastructure.adapter.out.repository.CitaRepositoryJdbc;
import com.salud.citas.infrastructure.adapter.out.repository.EspecialidadRepositoryJdbc;
import com.salud.citas.infrastructure.adapter.out.repository.PagoRepositoryJdbc;
import com.salud.citas.infrastructure.adapter.out.repository.ProfesionalRepositoryJdbc;
import com.salud.citas.infrastructure.adapter.out.repository.UsuarioRepositoryJdbc;
import com.salud.citas.infrastructure.config.ConexionBD;

/**
 * Punto de entrada. Aquí se "arma" todo: se crean las implementaciones,
 * se inyectan en los services (por constructor) y los services en las vistas.
 */
public class Main {

    public static void main(String[] args) {
        // 0. Verificar la conexión con la base de datos (XAMPP -> MySQL)
        String problema = ConexionBD.probar();
        if (problema != null) {
            System.out.println("[X] " + problema);
            return;
        }

        // 1. Puertos de salida -> adaptadores (repositories que guardan en MySQL y pasarela simulada)
        UsuarioRepositoryPort usuarioRepo = new UsuarioRepositoryJdbc();
        EspecialidadRepositoryPort especialidadRepo = new EspecialidadRepositoryJdbc();
        ProfesionalRepositoryPort profesionalRepo = new ProfesionalRepositoryJdbc();
        PagoRepositoryPort pagoRepo = new PagoRepositoryJdbc();
        CitaRepositoryPort citaRepo = new CitaRepositoryJdbc(usuarioRepo, profesionalRepo, pagoRepo);
        PasarelaPagoPort pasarela = new PasarelaPagoSimulada();

        // 2. Puertos de entrada -> services (reciben solo interfaces)
        UsuarioUseCase usuarios = new UsuarioService(usuarioRepo);
        CitaUseCase citas = new CitaService(citaRepo, usuarioRepo, profesionalRepo);
        PagoUseCase pagos = new PagoService(citaRepo, pagoRepo, pasarela);
        ProfesionalUseCase profesionales = new ProfesionalService(profesionalRepo, especialidadRepo, citaRepo);

        // 3. Vistas (reciben los casos de uso, no los services concretos)
        LectorConsola lector = new LectorConsola();
        SelectorCita selector = new SelectorCita(citas, lector);
        UsuarioView usuarioView = new UsuarioView(usuarios, lector);
        CitaView citaView = new CitaView(citas, profesionales, selector, lector);
        PagoView pagoView = new PagoView(pagos, selector, lector);

        // 4. Arrancar el menú
        new MenuPrincipalView(usuarioView, citaView, pagoView, lector).iniciar();
    }
}
