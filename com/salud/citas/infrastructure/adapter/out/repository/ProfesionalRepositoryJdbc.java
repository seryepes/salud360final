package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.ProfesionalRepositoryPort;
import com.salud.citas.domain.model.Especialidad;
import com.salud.citas.domain.model.Profesional;
import com.salud.citas.infrastructure.config.ConexionBD;
import com.salud.citas.infrastructure.config.RepositorioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfesionalRepositoryJdbc implements ProfesionalRepositoryPort {

    private static final String COLUMNAS =
            "p.id_profesional, p.nombre, p.apellido, p.correo, p.telefono, p.documento";

    @Override
    public Optional<Profesional> buscarPorId(int idProfesional) {
        String sql = "SELECT " + COLUMNAS + " FROM profesional p WHERE p.id_profesional = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProfesional);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(con, rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al buscar el profesional", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Profesional> buscarPorEspecialidad(int idEspecialidad) {
        String sql = "SELECT " + COLUMNAS + " FROM profesional p "
                + "JOIN profesional_especialidad pe ON pe.id_profesional = p.id_profesional "
                + "WHERE pe.id_especialidad = ? ORDER BY p.id_profesional";
        List<Profesional> resultado = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(con, rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al listar los profesionales", e);
        }
        return resultado;
    }

    /** Convierte la fila en Profesional y le carga todas sus especialidades. */
    private Profesional mapear(Connection con, ResultSet rs) throws SQLException {
        Profesional p = new Profesional();
        p.setIdProfesional(rs.getInt("id_profesional"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setCorreo(rs.getString("correo"));
        p.setTelefono(rs.getString("telefono"));
        p.setDocumento(rs.getString("documento"));
        p.setEspecialidades(cargarEspecialidades(con, p.getIdProfesional()));
        return p;
    }

    private List<Especialidad> cargarEspecialidades(Connection con, int idProfesional) throws SQLException {
        String sql = "SELECT e.id_especialidad, e.nombre, e.descripcion FROM especialidad e "
                + "JOIN profesional_especialidad pe ON pe.id_especialidad = e.id_especialidad "
                + "WHERE pe.id_profesional = ? ORDER BY e.id_especialidad";
        List<Especialidad> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProfesional);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Especialidad(
                            rs.getInt("id_especialidad"),
                            rs.getString("nombre"),
                            rs.getString("descripcion")));
                }
            }
        }
        return lista;
    }
}
