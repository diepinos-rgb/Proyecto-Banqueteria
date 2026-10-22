package com.example.appbanqueteria.data.local

import com.example.appbanqueteria.R
import com.example.appbanqueteria.model.PasteleriaItem

object PasteleriaRepository {
    fun getPasteleriaItems(): List<PasteleriaItem> {
        return listOf(
            PasteleriaItem(
                id = 1,
                title = "Torta Belga de Chocolate Oscuro",
                description = "Tres capas de bizcocho húmedo con ganache de chocolate belga 70%, relleno de crema de cacao y decoración artesanal. Personalizable con mensaje y diseño.",
                pricePerPerson = 2800,
                minPersons = 10,
                maxPersons = 60,
                tags = listOf("Personalizable", "Sin gluten disponible"),
                allergens = "Gluten, Lácteos, Huevo",
                imageRes = R.drawable.torta_belga
            ),
            PasteleriaItem(
                id = 2,
                title = "Torta Tres Leches con Frutos Rojos",
                description = "Clásica torta tres leches empapada en mezcla de leches, coronada con crema chantilly artesanal y frutas frescas de temporada.",
                pricePerPerson = 2900,
                minPersons = 10,
                maxPersons = 60,
                tags = listOf("Temporada", "Premium"),
                allergens = "Gluten, Lácteos, Huevo",
                imageRes = R.drawable.torta_3leches
            ),
            PasteleriaItem(
                id = 3,
                title = "Box de Pastelería Fina",
                description = "Surtido de 24 piezas artesanales: macarons, financiers, trufas de chocolate y petit fours de temporada. Presentación en caja regalo.",
                pricePerPerson = 920,
                minPersons = 10,
                maxPersons = 100,
                tags = listOf("Regalo corporativo", "Personalizable"),
                allergens = "Gluten, Lácteos, Huevo, Frutos secos",
                imageRes = R.drawable.caja_pasteleria
            )
        )
    }
}