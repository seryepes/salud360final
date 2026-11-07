package com.salud.citas.domain.model;

import java.time.LocalDateTime;

public class Notificacion {
    private int idNotificacion;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private String tipo;
    private String estado;

    public Notificacion() {
    }

    public Notificacion(int idNotificacion, String mensaje, LocalDateTime fechaEnvio, String tipo, String estado) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
        this.tipo = tipo;
        this.estado = estado;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
