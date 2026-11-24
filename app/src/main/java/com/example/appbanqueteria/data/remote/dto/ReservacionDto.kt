package com.example.appbanqueteria.data.remote.dto

import com.example.appbanqueteria.data.local.entity.ReservacionEntity

data class ReservacionDto(
    val id: Long? = null, // Se envía null en POST para que MySQL genere el ID automático
    val tipoCliente: String = "Persona natural",
    val nombreCliente: String,
    val correoCliente: String,
    val telefonoCliente: String,
    val servicioReservado: String,
    val fecha: String,
    val hora: String,
    val personas: Int,
    val totalEstimado: Int,
    val porcentajeAbono: Int = 50,
    val observaciones: String = "",
    val costoInsumos: Int = 0,
    val trabajadoresNecesarios: Int = 0,
    val rutaComprobante: String = ""
)

// Convierte tu entidad local de Room al DTO que viaja por Retrofit
fun ReservacionEntity.toDto(): ReservacionDto = ReservacionDto(
    id = if (this.id == 0) null else this.id.toLong(),
    tipoCliente = this.tipoCliente,
    nombreCliente = this.nombreCliente,
    correoCliente = this.correoCliente,
    telefonoCliente = this.telefonoCliente,
    servicioReservado = this.servicioReservado,
    fecha = this.fecha,
    hora = this.hora,
    personas = this.personas,
    totalEstimado = this.totalEstimado,
    porcentajeAbono = this.porcentajeAbono,
    observaciones = this.observaciones,
    costoInsumos = this.costoInsumos,
    trabajadoresNecesarios = this.trabajadoresNecesarios,
    rutaComprobante = this.rutaComprobante
)

// Convierte la respuesta JSON del servidor a tu entidad local de Room
fun ReservacionDto.toEntity(): ReservacionEntity = ReservacionEntity(
    id = this.id?.toInt() ?: 0,
    tipoCliente = this.tipoCliente,
    nombreCliente = this.nombreCliente,
    correoCliente = this.correoCliente,
    telefonoCliente = this.telefonoCliente,
    servicioReservado = this.servicioReservado,
    fecha = this.fecha,
    hora = this.hora,
    personas = this.personas,
    totalEstimado = this.totalEstimado,
    porcentajeAbono = this.porcentajeAbono,
    observaciones = this.observaciones,
    costoInsumos = this.costoInsumos,
    trabajadoresNecesarios = this.trabajadoresNecesarios,
    rutaComprobante = this.rutaComprobante
)