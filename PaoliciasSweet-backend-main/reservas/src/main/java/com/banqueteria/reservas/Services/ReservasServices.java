package com.banqueteria.reservas.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banqueteria.reservas.Model.Reservacion;
import com.banqueteria.reservas.Repository.ReservacionRepository;

@Service
public class ReservasServices {

    @Autowired
    private ReservacionRepository reservacionRepository;

    // 1. Crear o agendar una nueva reservación
    public Reservacion crearReservacion(Reservacion reservacion) {
        // Aquí podríamos agregar validaciones a futuro, por ejemplo, 
        // revisar si la fecha y hora ya están ocupadas.
        return reservacionRepository.save(reservacion);
    }

    // 2. Obtener todas las reservaciones (Ideal para el Panel Admin)
    public List<Reservacion> obtenerTodas() {
        return reservacionRepository.findAll();
    }

    // 3. Obtener reservaciones de un cliente específico (Historial en la app)
    public List<Reservacion> obtenerPorCorreoCliente(String correoCliente) {
        return reservacionRepository.findByCorreoCliente(correoCliente);
    }

    // 4. Obtener una reservación específica por su ID
    public Optional<Reservacion> obtenerPorId(Long id) {
        return reservacionRepository.findById(id);
    }

    // 5. Eliminar o cancelar una reservación
    public void eliminarReservacion(Long id) {
        reservacionRepository.deleteById(id);
    }

    public Optional<Reservacion> actualizarReservacion(Long id, Reservacion detalles) {
        return reservacionRepository.findById(id).map(reserva -> {
            reserva.setNombreCliente(detalles.getNombreCliente());
            reserva.setCorreoCliente(detalles.getCorreoCliente());
            reserva.setTelefonoCliente(detalles.getTelefonoCliente());
            reserva.setTipoCliente(detalles.getTipoCliente());
            reserva.setServicioReservado(detalles.getServicioReservado());
            reserva.setFecha(detalles.getFecha());
            reserva.setHora(detalles.getHora());
            reserva.setPersonas(detalles.getPersonas());
            reserva.setCostoInsumos(detalles.getCostoInsumos());
            reserva.setTotalEstimado(detalles.getTotalEstimado());
            reserva.setPorcentajeAbono(detalles.getPorcentajeAbono());
            reserva.setTrabajadoresNecesarios(detalles.getTrabajadoresNecesarios());
            reserva.setObservaciones(detalles.getObservaciones());
            reserva.setRutaComprobante(detalles.getRutaComprobante());
            return reservacionRepository.save(reserva);
        });
    }
}