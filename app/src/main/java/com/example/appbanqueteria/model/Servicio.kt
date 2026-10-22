package com.example.appbanqueteria.model

data class Servicio(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val minPersonas: Float,
    val maxPersonas: Float,
    val precioPorPersona: Int,
    val etiquetas: List<String>,
    val alergenos: String,
    val rangoPersonas: String,
    val imagenRes: Int
)