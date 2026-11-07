package com.salud.citas.infrastructure.adapter.out.repository;

import com.salud.citas.application.port.out.PagoRepositoryPort;
import com.salud.citas.domain.model.EstadoPago;
import com.salud.citas.domain.model.Pago;
import com.salud.citas.infrastructure.config.ConexionBD;
import com.salud.citas.infrastructure.config.RepositorioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public class PagoRepositoryJdbc implements PagoRepositoryPort {

    @Override
    public Pago guardar(Pago pago) {
        try (Connection con = ConexionBD.obtener()) {
            if (pago.getIdPago() == 0) {
                String sql = "INSERT INTO pago (monto, metodo_pago, fecha_pago, estado_pago) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    llenar(ps, pago);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            pago.setIdPago(keys.getInt(1));
                        }
                    }
                }
            } else {
                String sql = "UPDATE pago SET monto = ?, metodo_pago = ?, fecha_pago = ?, estado_pago = ? WHERE id_pago = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    llenar(ps, pago);
                    ps.setInt(5, pago.getIdPago());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al guardar el pago", e);
        }
        return pago;
    }

    @Override
    public Optional<Pago> buscarPorId(int idPago) {
        String sql = "SELECT id_pago, monto, metodo_pago, fecha_pago, estado_pago FROM pago WHERE id_pago = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Pago(
                            rs.getInt("id_pago"),
                            rs.getBigDecimal("monto").doubleValue(),
                            rs.getString("metodo_pago"),
                            rs.getTimestamp("fecha_pago").toLocalDateTime(),
                            EstadoPago.valueOf(rs.getString("estado_pago"))));
                }
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al buscar el pago", e);
        }
        return Optional.empty();
    }

    private void llenar(PreparedStatement ps, Pago pago) throws SQLException {
        ps.setBigDecimal(1, BigDecimal.valueOf(pago.getMonto()));
        ps.setString(2, pago.getMetodoPago());
        ps.setTimestamp(3, Timestamp.valueOf(pago.getFechaPago()));
        ps.setString(4, pago.getEstadoPago().name());
    }
}
