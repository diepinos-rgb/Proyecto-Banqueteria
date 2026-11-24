package com.example.appbanqueteria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appbanqueteria.data.local.entity.ReservacionEntity
import com.example.appbanqueteria.data.remote.api.RetrofitClient
import com.example.appbanqueteria.data.remote.dto.toDto
import com.example.appbanqueteria.data.remote.dto.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservacionViewModel : ViewModel() {

    private val _reservaciones = MutableStateFlow<List<ReservacionEntity>>(emptyList())
    val reservaciones: StateFlow<List<ReservacionEntity>> = _reservaciones.asStateFlow()

    private val _horasOcupadas = MutableStateFlow<List<String>>(emptyList())
    val horasOcupadas: StateFlow<List<String>> = _horasOcupadas.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // GET: Obtener todas las reservaciones desde Spring Boot
    fun cargarReservaciones() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val listaRemota = RetrofitClient.reservasApi.obtenerTodas()
                _reservaciones.value = listaRemota.map { it.toEntity() }
            } catch (e: Exception) {
                _error.value = "Error de conexión con el servidor: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // GET: Consultar horas ocupadas para una fecha específica
    fun cargarHorasOcupadasPorFecha(fecha: String) {
        if (fecha.isBlank()) {
            _horasOcupadas.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val todas = RetrofitClient.reservasApi.obtenerTodas()
                _horasOcupadas.value = todas
                    .filter { it.fecha == fecha }
                    .map { it.hora }
            } catch (_: Exception) {
                _horasOcupadas.value = emptyList()
            }
        }
    }

    // POST: Crear una nueva reservación en el servidor
    fun crearReservacion(
        reservacion: ReservacionEntity,
        onExito: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val dtoParaEnviar = reservacion.copy(id = 0).toDto()
                val creada = RetrofitClient.reservasApi.crearReservacion(dtoParaEnviar)
                cargarReservaciones()
                onExito(creada.id ?: 0L)
            } catch (e: Exception) {
                onError("No se pudo guardar en el servidor: ${e.localizedMessage}")
            } finally {
                _cargando.value = false
            }
        }
    }

    // PUT: Actualizar trabajadores e insumos de una reservación existente
    fun actualizarReservacion(
        reservacion: ReservacionEntity,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val idRemoto = reservacion.id.toLong()
                RetrofitClient.reservasApi.actualizarReservacion(idRemoto, reservacion.toDto())
                cargarReservaciones()
                onExito()
            } catch (e: Exception) {
                onError("Error al actualizar reserva: ${e.localizedMessage}")
            } finally {
                _cargando.value = false
            }
        }
    }

    // DELETE: Eliminar una reservación del servidor
    fun eliminarReservacion(
        id: Int,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = RetrofitClient.reservasApi.eliminarReservacion(id.toLong())
                if (response.isSuccessful) {
                    cargarReservaciones()
                    onExito()
                } else {
                    onError("El servidor rechazó la eliminación (HTTP ${response.code()})")
                }
            } catch (e: Exception) {
                onError("Error de red al eliminar: ${e.localizedMessage}")
            } finally {
                _cargando.value = false
            }
        }
    }
}