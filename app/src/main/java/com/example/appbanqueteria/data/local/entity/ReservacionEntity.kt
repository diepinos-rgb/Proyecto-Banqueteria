package com.example.appbanqueteria.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservaciones")
data class ReservacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID correlativo automático (1, 2, 3...)
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