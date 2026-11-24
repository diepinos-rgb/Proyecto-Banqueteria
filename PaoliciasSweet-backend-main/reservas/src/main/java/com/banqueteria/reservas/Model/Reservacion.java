package com.banqueteria.reservas.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "reservaciones")
public class Reservacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoCliente = "Persona natural";
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private String servicioReservado;
    private String fecha;
    private String hora;
    private Integer personas;
    private Integer totalEstimado;
    private Integer porcentajeAbono = 50;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    private Integer costoInsumos = 0;
    private Integer trabajadoresNecesarios = 0;
    private String rutaComprobante;

    public Reservacion() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getCorreoCliente() { return correoCliente; }
    public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public String getServicioReservado() { return servicioReservado; }
    public void setServicioReservado(String servicioReservado) { this.servicioReservado = servicioReservado; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public Integer getPersonas() { return personas; }
    public void setPersonas(Integer personas) { this.personas = personas; }

    public Integer getTotalEstimado() { return totalEstimado; }
    public void setTotalEstimado(Integer totalEstimado) { this.totalEstimado = totalEstimado; }

    public Integer getPorcentajeAbono() { return porcentajeAbono; }
    public void setPorcentajeAbono(Integer porcentajeAbono) { this.porcentajeAbono = porcentajeAbono; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getCostoInsumos() { return costoInsumos; }
    public void setCostoInsumos(Integer costoInsumos) { this.costoInsumos = costoInsumos; }

    public Integer getTrabajadoresNecesarios() { return trabajadoresNecesarios; }
    public void setTrabajadoresNecesarios(Integer trabajadoresNecesarios) { this.trabajadoresNecesarios = trabajadoresNecesarios; }

    public String getRutaComprobante() { return rutaComprobante; }
    public void setRutaComprobante(String rutaComprobante) { this.rutaComprobante = rutaComprobante; }
}