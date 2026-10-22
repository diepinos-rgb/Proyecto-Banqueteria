package com.example.appbanqueteria.data.local

import com.example.appbanqueteria.R
import com.example.appbanqueteria.model.Servicio

object CateringRepository {
    fun getCateringItems(): List<Servicio> {
        return listOf(
            Servicio(
                id = "1",
                titulo = "Cocktail Ejecutivo & Coctelería Fina",
                descripcion = "Selección de 12 bocados fríos y calientes por persona, tabla de quesos madurados y servicio de barra abierta con tragos de autor.",
                minPersonas = 20f,
                maxPersonas = 100f,
                precioPorPersona = 14900,
                etiquetas = listOf("Popular", "Barra Abierta"),
                alergenos = "Gluten, Lácteos, Mariscos",
                rangoPersonas = "20–100 personas",
                imagenRes = R.drawable.catering_111
            ),
            Servicio(
                id = "2",
                titulo = "Banquete Formal de 3 Tiempos",
                descripcion = "Entrada, plato principal con guarnición y buffet de postres individuales. Incluye mantelería, vajilla fina y garzones.",
                minPersonas = 30f,
                maxPersonas = 100f,
                precioPorPersona = 22500,
                etiquetas = listOf("Formal", "Servicio Completo"),
                alergenos = "Gluten, Lácteos, Huevo",
                rangoPersonas = "30–100 personas",
                imagenRes = R.drawable.catering_2
            )
        )
    }
}