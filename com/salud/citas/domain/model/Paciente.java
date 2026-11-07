package com.salud.citas.domain.model;

import java.time.LocalDate;

public class Paciente {
    private int idPaciente;
    private String documento;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String eps;

    public Paciente() {
    }

    public Paciente(int idPaciente, String documento, LocalDate fechaNacimiento, String direccion, String eps) {
        this.idPaciente = idPaciente;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.eps = eps;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }
}
