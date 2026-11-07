package com.salud.citas.domain.model;

import java.time.LocalDate;

public class Cita {
    private int idCita;
    private LocalDate fecha;
    private String hora;
    private EstadoCita estado;
    private String motivo;
    private String modalidad;
    private Paciente paciente;
    private Profesional profesional;
    private Pago pago;
    private ConfirmacionCita confirmacion;

    public Cita() {
    }

    public Cita(int idCita, LocalDate fecha, String hora, EstadoCita estado, String motivo, String modalidad, Paciente paciente, Profesional profesional, Pago pago, ConfirmacionCita confirmacion) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.motivo = motivo;
        this.modalidad = modalidad;
        this.paciente = paciente;
        this.profesional = profesional;
        this.pago = pago;
        this.confirmacion = confirmacion;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public void setProfesional(Profesional profesional) {
        this.profesional = profesional;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public ConfirmacionCita getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(ConfirmacionCita confirmacion) {
        this.confirmacion = confirmacion;
    }
}
