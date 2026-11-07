package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.EspecialidadRepositoryPort;
import com.salud.citas.domain.model.Especialidad;
import com.salud.citas.infrastructure.config.ConexionBD;
import com.salud.citas.infrastructure.config.RepositorioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadRepositoryJdbc implements EspecialidadRepositoryPort {

    @Override
    public List<Especialidad> listarTodas() {
        String sql = "SELECT id_especialidad, nombre, descripcion FROM especialidad ORDER BY id_especialidad";
        List<Especialidad> resultado = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new Especialidad(
                        rs.getInt("id_especialidad"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")));
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al listar las especialidades", e);
        }
        return resultado;
    }
}
