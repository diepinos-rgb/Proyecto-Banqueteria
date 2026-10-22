package com.example.appbanqueteria.model

data class PasteleriaItem(
    val id: Int,
    val title: String,
    val description: String,
    val pricePerPerson: Int,
    val minPersons: Int,
    val maxPersons: Int,
    val tags: List<String>,
    val allergens: String,
    val imageRes: Int
)
