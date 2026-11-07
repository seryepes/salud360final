package com.salud.citas.domain.model;

import java.time.LocalDateTime;

public class Registro {
    private int idRegistro;
    private LocalDateTime fechaRegistro;
    private String tipoUsuario;
    private String estado;

    public Registro() {
    }

    public Registro(int idRegistro, LocalDateTime fechaRegistro, String tipoUsuario, String estado) {
        this.idRegistro = idRegistro;
        this.fechaRegistro = fechaRegistro;
        this.tipoUsuario = tipoUsuario;
        this.estado = estado;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
