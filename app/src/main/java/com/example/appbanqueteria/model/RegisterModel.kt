package com.example.appbanqueteria.model

data class RegisterModel(
    val name: String = "",
    val email: String = "",
    val numberPhone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null
)