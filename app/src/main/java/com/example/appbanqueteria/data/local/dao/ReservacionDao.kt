package com.example.appbanqueteria.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appbanqueteria.data.local.entity.ReservacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarReservacion(reservacion: ReservacionEntity): Long

    @Update
    suspend fun actualizarReservacion(reservacion: ReservacionEntity)

    @Delete
    suspend fun eliminarReservacion(reservacion: ReservacionEntity)

    @Query("SELECT * FROM reservaciones ORDER BY id DESC")
    fun obtenerTodasLasReservaciones(): Flow<List<ReservacionEntity>>

    @Query("SELECT * FROM reservaciones WHERE correoCliente = :correo ORDER BY id DESC")
    fun obtenerReservacionesPorCorreo(correo: String): Flow<List<ReservacionEntity>>

    @Query("SELECT hora FROM reservaciones WHERE fecha = :fecha")
    suspend fun obtenerHorasReservadasPorFecha(fecha: String): List<String>
}