package com.example.appbanqueteria.data.remote.api

import com.example.appbanqueteria.data.remote.dto.ReservacionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReservasApi {

    @POST("api/reservaciones")
    suspend fun crearReservacion(@Body reservacion: ReservacionDto): ReservacionDto

    @GET("api/reservaciones")
    suspend fun obtenerTodas(): List<ReservacionDto>

    @GET("api/reservaciones/cliente/{correo}")
    suspend fun obtenerPorCorreo(@Path("correo") correo: String): List<ReservacionDto>

    @GET("api/reservaciones/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): ReservacionDto

    @PUT("api/reservaciones/{id}")
    suspend fun actualizarReservacion(
        @Path("id") id: Long,
        @Body reservacion: ReservacionDto
    ): ReservacionDto

    @DELETE("api/reservaciones/{id}")
    suspend fun eliminarReservacion(@Path("id") id: Long): Response<Unit>
}