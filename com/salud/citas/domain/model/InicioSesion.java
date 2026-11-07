package com.salud.citas.domain.model;

import java.time.LocalDateTime;

public class InicioSesion {
    private int idSesion;
    private LocalDateTime fechaInicio;
    private String dispositivo;
    private String estado;

    public InicioSesion() {
    }

    public InicioSesion(int idSesion, LocalDateTime fechaInicio, String dispositivo, String estado) {
        this.idSesion = idSesion;
        this.fechaInicio = fechaInicio;
        this.dispositivo = dispositivo;
        this.estado = estado;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
