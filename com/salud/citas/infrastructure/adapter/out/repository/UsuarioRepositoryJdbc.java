package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.UsuarioRepositoryPort;
import com.salud.citas.domain.model.Paciente;
import com.salud.citas.domain.model.Usuario;
import com.salud.citas.infrastructure.config.ConexionBD;
import com.salud.citas.infrastructure.config.RepositorioException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UsuarioRepositoryJdbc implements UsuarioRepositoryPort {

    // Usuario + su paciente en una sola consulta (LEFT JOIN: un usuario podría no tener paciente)
    private static final String SELECT_USUARIO =
            "SELECT u.id_usuario, u.nombre, u.apellido, u.correo, u.contrasena, u.telefono, "
                    + "p.documento, p.fecha_nacimiento, p.direccion, p.eps "
                    + "FROM usuario u LEFT JOIN paciente p ON p.id_paciente = u.id_usuario ";

    @Override
    public Usuario guardar(Usuario usuario) {
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false); // usuario y paciente se guardan juntos o no se guarda nada
            try {
                if (usuario.getIdUsuario() == 0) {
                    insertar(con, usuario);
                } else {
                    actualizar(con, usuario);
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al guardar el usuario", e);
        }
        return usuario;
    }

    private void insertar(Connection con, Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, apellido, correo, contrasena, telefono) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getTelefono());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setIdUsuario(keys.getInt(1));
                }
            }
        }
        Paciente p = u.getPaciente();
        if (p != null) {
            // Igual que en memoria: el paciente comparte el número del usuario
            p.setIdPaciente(u.getIdUsuario());
            String sqlPaciente = "INSERT INTO paciente (id_paciente, documento, fecha_nacimiento, direccion, eps) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlPaciente)) {
                ps.setInt(1, p.getIdPaciente());
                ps.setString(2, p.getDocumento());
                ps.setDate(3, Date.valueOf(p.getFechaNacimiento()));
                ps.setString(4, p.getDireccion());
                ps.setString(5, p.getEps());
                ps.executeUpdate();
            }
        }
    }

    private void actualizar(Connection con, Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, apellido = ?, correo = ?, contrasena = ?, telefono = ? "
                + "WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getTelefono());
            ps.setInt(6, u.getIdUsuario());
            ps.executeUpdate();
        }
        Paciente p = u.getPaciente();
        if (p != null) {
            String sqlPaciente = "UPDATE paciente SET documento = ?, fecha_nacimiento = ?, direccion = ?, eps = ? "
                    + "WHERE id_paciente = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlPaciente)) {
                ps.setString(1, p.getDocumento());
                ps.setDate(2, Date.valueOf(p.getFechaNacimiento()));
                ps.setString(3, p.getDireccion());
                ps.setString(4, p.getEps());
                ps.setInt(5, u.getIdUsuario());
                ps.executeUpdate();
            }
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        return buscarUno(SELECT_USUARIO + "WHERE u.id_usuario = ?", ps -> ps.setInt(1, idUsuario),
                "Error al buscar el usuario");
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        // La comparación ignora mayúsculas/minúsculas por la colación utf8mb4_general_ci
        return buscarUno(SELECT_USUARIO + "WHERE u.correo = ?", ps -> ps.setString(1, correo),
                "Error al buscar el usuario por correo");
    }

    @Override
    public boolean existePorCorreo(String correo) {
        String sql = "SELECT 1 FROM usuario WHERE correo = ? LIMIT 1";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al verificar el correo", e);
        }
    }

    @Override
    public Optional<Paciente> buscarPacientePorId(int idPaciente) {
        String sql = "SELECT id_paciente, documento, fecha_nacimiento, direccion, eps FROM paciente WHERE id_paciente = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Paciente(
                            rs.getInt("id_paciente"),
                            rs.getString("documento"),
                            rs.getDate("fecha_nacimiento").toLocalDate(),
                            rs.getString("direccion"),
                            rs.getString("eps")));
                }
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al buscar el paciente", e);
        }
        return Optional.empty();
    }

    // ---------- apoyo ----------

    /** Permite pasarle al método el "rellenar parámetros" como una expresión lambda. */
    @FunctionalInterface
    private interface Parametros {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    private Optional<Usuario> buscarUno(String sql, Parametros parametros, String errorMensaje) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            parametros.aplicar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositorioException(errorMensaje, e);
        }
        return Optional.empty();
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_usuario");
        Paciente paciente = null;
        Date fechaNacimiento = rs.getDate("fecha_nacimiento");
        if (fechaNacimiento != null) { // null = este usuario no tiene fila en paciente
            paciente = new Paciente(id,
                    rs.getString("documento"),
                    fechaNacimiento.toLocalDate(),
                    rs.getString("direccion"),
                    rs.getString("eps"));
        }
        return new Usuario(id,
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("correo"),
                rs.getString("contrasena"),
                rs.getString("telefono"),
                paciente);
    }
}
