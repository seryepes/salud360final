package com.salud.citas.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class ConfirmacionCita {
    private int idConfirmacion;
    private LocalDateTime fechaConfirmacion;
    private String codigoConfirmacion;
    private String estado;
    private List<Notificacion> notificaciones;

    public ConfirmacionCita() {
    }

    public ConfirmacionCita(int idConfirmacion, LocalDateTime fechaConfirmacion, String codigoConfirmacion, String estado, List<Notificacion> notificaciones) {
        this.idConfirmacion = idConfirmacion;
        this.fechaConfirmacion = fechaConfirmacion;
        this.codigoConfirmacion = codigoConfirmacion;
        this.estado = estado;
        this.notificaciones = notificaciones;
    }

    public int getIdConfirmacion() {
        return idConfirmacion;
    }

    public void setIdConfirmacion(int idConfirmacion) {
        this.idConfirmacion = idConfirmacion;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public String getCodigoConfirmacion() {
        return codigoConfirmacion;
    }

    public void setCodigoConfirmacion(String codigoConfirmacion) {
        this.codigoConfirmacion = codigoConfirmacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }
}
