package com.example.appbanqueteria.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appbanqueteria.data.database.AppDatabase
import com.example.appbanqueteria.data.local.entity.UsuarioEntity
import com.example.appbanqueteria.model.RegisterModel
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var uiState by mutableStateOf(RegisterModel())
        private set

    fun onNameChanged(name: String) { uiState = uiState.copy(name = name, errorMessage = null) }
    fun onEmailChanged(email: String) { uiState = uiState.copy(email = email, errorMessage = null) }
    fun onPasswordChanged(password: String) { uiState = uiState.copy(password = password, errorMessage = null) }
    fun onConfirmPasswordChanged(confirmPassword: String) { uiState = uiState.copy(confirmPassword = confirmPassword, errorMessage = null) }

    fun onRegisterClick(context: Context, onSuccess: () -> Unit) {
        val state = uiState

        // 1. Validar campos vacíos
        if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Por favor completa todos los campos.")
            return
        }

        // 2. Validar longitud del nombre (mínimo 3 caracteres)
        if (state.name.trim().length < 3) {
            uiState = uiState.copy(errorMessage = "El nombre debe tener al menos 3 caracteres.")
            return
        }

        // 3. Validar que contenga '@'
        if (!state.email.contains("@")) {
            uiState = uiState.copy(errorMessage = "Correo inválido. Debe contener un '@'.")
            return
        }

        // 4. Validar dominio (.com o .cl)
        val emailLowerCase = state.email.lowercase().trim()
        if (!emailLowerCase.endsWith(".com") && !emailLowerCase.endsWith(".cl")) {
            uiState = uiState.copy(errorMessage = "Correo inválido. Debe terminar en '.com' o '.cl'.")
            return
        }

        // 5. Validar longitud de contraseña (mínimo 6 caracteres)
        if (state.password.length < 6) {
            uiState = uiState.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres.")
            return
        }

        // 6. Validar coincidencia de contraseñas
        if (state.password != state.confirmPassword) {
            uiState = uiState.copy(errorMessage = "Las contraseñas no coinciden.")
            return
        }

        // 7. Registro en la base de datos Room
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val existingUser = db.usuarioDao().getUserByEmail(state.email.trim())

            if (existingUser != null) {
                uiState = uiState.copy(errorMessage = "El correo ya está registrado.")
            } else {
                val newUser = UsuarioEntity(
                    name = state.name.trim(),
                    email = state.email.trim(),
                    password = state.password
                )
                db.usuarioDao().registerUser(newUser)
                uiState = uiState.copy(errorMessage = null)
                onSuccess()
            }
        }
    }
}