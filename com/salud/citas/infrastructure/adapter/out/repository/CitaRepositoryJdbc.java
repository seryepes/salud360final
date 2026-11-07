package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.CitaRepositoryPort;
import com.salud.citas.application.port.out.PagoRepositoryPort;
import com.salud.citas.application.port.out.ProfesionalRepositoryPort;
import com.salud.citas.application.port.out.UsuarioRepositoryPort;
import com.salud.citas.domain.model.Cita;
import com.salud.citas.domain.model.ConfirmacionCita;
import com.salud.citas.domain.model.EstadoCita;
import com.salud.citas.domain.model.Notificacion;
import com.salud.citas.infrastructure.config.ConexionBD;
import com.salud.citas.infrastructure.config.RepositorioException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Guarda las citas en MySQL. Una cita une varias cosas (paciente, profesional,
 * pago y confirmación), así que para armarla reutiliza los otros puertos.
 */
public class CitaRepositoryJdbc implements CitaRepositoryPort {

    private static final String SELECT_CITA =
            "SELECT id_cita, fecha, hora, estado, motivo, modalidad, id_paciente, id_profesional, id_pago FROM cita ";

    private final UsuarioRepositoryPort usuarioRepository;
    private final ProfesionalRepositoryPort profesionalRepository;
    private final PagoRepositoryPort pagoRepository;

    public CitaRepositoryJdbc(UsuarioRepositoryPort usuarioRepository,
                              ProfesionalRepositoryPort profesionalRepository,
                              PagoRepositoryPort pagoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.profesionalRepository = profesionalRepository;
        this.pagoRepository = pagoRepository;
    }

    // ---------------------------------------------------------------- guardar

    @Override
    public Cita guardar(Cita cita) {
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false); // cita + confirmación + notificaciones: todo o nada
            try {
                if (cita.getIdCita() == 0) {
                    insertar(con, cita);
                } else {
                    actualizar(con, cita);
                }
                if (cita.getConfirmacion() != null) {
                    guardarConfirmacion(con, cita);
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al guardar la cita", e);
        }
        return cita;
    }

    private void insertar(Connection con, Cita c) throws SQLException {
        String sql = "INSERT INTO cita (fecha, hora, estado, motivo, modalidad, id_paciente, id_profesional, id_pago) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            llenar(ps, c);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setIdCita(keys.getInt(1));
                }
            }
        }
    }

    private void actualizar(Connection con, Cita c) throws SQLException {
        String sql = "UPDATE cita SET fecha = ?, hora = ?, estado = ?, motivo = ?, modalidad = ?, "
                + "id_paciente = ?, id_profesional = ?, id_pago = ? WHERE id_cita = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            llenar(ps, c);
            ps.setInt(9, c.getIdCita());
            ps.executeUpdate();
        }
    }

    private void llenar(PreparedStatement ps, Cita c) throws SQLException {
        ps.setDate(1, Date.valueOf(c.getFecha()));
        ps.setString(2, c.getHora());
        ps.setString(3, c.getEstado().name());
        ps.setString(4, c.getMotivo());
        ps.setString(5, c.getModalidad());
        ps.setInt(6, c.getPaciente().getIdPaciente());
        ps.setInt(7, c.getProfesional().getIdProfesional());
        // El pago es opcional: una cita recién agendada todavía no tiene pago
        if (c.getPago() != null && c.getPago().getIdPago() != 0) {
            ps.setInt(8, c.getPago().getIdPago());
        } else {
            ps.setNull(8, Types.INTEGER);
        }
    }

    /** Crea o actualiza la confirmación de la cita (máximo una por cita) y reemplaza sus notificaciones. */
    private void guardarConfirmacion(Connection con, Cita cita) throws SQLException {
        ConfirmacionCita conf = cita.getConfirmacion();

        Integer idExistente = null;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_confirmacion FROM confirmacion_cita WHERE id_cita = ?")) {
            ps.setInt(1, cita.getIdCita());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idExistente = rs.getInt(1);
                }
            }
        }

        if (idExistente == null) {
            String sql = "INSERT INTO confirmacion_cita (id_cita, fecha_confirmacion, codigo_confirmacion, estado) "
                    + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, cita.getIdCita());
                ps.setTimestamp(2, Timestamp.valueOf(conf.getFechaConfirmacion()));
                ps.setString(3, conf.getCodigoConfirmacion());
                ps.setString(4, conf.getEstado());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        conf.setIdConfirmacion(keys.getInt(1));
                    }
                }
            }
        } else {
            String sql = "UPDATE confirmacion_cita SET fecha_confirmacion = ?, codigo_confirmacion = ?, estado = ? "
                    + "WHERE id_confirmacion = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(conf.getFechaConfirmacion()));
                ps.setString(2, conf.getCodigoConfirmacion());
                ps.setString(3, conf.getEstado());
                ps.setInt(4, idExistente);
                ps.executeUpdate();
            }
            conf.setIdConfirmacion(idExistente);
        }

        // Notificaciones: se reemplaza el conjunto completo por el que trae el objeto
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM notificacion WHERE id_confirmacion = ?")) {
            ps.setInt(1, conf.getIdConfirmacion());
            ps.executeUpdate();
        }
        if (conf.getNotificaciones() != null) {
            String sql = "INSERT INTO notificacion (id_confirmacion, mensaje, fecha_envio, tipo, estado) "
                    + "VALUES (?, ?, ?, ?, ?)";
            for (Notificacion n : conf.getNotificaciones()) {
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, conf.getIdConfirmacion());
                    ps.setString(2, n.getMensaje());
                    ps.setTimestamp(3, Timestamp.valueOf(n.getFechaEnvio()));
                    ps.setString(4, n.getTipo());
                    ps.setString(5, n.getEstado());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            n.setIdNotificacion(keys.getInt(1));
                        }
                    }
                }
            }
        }
    }

    // --------------------------------------------------------------- consultas

    @Override
    public Optional<Cita> buscarPorId(int idCita) {
        List<Cita> citas = consultar(SELECT_CITA + "WHERE id_cita = ?", idCita);
        return citas.isEmpty() ? Optional.empty() : Optional.of(citas.get(0));
    }

    @Override
    public List<Cita> buscarPorPaciente(int idPaciente) {
        return consultar(SELECT_CITA + "WHERE id_paciente = ? ORDER BY id_cita", idPaciente);
    }

    @Override
    public List<Cita> buscarPorProfesional(int idProfesional) {
        return consultar(SELECT_CITA + "WHERE id_profesional = ? ORDER BY id_cita", idProfesional);
    }

    @Override
    public boolean existeCitaActiva(int idProfesional, LocalDate fecha, String hora) {
        String sql = "SELECT 1 FROM cita WHERE id_profesional = ? AND fecha = ? AND hora = ? "
                + "AND estado <> 'CANCELADA' LIMIT 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProfesional);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setString(3, hora);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al verificar el horario", e);
        }
    }

    // ------------------------------------------------------------------ apoyo

    private List<Cita> consultar(String sql, int parametro) {
        List<Cita> resultado = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(con, rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al consultar las citas", e);
        }
        return resultado;
    }

    private Cita mapear(Connection con, ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        c.setFecha(rs.getDate("fecha").toLocalDate());
        c.setHora(rs.getString("hora"));
        c.setEstado(EstadoCita.valueOf(rs.getString("estado")));
        c.setMotivo(rs.getString("motivo"));
        c.setModalidad(rs.getString("modalidad"));

        // Paciente, profesional y pago se piden a sus propios repositorios
        c.setPaciente(usuarioRepository.buscarPacientePorId(rs.getInt("id_paciente")).orElse(null));
        c.setProfesional(profesionalRepository.buscarPorId(rs.getInt("id_profesional")).orElse(null));

        int idPago = rs.getInt("id_pago");
        if (!rs.wasNull()) {
            c.setPago(pagoRepository.buscarPorId(idPago).orElse(null));
        }
        c.setConfirmacion(cargarConfirmacion(con, c.getIdCita()));
        return c;
    }

    private ConfirmacionCita cargarConfirmacion(Connection con, int idCita) throws SQLException {
        String sql = "SELECT id_confirmacion, fecha_confirmacion, codigo_confirmacion, estado "
                + "FROM confirmacion_cita WHERE id_cita = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                ConfirmacionCita conf = new ConfirmacionCita();
                conf.setIdConfirmacion(rs.getInt("id_confirmacion"));
                conf.setFechaConfirmacion(rs.getTimestamp("fecha_confirmacion").toLocalDateTime());
                conf.setCodigoConfirmacion(rs.getString("codigo_confirmacion"));
                conf.setEstado(rs.getString("estado"));
                conf.setNotificaciones(cargarNotificaciones(con, conf.getIdConfirmacion()));
                return conf;
            }
        }
    }

    private List<Notificacion> cargarNotificaciones(Connection con, int idConfirmacion) throws SQLException {
        String sql = "SELECT id_notificacion, mensaje, fecha_envio, tipo, estado FROM notificacion "
                + "WHERE id_confirmacion = ? ORDER BY id_notificacion";
        List<Notificacion> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConfirmacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Notificacion(
                            rs.getInt("id_notificacion"),
                            rs.getString("mensaje"),
                            rs.getTimestamp("fecha_envio").toLocalDateTime(),
                            rs.getString("tipo"),
                            rs.getString("estado")));
                }
            }
        }
        return lista;
    }
}
