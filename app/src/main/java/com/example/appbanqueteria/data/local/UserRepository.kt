package com.example.appbanqueteria.data.local

import com.example.appbanqueteria.data.local.entity.UsuarioEntity

object UserRepository {

    // Lista global en memoria usando los campos exactos de tu UsuarioEntity
    private val listaUsuarios = mutableListOf(
        UsuarioEntity(
            name = "Administrador",
            email = "admin@paolicias.cl",
            password = "123456"
        )
    )

    // Función para registrar un nuevo usuario
    fun registrarUsuario(nombre: String = "", correo: String, contrasena: String): Boolean {
        val correoLimpio = correo.trim().lowercase()
        val claveLimpia = contrasena.trim()

        // Comprobar si el correo ya existe
        if (listaUsuarios.any { it.email.lowercase() == correoLimpio }) {
            return false // Correo duplicado
        }

        val nuevoUsuario = UsuarioEntity(
            name = nombre.trim(),
            email = correoLimpio,
            password = claveLimpia
        )

        listaUsuarios.add(nuevoUsuario)
        return true
    }

    // Función para validar inicio de sesión
    fun validarLogin(correo: String, contrasena: String): Boolean {
        val correoLimpio = correo.trim().lowercase()
        val claveLimpia = contrasena.trim()

        return listaUsuarios.any {
            it.email.lowercase() == correoLimpio && it.password == claveLimpia
        }
    }
}